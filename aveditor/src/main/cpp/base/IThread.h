//
// Created by 阳坤 on 2020-05-21.
//

#ifndef IAVEDIT_ITHREAD_H
#define IAVEDIT_ITHREAD_H

#include <thread>
#include <vector>
#include "android_xlog.h"
#include <chrono>




using namespace std;

/**
 * 统一管理线程
 * C++ 11 线程库
 */
class IThread {
    /**
     * 公共函数
     */
public:
    /**
     * 线程开始执行
     * @return 1 is success 。
     */
    virtual int start();

    /**
     * 线程停止
     */
    virtual void stop();

    /**
     * 线程暂停
     */
    virtual void setPause(int pause);

    /**
     * 线程是否停止
     * @return  1 is pause
     */
    virtual int isPause();

    /**
     * 线程执行的入口
     */
    virtual void main(){};

    /**
     * 线程睡眠 默认睡 10 ms
     */
    void sleep(long mis = 10);

    /**
     * 变量
     */
protected:
    /**
     * 线程是否退出
     */
    int isExit = 0;
    /**
     * 线程是否正在运行
     */
    int isRuning = 0;
    /**
     * 线程是否已经暂停
     */
    int isPauseing = 0;

/**
 * 私有函数
 */
private:
    /**
     * 线程直接入口
     */
    void threadMain();


};


#endif //IAVEDIT_ITHREAD_H
