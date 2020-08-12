//
// Created by 阳坤 on 2020-05-23.
//

#ifndef IAVEDIT_IPLAYER_H
#define IAVEDIT_IPLAYER_H

#include "demux/IDemux.h"
#include "decode/IDecode.h"
#include "audio/IAudioPlayer.h"
#include "video/IVideoPlayer.h"
#include "resample/IResample.h"
#include <stdint.h>


class IPlayer : public IThread {
public:
    //是否视频硬解码
    int isMediaCodec = false;

    //音频输出参数配置
    AVParameter outPara;

    IDemux *demux = 0;
    IDecode *vdecode = 0;
    IDecode *adecode = 0;
    IResample *resample = 0;
    IVideoPlayer *videoView = 0;
    IAudioPlayer *audioPlay = 0;

protected:
    //用作音视频同步
    void main();

    std::mutex mux;

    IPlayer() {};
public:
    /**
     * 静态拿到当前组合管理
     * @param index
     * @return
     */
    static IPlayer *getInstance(unsigned char index = 0);

    /**
     * 打开资源
     * @param path
     * @return
     */
    virtual int open(const char *path, int isMediaCodec);

    /**
     * 关闭资源
     */
    virtual void close();

    /**
     * 关闭
     * @return
     */
    virtual int start();

    /**
     * 初始化
     * @param win
     */
    virtual void initView(void *win);

    /**
     * 播放
     * @return
     */
    //获取当前的播放进度 0.0 ~ 1.0
    virtual double playPos();

    /**
     * 获取总时间
     */
    uint64_t getTotalDuration();

    /**
     * seek
     * @param pos
     * @return
     */
    virtual int seekTo(double pos);

    /**
     * 设置播放状态
     * @param isP
     */
    virtual void setPause(bool isP);


    /**
     * 是否设置硬编码器
     * @param isMediacodec
     */
    virtual void initMediaCodec(void *vm);
};


#endif //IAVEDIT_IPLAYER_H
