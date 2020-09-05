//
// Created by 阳坤 on 2020-08-13.
//

#ifndef IKAVEDIT_AVAUDIOTRANSFER_H
#define IKAVEDIT_AVAUDIOTRANSFER_H


#include <utils/AVQueue.h>
#include "ITransfer.h"

class AVAudioTransfer : public ITransfer {

protected:
    JNICallback *jniCallback = 0;
    int sampleRate = 0;
    int channles = 1;
    int format = 0;
    pthread_t id;

    AVQueue<AVData> avQueue;

public:
    virtual void onData(AVData data);


    virtual void onDecodeStart(int sampleRate, int channels, int format);


    virtual void onDecodeStop();

    virtual void setCallback(JNICallback *jniCallback);


    virtual void open(AVParameter parameter);

    virtual void startTransfer();

    void initBuffer();

    void onPush();
};

#endif //IKAVEDIT_AVAUDIOTRANSFER_H
