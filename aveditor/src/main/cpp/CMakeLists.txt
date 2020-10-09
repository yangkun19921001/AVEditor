cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

#JNI 路径
set(JNI_PATH ${CMAKE_SOURCE_DIR})
#FFMPEG 文件路径
set(FFMPEG_PATH ${CMAKE_SOURCE_DIR}/../../../../thirdparties/ffmpeg)
#librtmp 文件路径
set(LIBRTMP_PATH ${CMAKE_SOURCE_DIR}/../../../../thirdparties/librtmp)
#soundtouch 文件路径
set(SOUNDTOUCH_PATH ${CMAKE_SOURCE_DIR}/soundtouch)

# 其它动态库路径
# 动态库 路径
set(SHARED_PATH ${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI})
# librtmp 静态库 路径
set(LIBRTMP_STATIC_PATH ${CMAKE_SOURCE_DIR}/../../../../thirdparties/librtmp/libs/${CMAKE_ANDROID_ARCH_ABI})


#xlog header 路径
set(XLOG_PATH ${CMAKE_SOURCE_DIR}/xlog)
#Base
set(BASE_PATH ${CMAKE_SOURCE_DIR}/base)
#editor
set(EDITOR_PATH ${CMAKE_SOURCE_DIR}/editor)

message(FFMPEG_STATIC_PATH------>${FFMPEG_STATIC_PATH})
message(FDKAAC_STATIC_PATH------>${FDKAAC_STATIC_PATH})
message(X264_STATIC_PATH------>${X264_STATIC_PATH})

#-------------------------------------------------找文件----------------------------------------------
#找文件

