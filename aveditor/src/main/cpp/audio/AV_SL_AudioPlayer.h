//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IAVEDIT_AV_SL_AUDIOPLAYER_H
#define IAVEDIT_AV_SL_AUDIOPLAYER_H


#include "IAudioPlayer.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "utils/SoundTouchUtils.h"

//#include "../builder/AVToolsBuilder.h"

/**
 * 具体 OpenSL ES 渲染模块
 */
class AV_SL_AudioPlayer : public IAudioPlayer {
protected:
    unsigned char *buffer = 0;
    std::mutex mux;
    SAMPLETYPE *sd_buffer;
    /**
     * 播放的速率
     */
    double mPlaySpeed = 1.0;
    /**
      * 当前播放的音量
    */
    int curVolume = 10;
    AVParameter parameter;


    int preLen = 0;

public:
    AV_SL_AudioPlayer();

    virtual ~AV_SL_AudioPlayer();


public:
    /**
     * 开始播放
     * @param parameter
     * @return
     */
    virtual int startPlayer(AVParameter parameter);

    /**
    * 设置播放的速率
    * @param v
    */
    virtual void setPlaySpeed(double v);

    /**
     * 设置播放的声音
     */
    virtual void setPlayVolume(int percent);

    /**
     * 关闭资源
     */
    virtual void close();

    /**
     * 播放回调
     */
    void playCallback(void *p);

    /**
     * 创建 OpenSL
     * @return
     */
    static SLEngineItf createSL();

    int OpenSLSampleRate(SLuint32 sampleRate);

    int GetChannelMask(int channels);


    int readPcmData();
};


#endif //IAVEDIT_AV_SL_AUDIOPLAYER_H
