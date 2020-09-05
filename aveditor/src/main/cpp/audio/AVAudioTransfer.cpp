//
// Created by 阳坤 on 2020-08-13.
//

#include "AVAudioTransfer.h"


/**
 * 父类传递过来的解码完成的数据
 * @param data
 */
void AVAudioTransfer::onData(AVData data) {
    //没有注册该模块不能进行后续操作
    if (!REGISTER_AUDIO_TRANSFER_MODEL)return;
    if (data.isAudio) {
        if (!buffer) {
            initBuffer();
        }

        avQueue.push(data);
//        memcpy(buffer, data.data, data.size);
//        jniCallback->onDecode(CHILD_THREAD, buffer, data.size);
    }


//    delete[] data.data;
//    data.data == 0;
}


void AVAudioTransfer::setCallback(JNICallback *jniCallback) {
    this->jniCallback = jniCallback;
}


void *onPushData(void *pVoid) {

    AVAudioTransfer *transfer = static_cast<AVAudioTransfer *>(pVoid);

    transfer->onPush();

    return 0;

}

void AVAudioTransfer::onDecodeStart(int sampleRate, int channels, int format) {
    initBuffer();
    avQueue.setFlag(1);
    pthread_create(&id, 0, onPushData, this);
    jniCallback->startDecode(MAIN_THREAD, sampleRate, channels, format);

}

void AVAudioTransfer::onDecodeStop() {
    avQueue.setFlag(0);
    jniCallback->stopDecode(MAIN_THREAD);
    delete[] buffer;
    buffer = 0;
    avQueue.clearQueue();

}

void AVAudioTransfer::open(AVParameter parameter) {
    this->sampleRate = parameter.sample_rate;
    this->channles = parameter.channels;
    this->format = parameter.format;
}

void AVAudioTransfer::startTransfer() {
    onDecodeStart( sampleRate, channles, 16);
//    jniCallback->startDecode(MAIN_THREAD, sampleRate, channles, 16);
}

void AVAudioTransfer::initBuffer() {
//    buffer = new unsigned char[1024 * 1024];

}

void AVAudioTransfer::onPush() {
    while (!isExit) {
        AVData data;
        avQueue.pop(data);
        if (data.size > 0) {
            jniCallback->onDecode(CHILD_THREAD, data.data, data.size);
            delete[] data.data;
            data.data == 0;
        }
    }
}
