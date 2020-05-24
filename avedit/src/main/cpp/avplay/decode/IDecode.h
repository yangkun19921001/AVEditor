//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IKAVEDIT_IDECODE_H
#define IKAVEDIT_IDECODE_H


#include "../../base/IObserver.h"
#include <list>


#define H264_MEDIACODEC "h264_mediacodec"
#define MPEG4_MEDIACODEC "mpeg4_mediacodec"
#define HEVC_MEDIACODEC "hevc_mediacodec"

/**
 * 抽象解码模块 包含软硬编解码
 */
class IDecode : public IObserver {

    /**
     * 变量
     */
protected:
    /**
     * 读取缓存
     */
    std:: list <AVData> packs;
    /**
     * 互斥锁
     */
    std::mutex packsMutex;

protected:
    /**
     * 子线程解码函数
     */
    virtual void main();


    /**
     * 公共变量
     */
public:
    /**
     * 最大的队列缓存
     */
    int maxCache = 88;
    /**
     * 音视频同步 pts
     */
    int synPts = 0;
    /**
     * 当前显示  pts
     */
    long long pts = 0;

    /**
     * 是否是音频数据
     */
    int isAudio = false;

public:
    /**
     * 打开解码器
     */
    virtual int open(AVParameter parameter, int isMediaCodec = false) = 0;

    /**
     * 关闭解码器
     * @return
     */
    virtual int close() = 0;

    /**
     * 清理缓存
     */
    virtual int clear();

    /**
     * 发送待解码数据到 ffmpeg 解码线程
     * @param data
     * @return
     */
    virtual int sendPacket(AVData data) = 0;

    /**
     * 接收解码完成的数据
     */
    virtual AVData getDecodeFrame() = 0;

    /**
     * 发送音视频数据到解码队列中
     * @param data
     */
    virtual void update(AVData data);


};


#endif //IKAVEDIT_IDECODE_H
