//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IKAVEDIT_KAVDECODE_H
#define IKAVEDIT_KAVDECODE_H


#include "IDecode.h"

extern "C" {
#include "libavcodec/jni.h"
};

/**
 * 具体音视频解码
 */
class KAVDecode : public IDecode {
protected:
    /**
     * 解码器上下文
     */
    AVCodecContext *pCodec = 0;

    /**
     * 解码之后的裸流数据 PCM/YUV
     */
    AVFrame *pFrame = 0;
    /**
     * 互斥锁
     */
    mutex mux;

public:
    /**
 * 初始化硬件编码器
 * @param vm
 */
    static void initMediaCodec(void *vm);

    /**
     * 打开解码器
     */
    virtual int open(AVParameter parameter, int isMediaCodec = false);

    /**
     * 关闭解码器
     */
    virtual int close();

    /**
     * 清理数据
     */
    virtual int clear();

    /**
    * 发送待解码数据到 ffmpeg 解码线程
    * @param data
    * @return
    */
    virtual int sendPacket(AVData data);

    /**
     * 接收解码完成的数据
     */
    virtual AVData getDecodeFrame();

};


#endif //IKAVEDIT_KAVDECODE_H
