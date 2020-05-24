//
// Created by 阳坤 on 2020-05-21.
//


#include <jni.h>
#include <android/native_window.h>
#include "avplay/demux/KAVDemux.h"
#include "avplay/decode/KAVDecode.h"
#include "avplay/video/KAV_GL_VideoPlayer.h"
#include "avplay/audio/KAV_SL_AudioPlayer.h"
#include "avplay/resample/KAVResample.h"
#include "avplay/IPlayerProxy.h"
#include <android/native_window_jni.h>

#define JNI_PLAY_JAVA_PATH "com/devyk/avedit/KAVPlayView"
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))


static void Android_JNI_initView(JNIEnv *env, jobject instance, jobject surface) {
    ANativeWindow *win = ANativeWindow_fromSurface(env, surface);
    IPlayerProxy::getInstance()->initWindow(win);
/*    IDemux *demux = new KAVDemux();
    IDecode *adecode = new KAVDecode();
    IDecode *vdecode = new KAVDecode();
    IVideoPlayer *videoPlayer = new KAV_GL_VideoPlayer();
    IAudioPlayer *audioPlayer = new KAV_SL_AudioPlayer();
    IResample *resample = new KAVResample();
    int ret = demux->open(
            "http://39.135.34.150:8080/000000001000/1000000001000009115/1.m3u8?channel-id=ystenlive&Contentid=1000000001000009115&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000009115&owchid=ystenlive&owsid=1106497909461209970&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRJ1HPTMtmQf%2bVwcp8RojByB2Rhtn7waHVWUQ9gcJ0mHLEp3xuYtoWp3K%2bdNVn%2bMR4");
//                       );
    if (ret) {
        videoPlayer->close();
        videoPlayer->setRender(win);
        demux->registers(adecode);
        demux->registers(vdecode);
        vdecode->registers(videoPlayer);
        adecode->registers(resample);
        resample->registers(audioPlayer);
        adecode->open(demux->getAInfo());
        vdecode->open(demux->getVInfo(), false);
        AVParameter out = demux->getAInfo();
        resample->open(demux->getAInfo(), out);
        adecode->start();
        vdecode->start();
        demux->start();
        audioPlayer->startPlayer(demux->getAInfo());
    }*/



}

static void Android_JNI_setDataSource(JNIEnv *env, jobject instance, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);
    IPlayerProxy::getInstance()->setDataSource(url);
    env->ReleaseStringUTFChars(url_, url);
}

static void Android_JNI_start(JNIEnv *env, jobject instance) {
    if (IPlayerProxy::getInstance()->open(IPlayerProxy::getInstance()->getDataSource(), false))
        IPlayerProxy::getInstance()->start();

}

static jdouble Android_JNI_progress(JNIEnv *env, jobject instance) {
    return IPlayerProxy::getInstance()->playPos() * 100.00;
}

static void Android_JNI_setPause(JNIEnv *env, jobject instance, jboolean isPause) {
    IPlayerProxy::getInstance()->setPause(isPause);
}

static void Android_JNI_stop(JNIEnv *env, jobject instance) {
    IPlayerProxy::getInstance()->close();
}

static int Android_JNI_seekTo(JNIEnv *env, jobject instance, jdouble seek) {
    if (IPlayerProxy::getInstance()->getTotalDuration() > 0)
        return IPlayerProxy::getInstance()->seekTo(
                seek/(double)100);
}

static JNINativeMethod mNativeMethods[] = {
        {"initView",      "(Ljava/lang/Object;)V", (void **) Android_JNI_initView},
        {"setDataSource", "(Ljava/lang/String;)V", (void **) Android_JNI_setDataSource},
        {"start",         "()V",                   (void **) Android_JNI_start},
        {"setPause",      "(Z)V",                  (void **) Android_JNI_setPause},
        {"stop",          "()V",                   (void **) Android_JNI_stop},
        {"progress",      "()D",                   (void **) Android_JNI_progress},
        {"seekTo",        "(D)I",                  (void **) Android_JNI_seekTo}
};

jint JNI_OnLoad(JavaVM *javaVM, void *pVoid) {
    JNIEnv *jniEnv;
    if (javaVM->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_6) != JNI_OK)
        return JNI_ERR;
    jclass nativeMethodClass = jniEnv->FindClass(JNI_PLAY_JAVA_PATH);
    jniEnv->RegisterNatives(nativeMethodClass, mNativeMethods, NELEM(mNativeMethods));
    jniEnv->DeleteLocalRef(nativeMethodClass);

    LOGE("FFMPEG CONFIG %s \n", avutil_configuration());
    LOGE("FFMPEG VERSION%s \n", av_version_info());
    IPlayerProxy::getInstance()->initMediaCodec(pVoid);
    return JNI_VERSION_1_6;
}