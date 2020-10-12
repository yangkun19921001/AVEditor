//
// Created by 阳坤 on 2020-05-21.
//

#include "IObserver.h"


/**
 * 订阅
 * @param obs
 */
void IObserver::registers(IObserver *obs) {
    if (!obs) {
        LOGE(TAG, "register error !");
        return;
    }
    id += 1;
    mux.lock();
    obss.push_back(obs);
    mux.unlock();
    LOGE(TAG, "register success !");
}

/**
 * 通知订阅者接收数据
 * @param data
 */
void IObserver::send(AVData data) {
    mux.lock();
    for (int i = 0; i < obss.size(); ++i) {
        if (obss[i]) {
            obss[i]->update(data);//子类实现，由子类接收
        }
    }
    mux.unlock();
}


void IObserver::unRegisters(IObserver *obs) {
    if (!obs) {
        LOGE(TAG, "unRegister error !");
        return;
    }
    mux.lock();
    for (vector<IObserver *>::iterator it = obss.begin(); it != obss.end();) {
        IObserver *observer = *it;
        if (observer->id == obs->id) {
            it = obss.erase(it); //不能写成arr.erase(it);
        } else {
            ++it;
        }
    }
    id = 0;
    obs->id = 0;
    mux.unlock();
}

void IObserver::notifyAll() {
    mux.unlock();
}

void IObserver::sendComplete(AVData data) {
    mux.lock();
    for (int i = 0; i < obss.size(); ++i) {
        if (obss[i])
            obss[i]->onComplete();//子类实现，由子类接收
    }
    mux.unlock();
}



