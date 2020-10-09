//
// Created by 阳坤 on 2020-05-23.
//

#include "AVTxture.h"
#include "IEGL.h"
#include "../../xlog/include/android_xlog.h"


ITexture *AVTxture::create() {
    return new AVTxture();
}


int AVTxture::init(void *window, AVTextureType type) {
    mux.lock();
    this->iegl = AVEGL::get();
    iegl->close();
    sh.close();
    this->type = type;
    if (!window) {
        mux.unlock();
        LOGE("txture init failed window is null");
        return false;
    }

    if (!iegl->init(window)) {
        mux.unlock();
        LOGE("IEGL init failed");
        return false;
    }

    if (!sh.init(static_cast<AVShaderType>(type))) {
        mux.unlock();
        LOGE("shader init failed");
        return false;
    }


    mux.unlock();
    return true;
}

void AVTxture::draw(unsigned char *data[], int width, int height) {




    mux.lock();
    sh.getTexture(0, width, height, data[0]);  // Y

    if (type == AVTEXTURE_YUV420P) {

        sh.getTexture(1, width / 2, height / 2, data[1]);  // U
        sh.getTexture(2, width / 2, height / 2, data[2]);  // V
    } else {
        sh.getTexture(1, width / 2, height / 2, data[1], true);  // UV
    }
    sh.draw();
    iegl->draw();
    mux.unlock();

}

void AVTxture::drop() {
    mux.lock();
    iegl->close();
    sh.close();
    mux.unlock();
    delete this;
}
