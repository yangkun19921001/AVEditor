//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IAVEDIT_IRESAMPLE_H
#define IAVEDIT_IRESAMPLE_H


#include "base/IObserver.h"

/**
 * 抽象音频重采样
 */
class IResample : public IObserver {

public:
    /**
     * 打开资源
     * @param in
     * @param out
     * @return
     */
    virtual bool open(AVParameter in, AVParameter out = AVParameter()) = 0;

    /**
     * 开始重采样
     * @param indata
     * @return
     */
    virtual AVData resample(AVData indata) = 0;

    /**
     * 关闭
     */
    virtual void close() = 0;

    /**
     * 有新的数据更新
     * @param data
     */
    virtual void update(AVData data);

    /**
     * 输出的声音通道数量
     */
    int outChannels = 2;

    /**
     * 采样格式 默认 16位
     */
    int outFormat = AV_SAMPLE_FMT_S16;
};


#endif //IAVEDIT_IRESAMPLE_H
