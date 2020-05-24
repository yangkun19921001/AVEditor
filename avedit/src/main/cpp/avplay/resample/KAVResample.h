//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IKAVEDIT_KAVRESAMPLE_H
#define IKAVEDIT_KAVRESAMPLE_H


#include "IResample.h"
extern "C"
{
#include <libswresample/swresample.h>
}

/**
 * 具体音频重采样模块
 */
class KAVResample : public IResample {
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


#endif //IKAVEDIT_KAVRESAMPLE_H
