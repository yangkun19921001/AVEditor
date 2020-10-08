//
// Created by 阳坤 on 2020-05-21.
//

#include "IThread.h"


void IThread::sleep(long mis) {
    chrono::microseconds du(mis);
    this_thread::sleep_for(du);
}

int IThread::start() {
    this->isExit = false;
    this->isPauseing = false;
    thread th(&IThread::threadMain, this);
    th.detach();
    return 1;
}

void IThread::stop() {
    isExit = true;
    LOGE("thread stop--->begin ！");
    int i = 200;
    while (i--) {//等待 一会儿
        if (!isRuning)
            break;
        sleep(2);
    }
    LOGE("thread stop--->success ！");
}

void IThread::setPause(int pause) {
    this->isPauseing = pause;
    for (int i = 0; i < 10; ++i) {
        if (this->isPauseing == pause)
            break;
        sleep();
    }
}

int IThread::isPause() {
    return this->isPauseing;
}




void IThread::threadMain() {
    LOGE("线程函数进入");
    isRuning = true;
    main();//由子类实现
    LOGE("线程函数退出");
    isRuning = false;

}

