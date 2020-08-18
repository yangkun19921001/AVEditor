//
// Created by 阳坤 on 2020-08-13.
//

#ifndef IKAVEDIT_JNICALLBACK_H
#define IKAVEDIT_JNICALLBACK_H


#include <jni.h>

#define MAIN_THREAD 1
#define CHILD_THREAD 0


class JNICallback {
protected:
    JNIEnv *jniEnv = 0;
    JavaVM *javaVM = 0;
    jobject jobject1;


    jmethodID onPCMData;
    jmethodID onDecodeStart;
    jmethodID onDecodeStop;


public:
    JNICallback(JavaVM *vm, JNIEnv *env, jobject obj);

    ~JNICallback();

    void startDecode(int threadMode,int sampleRate, int channels, int format);

    void stopDecode(int threadMode);

    void onDecode(int threadMode,uint8_t *data,int size);

};


#endif //IKAVEDIT_JNICALLBACK_H
