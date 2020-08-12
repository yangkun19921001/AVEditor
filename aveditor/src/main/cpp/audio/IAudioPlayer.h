//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IAVEDIT_IAUDIOPLAYER_H
#define IAVEDIT_IAUDIOPLAYER_H


#include <list>
#include "../../base/IObserver.h"

/**
 * 抽象音频播放
 */
class IAudioPlayer : public IObserver {

protected:
    std::list<AVData> frames;
    std::mutex framesMutex;
public:
    /**
     * 最大缓存
     */
    int maxFrames = 88;
    int pts = 0;

public:
    /**
     * 这里缓存满了就会产生阻塞
     */
    virtual void update(AVData data);

    /**
     * 获取缓存数量
     */
    virtual AVData getData();

    /**
     * 开始播放
     */
    virtual int startPlayer(AVParameter parameter) = 0;

    /**
     * 关闭资源
     */
    virtual void close() = 0;

    /**
     * 清理缓存
     */
    virtual void clear();
};


#endif //IAVEDIT_IAUDIOPLAYER_H
