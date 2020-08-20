//
// Created by 阳坤 on 2020-08-13.
//

#include <GLES2/gl2.h>
#include <android_xlog.h>
#include "JNICallback.h"

JNICallback::JNICallback(JavaVM *vm, JNIEnv *env, jobject obj) {
    this->javaVM = vm;
    this->jniEnv = env;
    //必须声明全局 不然会报 error JNI DETECTED ERROR IN APPLICATION: use of invalid jobject 0xff868d8c
    this->jobject1 = env->NewGlobalRef(obj);// 坑，需要是全局（jobject一旦涉及到跨函数，跨线程，必须是全局引用）
    if (!jniEnv || !jobject1)
        return;

    jclass jcls = jniEnv->GetObjectClass(this->jobject1);
    this->onDecodeStart = jniEnv->GetMethodID(jcls, "onDecodeStart", "(III)V");
    this->onPCMData = jniEnv->GetMethodID(jcls, "onPCMData", "([B)V");
    this->onDecodeStop = jniEnv->GetMethodID(jcls, "onDecodeStop", "()V");



}

JNICallback::~JNICallback() {
    if (jniEnv && jobject1 && javaVM) {
        jniEnv->DeleteGlobalRef(this->jobject1);
        jniEnv = NULL;
    }
}


void JNICallback::stopDecode(int threadMode) {
    if (CHILD_THREAD == threadMode) {
        JNIEnv *jniEnv1 = 0;
        if (javaVM->AttachCurrentThread(&jniEnv1, 0) == JNI_OK) {
            jniEnv1->CallVoidMethod(this->jobject1, this->onDecodeStop);
            javaVM->DetachCurrentThread();
        }
    } else {
        jniEnv->CallVoidMethod(this->jobject1, this->onDecodeStop);
    }

}

void JNICallback::startDecode(int threadMode, int sampleRate, int channels, int format) {
    if (CHILD_THREAD == threadMode) {
        JNIEnv *jniEnv1 = 0;
        if (javaVM->AttachCurrentThread(&jniEnv1, 0) == JNI_OK) {
            jniEnv1->CallVoidMethod(this->jobject1, this->onDecodeStart, sampleRate, channels, format);
            javaVM->DetachCurrentThread();
        }
    } else {
        jniEnv->CallVoidMethod(this->jobject1, this->onDecodeStart, sampleRate, channels, format);
    }
}

void JNICallback::onDecode(int threadMode, uint8_t *data, int size) {
    if (CHILD_THREAD == threadMode) {
        JNIEnv *jniEnv1 = 0;
        if (javaVM->AttachCurrentThread(&jniEnv1, 0) == JNI_OK) {
            jbyteArray jbyteArray1 = jniEnv1->NewByteArray(size);
            jniEnv1->SetByteArrayRegion(jbyteArray1, 0, size, reinterpret_cast<const jbyte *>(data));
            jniEnv1->CallVoidMethod(this->jobject1, this->onPCMData, jbyteArray1);
            jniEnv1->DeleteLocalRef(jbyteArray1);
            javaVM->DetachCurrentThread();
        }
    } else {
        jbyteArray jbyteArray1 = jniEnv->NewByteArray(size);
        jniEnv->GetByteArrayRegion(jbyteArray1, 0, size, reinterpret_cast<jbyte *>(data));
        jniEnv->CallVoidMethod(this->jobject1, this->onPCMData, jbyteArray1);
        jniEnv->DeleteLocalRef(jbyteArray1);
    }
}
