//
// Created by 阳坤 on 2020-05-21.
//

#include "IDemux.h"


/**
 *
 * 这样做的好处是，避免转换时间戳的时候出现分母为 0
 * 分数转为浮点数
 * @param r
 * @return
 */
double IDemux::r2d(AVRational r) {
    return r.num == 0 || r.den == 0 ? 0. : (double) r.num / (double) r.den;
}

/**
 * 子线程中解封装
 */
void IDemux::main() {
    while (!this->isExit) {//是否退出
        if (isPause()) {//如果暂停就 sleep 一下
            sleep();
            continue;
        }

        AVData data = read();//具体实现类去实现
        if (data.size > 0) {
//            LOGE("分发原始数据 pts:%lld type:%d format", data.pts, data.isAudio, data.format);
            send(data);
        } else {
            sleep();
        }
    }
}

