//
// Created by 阳坤 on 2020-05-21.
//

#ifndef IAVEDIT_AVPARAMETER_H
#define IAVEDIT_AVPARAMETER_H




extern "C" {
#include <libavutil/rational.h>
#include <libavcodec/avcodec.h>
}


struct AVCodecParameters;


enum MediaType {
    AAC,
    H264,
    H265,
};

class AVParameter {
public:
    /**
     * 解码参数
     */
    AVCodecParameters *para = 0;

    /**
     * 解码参数
     */
    AVCodecContext *codec = 0;
    /**
     * 音频通道
     */
    int channels = -1;
    /**
     * 音频码率
     */
    int sample_rate = -1;
    /**
     * 原始数据格式
     *
     * - video: the pixel format, the value corresponds to enum AVPixelFormat.
     * - audio: the sample format, the value corresponds to enum AVSampleFormat.
     */
    int format = -1;


    /**
     * 时间基
     */
    AVRational timebase;
    int64_t duration;
};


#endif //IAVEDIT_AVPARAMETER_H
