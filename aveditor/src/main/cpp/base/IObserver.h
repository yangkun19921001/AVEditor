//
// Created by 阳坤 on 2020-05-21.
//

#ifndef IAVEDIT_IOBSERVER_H
#define IAVEDIT_IOBSERVER_H


#include "IThread.h"
#include "data/AVData.h"
#include "data/AVParameter.h"


/**
 * 观察者
 * 主要是观察 解码的数据
 */
class IObserver : public IThread {



public:
    int id = 0;
    /**
     * 发送数据给订阅者
     * 子类实现
     * @param data
     */
    virtual void update(AVData data) {};
    virtual void onComplete() {};

    /**
     * 订阅消息，有数据更新就通知
     * @param obs
     */
    void registers(IObserver *obs = 0);
    void unRegisters(IObserver *obs = 0);

    /**
     * 通知所有的订阅者，有新的数据产生或者更新。
     * @param data
     */
    void send(AVData data);
    void sendComplete(AVData data);

    void notifyAll();

public:
    char *TAG = (char *)("UnKnown %s");//观察者 TAG


protected:
    vector<IObserver *> obss;//顺序存储
    mutex mux;//互斥锁
};


#endif //IAVEDIT_IOBSERVER_H
