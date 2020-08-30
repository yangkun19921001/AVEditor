//
// Created by 阳坤 on 2020-08-20.
//

#include "IMuxer.h"


void IMuxer::update(AVData avData) {
    enqueue(avData);
}


void IMuxer::main() {
    while (!isExit) {
        if (isPause()) {
            sleep();
            continue;
        }
        int ret = dequeue();
        if (ret < 0){
            LOGE("封装器父类成功退出! ret:%d",ret);
            break;
        }
    }
    LOGE("封装器父类成功退出!");
}


int IMuxer::start() {
    int ret = IThread::start();
    return ret;
}




