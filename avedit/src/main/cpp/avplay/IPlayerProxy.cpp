//
// Created by 阳坤 on 2020-05-23.
//

#include "IPlayerProxy.h"
#include "builder/KAVPlayerBuilder.h"

IPlayerProxy::IPlayerProxy() {
    //构建一个 播放组和模块，统一交于播放代理来处理
    this->pPlayer = KAVPlayerBuilder::getInstance()->builderPlayer();
}


void IPlayerProxy::initMediaCodec(void *vm) {
    mux.lock();
    if (pPlayer) {
        pPlayer->initMediaCodec(vm);
    }
    mux.unlock();
}

int IPlayerProxy::open(const char *path, int isMediaCodec) {
    int ret = 0;
    mux.lock();
    if (pPlayer) {
        ret = pPlayer->open(path, isMediaCodec);
    }
    mux.unlock();
    return ret;
}

int IPlayerProxy::seekTo(double pos) {
    bool re = false;
    mux.lock();
    if (pPlayer) {
        re = pPlayer->seekTo(pos);
    }
    mux.unlock();
    return re;
}

void IPlayerProxy::close() {
    mux.lock();
    if (pPlayer)
        pPlayer->close();
    mux.unlock();
}

int IPlayerProxy::start() {
    bool re = false;
    mux.lock();
    if (pPlayer)
        re = pPlayer->start();
    mux.unlock();
    return re;
}

void IPlayerProxy::initWindow(void *win) {
    mux.lock();
    if (pPlayer)
        pPlayer->initView(win);
    mux.unlock();

}

void IPlayerProxy::setPause(bool isP) {
    mux.lock();
    if (pPlayer)
        pPlayer->setPause(isP);
    mux.unlock();
}

int IPlayerProxy::isPause() {
    int re = false;
    mux.lock();
    if (pPlayer)
        re = pPlayer->isPause();
    mux.unlock();
    return re;
}

double IPlayerProxy::playPos() {
    double pos = 0.0;
    mux.lock();
    if (pPlayer) {
        pos = pPlayer->playPos();
    }
    mux.unlock();
    return pos;
}

void IPlayerProxy::setDataSource(const char *source) {
    this->url = new char[strlen(source) + 1];
    strcpy(this->url, source);

}

const char *IPlayerProxy::getDataSource() {
    return this->url;
}

int64_t IPlayerProxy::getTotalDuration() {
    int64_t time = 0;
    mux.lock();
    if (pPlayer) {
        time = pPlayer->getTotalDuration();
    }
    mux.unlock();
    return time;
}

