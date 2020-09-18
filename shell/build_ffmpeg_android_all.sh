#!/bin/bash

#你自己的NDK路径.
export NDK=/root/android/ndk/android-ndk-r20b
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/linux-x86_64


FDK_INCLUDE=/root/shell/libs/libfdk-aac/arm64-v8a/include
FDK_LIB=/root/shell/libs/libfdk-aac/arm64-v8a/lib
X264_INCLUDE=/root/shell/libs/libx264/arm64-v8a/include
X264_LIB=/root/shell/libs/libx264/arm64-v8a/lib


function build_android
{

echo "Compiling FFmpeg for $CPU"

./configure \
--prefix=$PREFIX \
--enable-neon  \
--enable-hwaccels  \
--enable-gpl   \
--enable-postproc \
--enable-shared \
--disable-debug \
--enable-jni \
--enable-mediacodec \
--enable-decoder=h264_mediacodec \
--extra-cflags="-I$X264_INCLUDE -I$FDK_INCLUDE " \
--extra-ldflags="-L$X264_LIB -L$FDK_LIB" \
--enable-nonfree \
--disable-static \
--enable-version3 \
--enable-pthreads \
--enable-small \
--disable-vda \
--disable-iconv \
--enable-libx264 \
--enable-yasm \
--enable-libfdk_aac \
--enable-encoder=libx264 \
--enable-encoder=mpeg4 \
--enable-encoder=libfdk_aac \
--enable-encoder=mjpeg \
--enable-encoder=png \
--enable-nonfree \
--enable-muxers \
--enable-muxer=mov \
--enable-muxer=mp4 \
--enable-muxer=h264 \
--enable-muxer=avi \
--enable-decoder=aac \
--enable-decoder=aac_latm \
--enable-decoder=h264 \
--enable-decoder=mpeg4 \
--enable-decoder=mjpeg \
--enable-decoder=png \
--enable-demuxer=image2 \
--enable-demuxer=h264 \
--enable-demuxer=aac \
--enable-demuxer=avi \
--enable-demuxer=mpc \
--enable-demuxer=mpegts \
--enable-demuxer=mov \
--enable-parser=aac \
--enable-parser=ac3 \
--enable-parser=h264 \
--enable-protocols \
--enable-zlib \
--enable-avfilter \
--enable-avresample \
--disable-outdevs \
--disable-ffserver \
--disable-stripping \
--enable-ffmpeg \
--disable-ffplay \
--disable-ffprobe \
--disable-avdevice \
--disable-doc \
--disable-symver \
--cross-prefix=$CROSS_PREFIX \
--target-os=android \
--arch=$ARCH \
--cpu=$CPU \
--cc=$CC \
--cxx=$CXX \
--enable-cross-compile \
--sysroot=$SYSROOT \
--extra-cflags="-Os -fpic $OPTIMIZE_CFLAGS" \
--extra-ldflags="$ADDI_LDFLAGS"


make clean
make
make install
echo "The Compilation of FFmpeg for $CPU is completed"

}

#armv8-a

ARCH=arm64
CPU=armv8-a
API=21
CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot
CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-
PREFIX=$(pwd)/android/$CPU
OPTIMIZE_CFLAGS="-march=$CPU"

build_android

#armv7-a

ARCH=arm
CPU=armv7-a
API=16
CC=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang
CXX=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang++
SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot
CROSS_PREFIX=$TOOLCHAIN/bin/arm-linux-androideabi-
PREFIX=$(pwd)/android/$CPU
OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=vfp -marm -march=$CPU "

#build_android

#x86

ARCH=x86
API=16
CPU=x86

CC=$TOOLCHAIN/bin/i686-linux-android$API-clang

CXX=$TOOLCHAIN/bin/i686-linux-android$API-clang++

SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot

CROSS_PREFIX=$TOOLCHAIN/bin/i686-linux-android-

PREFIX=$(pwd)/android/$CPU

OPTIMIZE_CFLAGS="-march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32"

#build_android

#x86_64

ARCH=x86_64
API=16
CPU=x86-64

CC=$TOOLCHAIN/bin/x86_64-linux-android$API-clang

CXX=$TOOLCHAIN/bin/x86_64-linux-android$API-clang++

SYSROOT=$NDK/toolchains/llvm/prebuilt/linux-x86_64/sysroot

CROSS_PREFIX=$TOOLCHAIN/bin/x86_64-linux-android-

PREFIX=$(pwd)/android/$CPU

OPTIMIZE_CFLAGS="-march=$CPU -msse4.2 -mpopcnt -m64 -mtune=intel"

#build_android