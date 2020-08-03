//
// Created by 阳坤 on 2020-05-22.
//

#include "IDecode.h"


void IDecode::update(AVData data) {
    if (data.isAudio != isAudio) {
        return;
    }
    while (!isExit) {
        packsMutex.lock();

        if (packs.size() < maxCache) {
            packs.push_back(data);
            packsMutex.unlock();
            break;
        }
        packsMutex.unlock();
        sleep(1);
    }

}

int IDecode::clear() {
    int ret = 0;
    //TODO ---- 音频模块这里会导致死锁,先暂时释放
    if (isAudio){
        packsMutex.unlock();
    }

    packsMutex.lock();

    while (!packs.empty()) {
        ret = 1;
        AVData data = packs.front();
        if (data.data)
            data.drop();
        packs.pop_front();
    }
    pts = 0;
    synPts = 0;
    packsMutex.unlock();

    return ret;
}

/**
 * 子线程解码
 */
void IDecode::main() {
    while (!isExit) {
        packsMutex.lock();
        if (isPause()) {
            packsMutex.unlock();
            sleep();
            continue;
        }
        //判断音视频同步
        if (!isAudio && synPts > 0) {
            if (synPts < pts) {
                packsMutex.unlock();

                sleep();
                continue;
            }

        }

        if (packs.empty()) {
            packsMutex.unlock();
            sleep();
            continue;
        }

        //取出待解码的数据
        AVData data = packs.front();
        packs.pop_front();
        /**
         * 发送到  ffmpeg 解码线程
         */
        if (sendPacket(data)) {
            while (!isExit) {
                //拿到解码之后的数据
                AVData frame = getDecodeFrame();
                if (!frame.data) {
                    break;
                }
                pts = frame.pts;
                //通知订阅者，有新的数据产生需要处理。
                this->send(frame);
            }
        }
        data.drop();
        packsMutex.unlock();
    }


}


