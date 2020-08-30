//
// Created by 阳坤 on 2020-05-23.
//

#ifndef IAVEDIT_IPLAYERPROXY_H
#define IAVEDIT_IPLAYERPROXY_H

#include <mutex>
#include <entity/MediaEntity.h>
#include "IPlayer.h"
#include "../builder/AVPlayerBuilder.h"



class IPlayerProxy {

public:
    IPlayerProxy();

protected:
    IPlayer *pPlayer = 0;
    std::mutex mux;

    /**
     * 播放源
     */
    char *url = 0;


public:
    static IPlayerProxy *getInstance() {
        static IPlayerProxy px;
        return &px;
    }

    /**
     * 初始化
     * @param vm
     */
    void initMediaCodec(void *vm = 0);

    /**
     * 打开资源
     * @param path
     * @return
     */
    virtual int open(const char *path, int isMediaCodec);

    /**
     * seek
     * @param pos
     * @return
     */
    virtual int seekTo(double pos);

    /**
     * 关闭资源
     */
    virtual void close();

    /**
     * 开始播放
     * @return
     */
    virtual int start();

    /**
     * 初始化 window
     * @param win
     */
    virtual void initWindow(void *win);

    /**
     * 设置播放状态
     * @param isP
     */
    virtual void setPause(bool isP);

    /**
     * 是否暂停
     * @return
     */
    virtual int isPause();

    /**
     * 获取当前播放的进度
     * @return
     */
    //获取当前的播放进度 0.0 ~ 1.0
    virtual double playPos();

    /**
     * 设置播放源
     * @param source
     */
    void setDataSource(const char *source);


    /**
     * 设置播放的音量
     */
    void setPlayVolume(int v);
    /**
     * 设置播放的速率
     */
    void setPlaySpeed(double d);


    /**
     * 设置播放源
     * @param source
     */
    void setDataSource(JNIEnv *jniEnv, jobject lists);

    /**
     * 拿到播放源
     * @return
     */
    const char *getDataSource();

    /**
     * 拿到总时长 ms
     */
    int64_t getTotalDuration();


    ITransfer *getTransferInstance();



};


#endif //IAVEDIT_IPLAYERPROXY_H
