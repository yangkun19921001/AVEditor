//
// Created by 阳坤 on 2020-05-22.
//

#include "AV_GL_VideoPlayer.h"

void AV_GL_VideoPlayer::setRender(void *window) {
    this->pNativeWindow = window;

}

void AV_GL_VideoPlayer::render(AVData data) {
    if (!pNativeWindow)return;
    if (!texture)
    {
        texture = AVTxture::create();
        texture ->init(pNativeWindow,(AVTextureType)data.format);
    }

    texture->draw(data.datas,data.width,data.height);
}

void AV_GL_VideoPlayer::close() {
    mux.lock();
    if (texture) {
        texture->drop();
        texture = 0;
    }
    mux.unlock();

}
