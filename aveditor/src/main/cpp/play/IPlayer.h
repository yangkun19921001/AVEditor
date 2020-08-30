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
#include "audio/ITransfer.h"
#include <stdint.h>
#include <mutex>
#include <entity/MediaEntity.h>


class IPlayer : public IThread {

private:


public:
    //是否视频硬解码
    int isMediaCodec = false;

    //音频输出参数配置
    AVParameter outPara;

    IDemux *demux = 0;
    IDecode *vdecode = 0;
    IDecode *adecode = 0;
    IResample *resample = 0;
    ITransfer *transfer = 0;
    IVideoPlayer *videoView = 0;
    IAudioPlayer *audioPlay = 0;

    /**
     * 用于装多个片段 Media 源
     */
    std::deque<MediaEntity *> mediaLists;

//    std::deque<MediaEntity*> mediaLists;
    //获取总的 totalDuration
    int64_t totalDuration;
    //是否播放完成
    int isPlayComplete = false;

    /**
     * 找到下一个索引
     */
    int nextIndex = 0;

    /**
     * 是否循环播放
     */
    int isLoopPlay = false;

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
     * 设置播放的音量
     */
    void setPlayVolume(int v);
    /**
     * 设置播放的速率
     */
    void setPlaySpeed(double d);

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

    /**
     * 设置
     * @param jniEnv
     * @param lists
     */
    virtual void setDataSource(JNIEnv *jniEnv, jobject lists);


    /**
     * 设置下一个片段播放路径
     */
    virtual bool setNextDataSource();

    /**
     * 播放下一个片段
     */
    virtual void playNext();


};


#endif //IAVEDIT_IPLAYER_H
