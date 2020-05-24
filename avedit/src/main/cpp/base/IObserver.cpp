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
        obss[i]->update(data);//子类实现，由子类接收
    }
    mux.unlock();

}
