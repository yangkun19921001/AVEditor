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
        memcpy(buffer, data.data, data.size);
        jniCallback->onDecode(CHILD_THREAD, buffer, data.size);
    }
}


void AVAudioTransfer::setCallback(JNICallback *jniCallback) {
    this->jniCallback = jniCallback;
}

void AVAudioTransfer::onDecodeStart(int sampleRate, int channels, int format) {
    initBuffer();
    jniCallback->startDecode(MAIN_THREAD, sampleRate, channels, format);
}

void AVAudioTransfer::onDecodeStop() {
    jniCallback->stopDecode(MAIN_THREAD);
    delete buffer;
    buffer = 0;
}

void AVAudioTransfer::open(AVParameter parameter) {
    this->sampleRate = parameter.sample_rate;
    this->channles = parameter.channels;
    this->format = parameter.format;
}

void AVAudioTransfer::startTransfer() {
    jniCallback->startDecode(MAIN_THREAD, sampleRate, channles, 16);
}

void AVAudioTransfer::initBuffer() {
    buffer = new unsigned char[1024 * 1024];

}
