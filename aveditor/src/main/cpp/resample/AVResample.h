//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IAVEDIT_AVRESAMPLE_H
#define IAVEDIT_AVRESAMPLE_H


#include <base/data/AVParameter.h>
#include <base/data/AVData.h>
#include <mutex>
#include "IResample.h"
extern "C"
{
#include <libswresample/swresample.h>
}

/**
 * 具体音频重采样模块
 */
class AVResample : public IResample {
public:
    /**
     * 打开资源，开始重采样
     * @param in
     * @param out
     * @return
     */
    virtual bool open(AVParameter in,AVParameter out=AVParameter());
    /**
     * 关闭重采样
     */
    virtual void close();
    /**
     * 重采样
     * @param indata
     * @return
     */
    virtual AVData resample(AVData indata);
protected:
    SwrContext *actx = 0;
    std::mutex mux;

};


#endif //IAVEDIT_AVRESAMPLE_H
