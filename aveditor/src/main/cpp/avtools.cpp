//
// Created by 阳坤 on 2020-05-21.
//

#include <android/native_window_jni.h>
#include "jni.h"
#include "play/IPlayerProxy.h"

#define NATIVE_MUSIC_ENCODE_PATH "com/devyk/aveditor/jni/AVFileDecodeEngine"

#define JNI_PLAY_JAVA_PATH "com/devyk/aveditor/jni/PlayerEngine"
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))


JavaVM *jVM = 0;

static void Android_JNI_initSurface(JNIEnv *env, jobject instance, jobject surface) {
    ANativeWindow *win = ANativeWindow_fromSurface(env, surface);
    IPlayerProxy::getInstance()->initWindow(win);
/*    IDemux *demux = new KAVDemux();
    IDecode *adecode = new KAVDecode();
    IDecode *vdecode = new KAVDecode();
    IVideoPlayer *videoPlayer = new AV_GL_VideoPlayer();
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
    IPlayerProxy::getInstance()->getTransferInstance()->registerModel(false);
    if (IPlayerProxy::getInstance()->open(IPlayerProxy::getInstance()->getDataSource(), false)) {
        IPlayerProxy::getInstance()->start();
    }
}


/**
 * 解码并且播放
 * @param env
 * @param instance
 */
static void Android_JNI_decodeMusicAlsoPlay(JNIEnv *env, jobject instance) {
    if (IPlayerProxy::getInstance()->open(IPlayerProxy::getInstance()->getDataSource(), false)) {
        IPlayerProxy::getInstance()->start();
    }
}

static jdouble Android_JNI_progress(JNIEnv *env, jobject instance) {
    return IPlayerProxy::getInstance()->playPos();
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
                seek / (double) 100);
}


static void Android_JNI_addRecordMusic(JNIEnv *jniEnv, jobject jobject1, jstring pathFile) {
    const char *url = jniEnv->GetStringUTFChars(pathFile, 0);
    JNICallback *jniCallback = new JNICallback(jVM, jniEnv, jobject1);
    //注册 AVAudioTransfer 模块
    IPlayerProxy::getInstance()->getTransferInstance()->registerModel(true);
    IPlayerProxy::getInstance()->getTransferInstance()->setCallback(jniCallback);
    IPlayerProxy::getInstance()->setDataSource(url);
    jniEnv->ReleaseStringUTFChars(pathFile, url);
}


static void Android_JNI_pause(JNIEnv *jniEnv, jobject jobject1) {
    IPlayerProxy::getInstance()->setPause(true);

}

static void Android_JNI_resume(JNIEnv *jniEnv, jobject jobject1) {
    IPlayerProxy::getInstance()->setPause(false);

}


/**
 * 解码相关函数
 */
static JNINativeMethod NativeMethod[] = {
        {"addRecordMusic", "(Ljava/lang/String;)V", (void *) Android_JNI_addRecordMusic},
        {"start",           "()V",                   (void *) Android_JNI_decodeMusicAlsoPlay},
        {"pause",           "()V",                   (void *) Android_JNI_pause},
        {"resume",          "()V",                   (void *) Android_JNI_resume},
        {"stop",            "()V",                   (void *) Android_JNI_stop}
};

/**
 * 播放相关函数
 */
static JNINativeMethod mNativePlayMethods[] = {
        {"initSurface",   "(Ljava/lang/Object;)V", (void **) Android_JNI_initSurface},
        {"setDataSource", "(Ljava/lang/String;)V", (void **) Android_JNI_setDataSource},
        {"start",         "()V",                   (void **) Android_JNI_start},
        {"setPause",      "(Z)V",                  (void **) Android_JNI_setPause},
        {"stop",          "()V",                   (void **) Android_JNI_stop},
        {"progress",      "()D",                   (void **) Android_JNI_progress},
        {"seekTo",        "(D)I",                  (void **) Android_JNI_seekTo}
};

jint JNI_OnLoad(JavaVM *javaVM, void *pVoid) {
    jVM = javaVM;
    JNIEnv *jniEnv;
    if (javaVM->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_6) != JNI_OK)
        return JNI_ERR;

    jclass javaClass = jniEnv->FindClass(NATIVE_MUSIC_ENCODE_PATH);
    jniEnv->RegisterNatives(javaClass, NativeMethod, sizeof(NativeMethod) / sizeof(NativeMethod[0]));
    jniEnv->DeleteLocalRef(javaClass);

    jclass nativeMethodClass = jniEnv->FindClass(JNI_PLAY_JAVA_PATH);
    jniEnv->RegisterNatives(nativeMethodClass, mNativePlayMethods, NELEM(mNativePlayMethods));
    jniEnv->DeleteLocalRef(nativeMethodClass);


    LOGE("FFMPEG CONFIG %s \n", avutil_configuration());
    LOGE("FFMPEG VERSION%s \n", av_version_info());
    IPlayerProxy::getInstance()->initMediaCodec(pVoid);

    return JNI_VERSION_1_6;

}

