//
// Created by 阳坤 on 2020-05-21.
//

#ifndef IAVEDIT_IDEMUX_H
#define IAVEDIT_IDEMUX_H


#include "../../base/IObserver.h"

extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/rational.h"
#include "libavutil/avutil.h"
};


/**
 * 解封装模块
 */
class IDemux : public IObserver {


public:
    /**
     * 总时长
     */
    long long totalDuration = 0;

    /**
     * 是否有音频流
     */
    int mAudioPacketExist = true;

    /**
    * 是否有视频流
    */
    int mVideoPacketExist = true;



public:
    /**
     * 打开资源
     * @param source
     * @return
     */
    virtual int open(const char *source) = 0;

    /**
     * 关闭资源
     * @return
     */
    virtual int close() = 0;

    /**
     * seek 某一位置
     * @param pos
     * @return
     */
    virtual int seekTo(double pos) = 0;

    /**
     * 获取音视频头部信息
     * @return
     */
    virtual AVParameter getVInfo() = 0;

    virtual AVParameter getAInfo() = 0;

    /**
     * 读取一帧音视频数据，哪里调用，哪里释放
     * @return
     */
    virtual AVData read() = 0;


    /**
     * 转换时间戳
     * @param r
     * @return
     */
    double r2d(AVRational r);

protected:
    /**
     * 子线程入口
     */
    virtual void main();



};


#endif //IAVEDIT_IDEMUX_H
