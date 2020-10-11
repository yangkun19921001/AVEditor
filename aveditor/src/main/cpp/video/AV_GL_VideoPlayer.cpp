//
// Created by 阳坤 on 2020-05-22.
//


#include "AV_GL_VideoPlayer.h"


void AV_GL_VideoPlayer::setRender(void *window) {
    this->pNativeWindow = window;

}

void AV_GL_VideoPlayer::render(AVData data) {
    if (!this->isNativeRender) {
        toJava(data);
        return;
    }
    if (!pNativeWindow)return;
    if (!texture) {
        texture = AVTxture::create();
        texture->init(pNativeWindow, (AVTextureType) data.format);
    }

    texture->draw(data.datas, data.width, data.height);
}

void AV_GL_VideoPlayer::close() {
    mux.lock();
    if (texture) {
        texture->drop();
        texture = 0;
    }


    mux.unlock();

}

/**
 * 设置是否在 native 端进行渲染
 * @param isRender
 */
FILE *file_;

void AV_GL_VideoPlayer::setNativeRender(JavaVM *javaVM, JNIEnv *env, jobject obj, int isRender) {
    this->isNativeRender = isRender;
    this->env = env;
    this->jvm = javaVM;

    if (!isNativeRender) {
        //找到方法 ID
        //必须声明全局 不然会报 error JNI DETECTED ERROR IN APPLICATION: use of invalid jobject 0xff868d8c
        this->obj = env->NewGlobalRef(obj);// 坑，需要是全局（jobject一旦涉及到跨函数，跨线程，必须是全局引用）
        if (!this->env || !this->obj)
            return;
        jclass jcls = this->env->GetObjectClass(this->obj);
        this->mYuvToJavaMethodId = this->env->GetMethodID(jcls, "onRecevierFromNativeYUVData", "(II[B[B[B)V");
//        file_ = fopen("sdcard/yuv_test_11.yuv","wb");
    }
}

/**
 * 将数据传递给 Java 端
 * @param data
 */

void AV_GL_VideoPlayer::toJava(AVData data) {
    if (this->env && data.width != 0, data.height != 0) {
        JNIEnv *jniEnv1 = 0;
        if (this->jvm->AttachCurrentThread(&jniEnv1, 0) == JNI_OK) {



//
//            data.datas[0] = static_cast<unsigned char *>(malloc(data.width * data.height));
//            data.datas[1] = static_cast<unsigned char *>(malloc(data.width / 2 * data.height / 2));
//            data.datas[2] = static_cast<unsigned char *>(malloc(data.width / 2 * data.height / 2));
//
//
//            for (int i = 0; i < data.height; i++) {
//                memcpy(data.datas[0] + data.width * i,
//                       pFrame->data[0] + pFrame->linesize[0] * i,
//                       data.width);
//            }
//            for (int j = 0; j < data.height / 2; j++) {
//                memcpy(data.datas[1] + data.width / 2 * j,
//                       pFrame->data[1] + pFrame->linesize[1] * j,
//                       data.width / 2);
//            }
//            for (int k = 0; k < data.height / 2; k++) {
//                memcpy(data.datas[2] + data.width / 2 * k,
//                       pFrame->data[2] + pFrame->linesize[2] * k,
//                       data.width / 2);
//            }

//            fwrite(data.datas[0], 1, data.width * data.height, file_);    //Y
//            fwrite(data.datas[1], 1, data.width * data.height / 4, file_);  //U
//            fwrite(data.datas[2], 1, data.width * data.height / 4, file_);  //V

            int yLen = data.width * data.height;
            int uLen = data.width * data.height / 4;
            int vLen = data.width * data.height / 4;

            //y
            jbyteArray y = jniEnv1->NewByteArray(yLen);
             jbyte *data_y = reinterpret_cast<jbyte *>(data.datas[0]);


            //u
            jbyteArray u = jniEnv1->NewByteArray(uLen);
            jbyte *data_u = reinterpret_cast<jbyte *>(data.datas[1]);

            //v
            jbyteArray v = jniEnv1->NewByteArray(vLen);
            jbyte *data_v = reinterpret_cast<jbyte *>(data.datas[2]);

            //设置 YUV 数据
            jniEnv1->SetByteArrayRegion(y, 0, yLen, data_y);
            jniEnv1->SetByteArrayRegion(u, 0, uLen, data_u);
            jniEnv1->SetByteArrayRegion(v, 0, vLen, data_v);

            //回调给 Java
            jniEnv1->CallVoidMethod(this->obj, this->mYuvToJavaMethodId, data.width, data.height, y, u, v);

            //释放 JNI
            jniEnv1->DeleteLocalRef(y);
            jniEnv1->DeleteLocalRef(u);
            jniEnv1->DeleteLocalRef(v);

            this->jvm->DetachCurrentThread();



            //释放申请的内存空间
            free(data.datas[0]);
            free(data.datas[1]);
            free(data.datas[2]);

            //释放 裸流数据
            AVFrame *pFrame = reinterpret_cast<AVFrame *>(data.data);
            av_frame_free(&pFrame);

            //删除 data
            data.drop();
        }
    }

}
