//
// Created by 阳坤 on 2020-05-23.
//

#include "IPlayerProxy.h"


IPlayerProxy::IPlayerProxy() {
    //构建一个 播放组和模块，统一交于播放代理来处理
    this->pPlayer = AVPlayerBuilder::getInstance()->builderPlayer();
}


int IPlayerProxy::initMediaCodec(void *vm) {
    int ret = -1;
    mux.lock();
    if (pPlayer) {
        ret = pPlayer->initMediaCodec(vm);
    }
    mux.unlock();
    return ret;
}

int IPlayerProxy::open(const char *path, int isMediaCodec) {
    int ret = 0;
    if (!path) {
        return ret;
    }
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
    LOGD("IPlayerProxy: close in");
    mux.lock();
    if (pPlayer) {
        pPlayer->close();
    }

    if (url) {
        delete[](url);
    }
    mux.unlock();
    LOGD("IPlayerProxy: close in");
}

int IPlayerProxy::start() {
    bool re = false;
    mux.lock();
    if (pPlayer) {

        re = pPlayer->start();
    }
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

ITransfer *IPlayerProxy::getTransferInstance() {
    return pPlayer->transfer;
}

/**
 * set多个 URL
 * @param jniEnv
 * @param lists
 */
void IPlayerProxy::setDataSource(JNIEnv *jniEnv, jobject lists) {
    if (pPlayer)
        pPlayer->setDataSource(jniEnv, lists);
}

void IPlayerProxy::setPlayVolume(int v) {
    if (pPlayer)
        pPlayer->setPlayVolume(v);

}

void IPlayerProxy::setPlaySpeed(double d) {
    if (pPlayer)
        pPlayer->setPlaySpeed(d);
}


/**
 * 拿到多个片段
 * @return
 */
deque<MediaEntity *> IPlayerProxy::getDataSources() {
    std::deque<MediaEntity *> mediaLists;
    if (pPlayer) {
        if (pPlayer->mediaLists.size() == 0) {
            MediaEntity *mediaEntity = new MediaEntity();
            mediaEntity->path = getDataSource();
            mediaLists.push_back(mediaEntity);
            return mediaLists;
        }
        return pPlayer->mediaLists;
    }

    return mediaLists;
}

void IPlayerProxy::setMediaCodec(jboolean isMediaCodec) {
    if (pPlayer) {
        pPlayer->setMediaCodec(isMediaCodec);
    }

}

bool IPlayerProxy::isMediaCodec() {

    bool isMediaCodec = false;
    if (pPlayer)
        return pPlayer->isMediaCodec();
    return isMediaCodec;
}





