#!/usr/bin/env bash

#wget http://ftp.gnu.org/gnu/libtool/libtool-2.2.6a.tar.gz
#./configure
#make
#make install

#yum install gcc-c++ libstdc++-devel -y
#echo "/usr/local/lib" > /etc/ld.so.conf.d/usr_local_lib.conf
#/sbin/ldconfig  


for arch in armeabi-v7a arm64-v8a x86 x86_64
do
    bash build_fdkaac.sh $arch
done
