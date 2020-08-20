//
// Created by 阳坤 on 2020-08-20.
//

#ifndef IKAVEDIT_AVMUXER_H
#define IKAVEDIT_AVMUXER_H


#include "IMuxer.h"

class AVMuxer : public IMuxer {


private:
    /**
     * 输出上下文
     */
    AVOutputFormat *ofmt = 0;
    //封装格式上下文
    AVFormatContext *formatCtx;

public:
    /**
   * 初始化复用器
   * @param source
   * @return
   */
    virtual int initMuxer(const char *outPath, const char *ourFormat = "mp4");

    /**
     * 清理资源
     * @return
     */
    virtual void clear();

    /**
 * 关闭封装器
 */
    virtual void close();

    /**
     * 取出封装器
     */
    virtual void dequeue(AVData avData);
};


#endif //IKAVEDIT_AVMUXER_H
