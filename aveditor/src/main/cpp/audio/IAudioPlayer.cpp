//
// Created by 阳坤 on 2020-05-22.
//

#include "IAudioPlayer.h"

void IAudioPlayer::update(AVData data) {
    //压入缓冲队列
    if (data.size <= 0 || !data.data) return;
    while (!isExit) {
        framesMutex.lock();
        if (frames.size() > maxFrames) {
            framesMutex.unlock();
            sleep();
            continue;
        }
        frames.push_back(data);
        framesMutex.unlock();
        break;
    }
}

AVData IAudioPlayer::getData() {
    AVData d;
    isRuning = true;
    while (!isExit) {
        framesMutex.lock();
        if (isPause()) {
            sleep(2);
            framesMutex.unlock();
            continue;
        }


        if (!frames.empty()) {
            //有数据返回
            d = frames.front();
            frames.pop_front();
            framesMutex.unlock();
            pts = d.pts;
            return d;
        }
        sleep();
        framesMutex.unlock();
    }
    isRuning = false;
    //未获取数据
    return d;
}

void IAudioPlayer::clear() {
    framesMutex.lock();
    while (!frames.empty()) {
        if (frames.front().data)
            frames.front().drop();
        frames.pop_front();
    }
    framesMutex.unlock();
}


