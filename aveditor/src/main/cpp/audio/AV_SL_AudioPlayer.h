//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IAVEDIT_AV_SL_AUDIOPLAYER_H
#define IAVEDIT_AV_SL_AUDIOPLAYER_H


#include "IAudioPlayer.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

/**
 * 具体 OpenSL ES 渲染模块
 */
class AV_SL_AudioPlayer : public IAudioPlayer {
protected:
    unsigned char *buffer = 0;
    std::mutex mux;
    //SoundTouch
//    SoundTouch *soundTouch = NULL;


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

    /**
 * 速率初始化
 */
    void initSoundTouch(int sampleRate,int channels,int speed);
//    {
//        this->sampleBuffer = static_cast<SAMPLETYPE *>(malloc(this->sampleRate * 2 * 2));
//        soundTouch = new SoundTouch();
//        soundTouch->setSampleRate(sampleRate);
//        soundTouch->setChannels(2);
//        soundTouch->setPitch(1);
//        soundTouch->setTempo(speed);
//    }
};


#endif //IAVEDIT_AV_SL_AUDIOPLAYER_H
