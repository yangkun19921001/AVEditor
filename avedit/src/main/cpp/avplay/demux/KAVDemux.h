//
// Created by 阳坤 on 2020-05-21.
//

#ifndef IKAVEDIT_KAVDEMUX_H
#define IKAVEDIT_KAVDEMUX_H


#include "IDemux.h"


/**
 * 具体解封装的操作
 */
class KAVDemux : public IDemux {
    /**
     * 成员变量
     */
private:
    mutex mux;

protected:
    /**
     * 音视频格式上下文
     */
    AVFormatContext *pFormatCtx = 0;
    /**
     * 音频索引
     */
    int audio_stream_index = -1;
    /**
     * 视频索引
     */
    int video_stream_index = -1;

    /**
     * 构造函数
     */
public:
    KAVDemux();


    /**
     * 公共函数
     */
public:
    /**
      * 打开资源
      * @param source
      * @return
      */
    virtual int open(const char *source);

    /**
     * 关闭资源
     * @return
     */
    virtual int close();

    /**
     * seek 某一位置
     * @param pos
     * @return
     */
    virtual int seekTo(double pos);

    /**
     * 获取音视频头部信息
     * @return
     */
    virtual AVParameter getVInfo();

    virtual AVParameter getAInfo();

    /**
     * 读取一帧音视频数据，哪里调用，哪里释放
     * @return
     */
    virtual AVData read();


};


#endif //IKAVEDIT_KAVDEMUX_H
