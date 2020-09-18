//
// Created by 阳坤 on 2020-08-13.
//

#ifndef IKAVEDIT_ITRANSFER_H
#define IKAVEDIT_ITRANSFER_H


#include <callback/JNICallback.h>
#include "../base/IObserver.h"


/**
 * 主要是做对数据的转移封装
 */
class ITransfer : public IObserver {

public:

    /**
    * 是否注册音频数据转移模块
    */
    int REGISTER_AUDIO_TRANSFER_MODEL = false;

    unsigned char *buffer = 0;

    /**
     * 这里缓存满了就会产生阻塞
     */
    virtual void update(AVData data);

    /**
     * 获取缓存数量
     */
    virtual void onData(AVData data) = 0;

    /**
     * 解码信息，子类实现
     * @param sampleRate
     * @param channels
     * @param format
     */
    virtual void onDecodeStart(int sampleRate, int channels, int format) = 0;


    virtual void onDecodeStop() = 0;

    /**
     * 设置回调通知 Java 层
     * @param jniCallback
     */
    virtual void setCallback(JNICallback *jniCallback) = 0;

    virtual void open(AVParameter parameter) = 0;


    virtual void startTransfer() = 0;

    /**
     * @param isRegister
     */
    void registerModel(bool isRegister) {
        REGISTER_AUDIO_TRANSFER_MODEL = isRegister;
    };
};


#endif //IKAVEDIT_ITRANSFER_H
