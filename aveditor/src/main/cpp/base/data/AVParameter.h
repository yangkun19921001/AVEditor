//
// Created by 阳坤 on 2020-05-21.
//

#ifndef IAVEDIT_AVPARAMETER_H
#define IAVEDIT_AVPARAMETER_H

#include <libavutil/rational.h>

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
     * 音频通道
     */
    int channels = 2;
    /**
     * 音频码率
     */
    int sample_rate = 44100;
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
};


#endif //IAVEDIT_AVPARAMETER_H
