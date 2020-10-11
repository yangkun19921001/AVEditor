//
// Created by 阳坤 on 2020-05-22.
//

#include "IDecode.h"


/**
 * 子线程
 * @param data
 */
void IDecode::update(AVData data) {
    if (data.isAudio != isAudio || data.size <= 0) {
        return;
    }

    while (!isExit) {
        if (packs.size() < maxCache) {
            packsMutex.lock();
            packs.push_back(data);
            packsMutex.unlock();
            break;
        }
        packsMutex.unlock();
        sleep(1);
    }


}

/**
 * 主线程调用
 * @return
 */
int IDecode::clear() {
    int ret = 0;
    //TODO ---- 音频模块这里会导致死锁,先暂时释放
//    packsMutex.unlock();
//    packsMutex.lock();
    while (!packs.empty()) {
        ret = 1;
//        packsMutex.lock();
        AVData data = packs.front();
        packs.pop_front();
//        packsMutex.unlock();
        if (data.data)
            data.drop();
    }
//
    pts = 0;
    synPts = 0;
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
            //TODO 这里要注意，更优解是
            //1、如果视频的 pts 小于 音频的 pts 那么视频 pts 就 seek
            //2、如果视频的 pts 大于 音频的 pts 那么需要丢帧
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
        AVData data = packs.front();//取出第一个元素
        packs.pop_front();//删除第一个元素
        /**
         * 发送到  ffmpeg 解码线程
         */
        if (sendPacket(data)) {
            while (!isExit) {
                //拿到解码之后的数据
                AVData frame = getDecodeFrame();
                if (!frame.data) {
                    packsMutex.unlock();
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

    packsMutex.unlock();

}


