//
// Created by 阳坤 on 2020-08-20.
//

#ifndef IKAVEDIT_IMUXER_H
#define IKAVEDIT_IMUXER_H


#include "../base/IObserver.h"
#include "../utils/AVQueue.h"


#define FLV "flv"
#define MP4 "mp4"


class IMuxer : public IObserver {

protected:
    //用于存音视频数据流的
    AVQueue<AVData> streamLists;

public:
    /**
   * 初始化复用器
   * @param source
   * @return
   */
    virtual int initMuxer(const char *outPath, const char *ourFormat = MP4) = 0;

    /**
     * 清理资源
     * @return
     */
    virtual void clear();

    /**
     * 关闭封装器
     */
     virtual void close()=0;

     virtual int start();

    /**
     * 送入封装器
     */

    virtual int enqueue(AVData avData);

    /**
     * 取出封装器
     */
    virtual void dequeue(AVData avData) = 0;

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


#endif //IKAVEDIT_IMUXER_H