#jni cpp
FILE(GLOB JNI_ALL_CPP ${JNI_PATH}/*.cpp)
FILE(GLOB JNI_ALL_H ${JNI_PATH}/*.h)
#解封装
FILE(GLOB DEMUX_ALL_CPP ${JNI_PATH}/demux/*.cpp)
FILE(GLOB DEMUX_ALL_H ${JNI_PATH}/demux/*.h)
#解码
FILE(GLOB DECODE_ALL_CPP ${JNI_PATH}/decode/*.cpp)
FILE(GLOB DECODE_ALL_H ${JNI_PATH}/decode/*.h)
#音频重采样
FILE(GLOB RESAMPLE_ALL_CPP ${JNI_PATH}/resample/*.cpp)
FILE(GLOB RESAMPLE_ALL_H ${JNI_PATH}/resample/*.h)
#音频渲染
FILE(GLOB AUDIO_ALL_CPP ${JNI_PATH}/audio/*.cpp)
FILE(GLOB AUDIO_ALL_H ${JNI_PATH}/audio/*.h)
#视频渲染
FILE(GLOB VIDEO_ALL_CPP ${JNI_PATH}/video/*.cpp)
FILE(GLOB VIDEO_ALL_H ${JNI_PATH}/video/*.h)
#构建模块
FILE(GLOB BUILDER_ALL_CPP ${JNI_PATH}/builder/*.cpp)
FILE(GLOB BUILDER_ALL_H ${JNI_PATH}/builder/*.h)
#base
FILE(GLOB BASE_ALL_CPP ${BASE_PATH}/*.cpp)
FILE(GLOB BASE_ALL_H ${BASE_PATH}/*.)
FILE(GLOB BASE_DATA_ALL_CPP ${BASE_PATH}/data/*.cpp)
FILE(GLOB BASE_DATA_ALL_H ${BASE_PATH}/data/*.h)
#播放模块
FILE(GLOB JNI_PATH_ALL_CPP ${JNI_PATH}/play/*.cpp)
FILE(GLOB JNI_PATH_ALL_H ${JNI_PATH}/play/*.h)
#回调模块
FILE(GLOB CALLBACK_PATH_ALL_CPP ${JNI_PATH}/callback/*.cpp)
FILE(GLOB CALLBACK_PATH_ALL_H ${JNI_PATH}/callback/*.h)
#utils 模块
FILE(GLOB UTILS_PATH_ALL_CPP ${JNI_PATH}/utils/*.cpp)
FILE(GLOB UTILS_PATH_ALL_H ${JNI_PATH}/utils/*.h)
#数据实体 模块
FILE(GLOB ENTITY_PATH_ALL_CPP ${JNI_PATH}/entity/*.cpp)
FILE(GLOB ENTITY_PATH_ALL_H ${JNI_PATH}/entity/*.h)
#封装音视频数据 mp4 flv 等
FILE(GLOB MUXER_PATH_ALL_CPP ${JNI_PATH}/muxer/*.cpp)
FILE(GLOB MUXER_PATH_ALL_H ${JNI_PATH}/muxer/*.h)
#soundtouch  path
FILE(GLOB SOUNDTOUCH_H_PATH ${SOUNDTOUCH_PATH}/include/*.h)
FILE(GLOB SOUNDTOUCH_SOURCE_H_PATH ${SOUNDTOUCH_PATH}/source/*.h)
FILE(GLOB SOUNDTOUCH_SOURCE_CPP_PATH ${SOUNDTOUCH_PATH}/source/*.cpp)
#editor
FILE(GLOB EDITOR_H_PATH ${EDITOR_PATH}/*.h)
FILE(GLOB EDITOR_CPP_PATH ${EDITOR_PATH}/*.cpp)

#CMD .c 文件
FILE(GLOB CMD_ALL_C_PATH ${CMD_PATH}/*.c)
FILE(GLOB CMD_ALL_CPP_PATH ${CMD_PATH}/*.cpp)


#-------------------------------------------------加载头文件----------------------------------------------
#加载头文件
include_directories(${SOUNDTOUCH_H_PATH})
include_directories(${SOUNDTOUCH_SOURCE_H_PATH})
include_directories(${FFMPEG_PATH}/include)
include_directories(${LIBRTMP_PATH}/include)

include_directories(${XLOG_PATH}/include)
include_directories(${CMAKE_SOURCE_DIR})
include_directories(${DEMUX_ALL_H})
include_directories(${DECODE_ALL_H})
include_directories(${RESAMPLE_ALL_H})
include_directories(${AUDIO_ALL_H})
include_directories(${VIDEO_ALL_H})
include_directories(${BASE_ALL_H})
include_directories(${BASE_DATA_ALL_H})
include_directories(${BUILDER_ALL_H})
include_directories(${CALLBACK_PATH_ALL_H})
include_directories(${UTILS_PATH_ALL_H})
include_directories(${ENTITY_PATH_ALL_H})
include_directories(${MUXER_PATH_ALL_H})
include_directories(${EDITOR_H_PATH})
include_directories(${SOUNDTOUCH_ROOT_H_PATH})
include_directories(${CMD_PATH}/include)


#-------------------------------------------------加载库文件----------------------------------------------

add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION ${SHARED_PATH}/libavcodec.so)

add_library(avformat SHARED IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION ${SHARED_PATH}/libavformat.so)

add_library(avutil SHARED IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION ${SHARED_PATH}/libavutil.so)

add_library(swscale SHARED IMPORTED)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION ${SHARED_PATH}/libswscale.so)

add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION ${SHARED_PATH}/libswresample.so)

add_library(filter SHARED IMPORTED)
set_target_properties(filter PROPERTIES IMPORTED_LOCATION ${SHARED_PATH}/libavfilter.so)


add_library(marsxlog SHARED IMPORTED)
set_target_properties(marsxlog PROPERTIES IMPORTED_LOCATION ${SHARED_PATH}/libmarsxlog.so)

add_library(rtmp STATIC IMPORTED)
set_target_properties(rtmp PROPERTIES IMPORTED_LOCATION ${LIBRTMP_STATIC_PATH}/librtmp.a)

add_library(
        avtools
        SHARED
        ${JNI_ALL_CPP}
        ${DEMUX_ALL_CPP}
        ${DECODE_ALL_CPP}
        ${RESAMPLE_ALL_CPP}
        ${AUDIO_ALL_CPP}
        ${VIDEO_ALL_CPP}
        ${BASE_ALL_CPP}
        ${BASE_DATA_ALL_CPP}
        ${BUILDER_ALL_CPP}
        ${JNI_PATH_ALL_CPP}
        ${CALLBACK_PATH_ALL_CPP}
        ${UTILS_PATH_ALL_CPP}
        ${ENTITY_PATH_ALL_CPP}
        ${MUXER_PATH_ALL_CPP}
        ${SOUNDTOUCH_SOURCE_CPP_PATH}
        ${EDITOR_CPP_PATH}
#        ${SOUNDTOUCH_ROOT_CPP_PATH}
        ${CMD_ALL_C_PATH}
        ${CMD_ALL_CPP_PATH}
)


find_library(
        log-lib
        log
)


#AVTools
target_link_libraries(
        avtools
        rtmp
        GLESv2
        EGL   #[=[EGL 是 nativeWindow 和 openGL ES 中间的桥梁]=]
        OpenSLES
        avcodec
        avformat
        swresample
        avutil
        filter
        z
        android
        marsxlog
        ${log-lib}
)
