//
// Created by 阳坤 on 2020-05-21.
//

#include <android/native_window_jni.h>
#include <entity/MediaEntity.h>
#include <muxer/IMuxer.h>
#include <muxer/AVMuxer.h>
#include "jni.h"
#include "play/IPlayerProxy.h"
#include "builder/AVToolsBuilder.h"

#define NATIVE_MUSIC_ENCODE_PATH "com/devyk/aveditor/jni/AVAudioDecodeEngine"
#define JNI_PLAY_JAVA_PATH "com/devyk/aveditor/jni/AVPlayerEngine"
#define JNI_MUXER_JAVA_PATH "com/devyk/aveditor/jni/AVMuxerEngine"
#define JNI_SPEED_JAVA_PATH "com/devyk/aveditor/jni/AVSpeedEngine"
#define JNI_EDITOR_JAVA_PATH "com/devyk/aveditor/jni/AVEditorEngine"
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))


JavaVM *jVM = 0;
IMuxer *muxer = 0;



//--------------------------------------------------- 播放器相关 API -----------------------------------------------------//

static void Android_JNI_PLAY_initSurface(JNIEnv *env, jobject instance, jobject surface) {
    ANativeWindow *win = ANativeWindow_fromSurface(env, surface);

    AVToolsBuilder::getInstance()->getPlayEngine()->initWindow(win);
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

static void Android_JNI_PLAY_setDataSource(JNIEnv *env, jobject instance, jstring url_) {
    const char *url = env->GetStringUTFChars(url_, 0);
    AVToolsBuilder::getInstance()->getPlayEngine()->setDataSource(url);
    env->ReleaseStringUTFChars(url_, url);
}

static void Android_JNI_PLAY_setDataSources(JNIEnv *env, jobject instance, jobject lists) {
    AVToolsBuilder::getInstance()->getPlayEngine()->setDataSource(env, lists);
}

static void Android_JNI_PLAY_setMediaCodec(JNIEnv *env, jobject instance, jboolean isMediacodec) {
    AVToolsBuilder::getInstance()->getPlayEngine()->setMediaCodec(isMediacodec);
}

static void Android_JNI_PLAY_start(JNIEnv *env, jobject instance) {
    AVToolsBuilder::getInstance()->getPlayEngine()->getTransferInstance()->registerModel(false);
    if (AVToolsBuilder::getInstance()->getPlayEngine()->open(
            AVToolsBuilder::getInstance()->getPlayEngine()->getDataSource(),
            AVToolsBuilder::getInstance()->getPlayEngine()->isMediaCodec())) {
        AVToolsBuilder::getInstance()->getPlayEngine()->start();
    }
}


static jdouble Android_JNI_PLAY_progress(JNIEnv *env, jobject instance) {
    return AVToolsBuilder::getInstance()->getPlayEngine()->playPos();
}

static void Android_JNI_PLAY_setPause(JNIEnv *env, jobject instance, jboolean isPause) {
    AVToolsBuilder::getInstance()->getPlayEngine()->setPause(isPause);
}

static void Android_JNI_PLAY_stop(JNIEnv *env, jobject instance) {

    if (AVToolsBuilder::getInstance()->getPlayEngine()->getTransferInstance()->REGISTER_AUDIO_TRANSFER_MODEL) {
        AVToolsBuilder::getInstance()->getPlayEngine()->getTransferInstance()->onDecodeStop();
    }


    AVToolsBuilder::getInstance()->getPlayEngine()->close();


}


static void Android_JNI_PLAY_setPlayVolume(JNIEnv *env, jobject instance, jint volume) {
    AVToolsBuilder::getInstance()->getPlayEngine()->setPlayVolume(volume);
}

static void Android_JNI_PLAY_setPlaySpeed(JNIEnv *env, jobject instance, jdouble volume) {
    AVToolsBuilder::getInstance()->getPlayEngine()->setPlaySpeed(volume);
}

static int Android_JNI_PLAY_seekTo(JNIEnv *env, jobject instance, jdouble seek) {
    if (AVToolsBuilder::getInstance()->getPlayEngine()->getTotalDuration() > 0)
        return AVToolsBuilder::getInstance()->getPlayEngine()->seekTo(
                seek / (double) 100);
    return 0;
}

static void Android_JNI_pause(JNIEnv *jniEnv, jobject jobject1) {
    AVToolsBuilder::getInstance()->getPlayEngine()->setPause(true);

}

static void Android_JNI_resume(JNIEnv *jniEnv, jobject jobject1) {
    AVToolsBuilder::getInstance()->getPlayEngine()->setPause(false);

}

//--------------------------------------------------- 播放器相关 API -----------------------------------------------------//

//--------------------------------------------------- MP3 边解码边播放 -----------------------------------------------------//
/**
 * 解码并且播放
 * @param env
 * @param instance
 */
static void Android_JNI_decodeMusicAlsoPlay(JNIEnv *env, jobject instance) {
    if (AVToolsBuilder::getInstance()->getPlayEngine()->open(
            AVToolsBuilder::getInstance()->getPlayEngine()->getDataSource(),
            AVToolsBuilder::getInstance()->getPlayEngine()->isMediaCodec())) {
        AVToolsBuilder::getInstance()->getPlayEngine()->start();
    }
}


static void Android_JNI_addRecordMusic(JNIEnv *jniEnv, jobject jobject1, jstring pathFile) {
    const char *url = jniEnv->GetStringUTFChars(pathFile, 0);
    JNICallback *jniCallback = new JNICallback(jVM, jniEnv, jobject1);
    //注册 AVAudioTransfer 模块
    AVToolsBuilder::getInstance()->getPlayEngine()->getTransferInstance()->registerModel(true);
    AVToolsBuilder::getInstance()->getPlayEngine()->getTransferInstance()->setCallback(jniCallback);
    AVToolsBuilder::getInstance()->getPlayEngine()->setDataSource(url);
    jniEnv->ReleaseStringUTFChars(pathFile, url);
}

//--------------------------------------------------- 封装器 API -----------------------------------------------------//
/**
 * 初始化封装器
 * @param jniEnv
 * @param jobject1
 * @param outPath
 * @param outformat
 *   const char *outPath, const char *outFormat = MP4,
            int video_width, int video_height,
            int frame_rate, int video_bit_rate,
            int audio_sample_rate, int audio_channels, int audio_bit_rate
 */
static void Android_JNI_muxer_initMuxer(JNIEnv *jniEnv, jobject jobject1,
                                        jstring outPath,
                                        jint video_width, jint video_height,
                                        jint frame_rate, jint video_bit_rate,
                                        jint audio_sample_rate, jint audio_channels, jint audio_bit_rate
) {
    const char *outUrl = jniEnv->GetStringUTFChars(outPath, 0);

    if (!muxer)
        muxer = new AVMuxer();
    int ret = muxer->init(outUrl, video_width, video_height, frame_rate, video_bit_rate, audio_sample_rate,
                          audio_channels, audio_bit_rate, "DevYK -> AVToos");
    if (ret >= 0)
        muxer->start();
    jniEnv->ReleaseStringUTFChars(outPath, outUrl);

}

/**
 * 将音视频数据入队
 * @param jniEnv
 * @param jobject1
 * @param bytes
 */

static void
Android_JNI_muxer_enqueue(JNIEnv *jniEnv, jobject jobject1, jbyteArray bytes, jboolean isAudio, jlong pts
) {
    jbyte *jbyte1 = jniEnv->GetByteArrayElements(bytes, 0);
    jsize size = jniEnv->GetArrayLength(bytes);

    AVData data;
    if (muxer) {
        data.alloc(size, reinterpret_cast<const char *>(jbyte1));
        data.pts = pts;
        data.size = size;
        if (isAudio) {
            data.isAudio = true;
            LOGI("size and position is  Android_JNI_muxer_enqueue {%lld, %d,%lld}", pts, size, data.pts);
        }
        muxer->enqueue(data);
        delete[] data.data;
    }
    jniEnv->ReleaseByteArrayElements(bytes, jbyte1, 0);
}


/**
 * 关闭封装器
 * @param jniEnv
 * @param jobject1
 */
static void Android_JNI_muxer_close(JNIEnv *jniEnv, jobject jobject1) {
    if (muxer) {
        muxer->close();
        muxer = 0;
    }

}

//--------------------------------------------------- 封装器 API -----------------------------------------------------//


//------------------------------------------------ 音频变速 ----------------------------------------------------------------------//
/**
 * 初始化 Speed 控制器
 * @param jniEnv
 * @param jobject1
 * @param track
 * @param channels
 * @param sampleingRate
 * @param tempo
 * @param pitchSemi
 */
static void Android_JNI_SPEED_initSpeedController(JNIEnv *jniEnv, jobject jobject1,
                                                  jint track, jint channels,
                                                  jint sampleingRate,
                                                  jdouble tempo,
                                                  jdouble pitchSemi) {

    AVToolsBuilder::getInstance()->getSoundTouchEngine()->initSpeedController(track, channels, sampleingRate, tempo,
                                                                              pitchSemi);


}

/**
 * 改变速率
 */
static jint Android_JNI_SPEED_putData(JNIEnv *jniEnv, jobject jobject1,
                                      jint track, jbyteArray pcm, jint size) {

    int outSize = 0;
    jbyte *inPcm = jniEnv->GetByteArrayElements(pcm, 0);

    outSize = AVToolsBuilder::getInstance()->getSoundTouchEngine()->putData(track, reinterpret_cast<uint8_t *>(inPcm),
                                                                            size
    );
    jniEnv->ReleaseByteArrayElements(pcm, inPcm, 0);
//
    return outSize;
}

/**
 * 改变速率
 */
static jint Android_JNI_SPEED_getData(JNIEnv *jniEnv, jobject jobject1,
                                      jint track, jshortArray out, jint size) {

    int outSize = 0;
    jshort *outPcm = jniEnv->GetShortArrayElements(out, 0);
    outSize = AVToolsBuilder::getInstance()->getSoundTouchEngine()->getData(track,
                                                                            &outPcm, size
    );

    jniEnv->ReleaseShortArrayElements(out, outPcm, 0);
//
    return outSize;
}


/**
 * 关闭 soundtouch
 */
static void Android_JNI_SPEED_close(JNIEnv *jniEnv, jobject jobject1,
                                    jint track) {
    AVToolsBuilder::getInstance()->getSoundTouchEngine()->close(track
    );
}

/**
 * 设置录制的 speed
 */
static void Android_JNI_SPEED_setRecordSpeed(JNIEnv *jniEnv, jobject jobject1,
                                             jint track, jdouble speed) {
    AVToolsBuilder::getInstance()->getSoundTouchEngine()->setSpeed(track, speed
    );
}

//------------------------------------------------ 音频变速 ----------------------------------------------------------------------//


//------------------------------------------------ 音视频编辑 ----------------------------------------------------------------------//
static void Android_JNI_EDITOR_avStartMerge(JNIEnv *jniEnv, jobject jobject1, jstring outPath, jstring mediaFormat) {
    deque<MediaEntity *> mediaLists = AVToolsBuilder::getInstance()->getPlayEngine()->getDataSources();
    const char *outUrl = jniEnv->GetStringUTFChars(outPath, 0);
    if (mediaLists.size() > 0 && AVToolsBuilder::getInstance()->getEditorEngine()->open(outUrl, mediaLists) == 1) { ;
        AVToolsBuilder::getInstance()->getEditorEngine()->start();
    }

    jniEnv->ReleaseStringUTFChars(outPath, outUrl);
}

static jint Android_JNI_EDITOR_avMergeProgress(JNIEnv *jniEnv, jobject jobject1) {
    return 0;
}

static void Android_JNI_EDITOR_addAVFile(JNIEnv *jniEnv, jobject jobject1, jobject mediaEntity) {}

static void Android_EDITOR_JNI_insertAVFile(JNIEnv *jniEnv, jobject jobject1, jint index, jobject mediaEntity) {}

static void Android_JNI_EDITOR_addAVFiles(JNIEnv *jniEnv, jobject jobject1, jobject lists) {}

static void Android_JNI_EDITOR_insertAVFiles(JNIEnv *jniEnv, jobject jobject1, jint index, jobject lists) {}

static void
Android_JNI_EDITOR_addMusicFile(JNIEnv *jniEnv, jobject jobject1, jlong startDur, jlong stopDur, jstring inPath,
                                jint bgVolume, jint musicVolume) {}

static void Android_JNI_EDITOR_removeAVFile(JNIEnv *jniEnv, jobject jobject1, jint index) {}
//------------------------------------------------ 音视频编辑 ----------------------------------------------------------------------//

/**
 * 解码相关函数
 */
static JNINativeMethod NativeMethod[] = {
        {"addRecordMusic", "(Ljava/lang/String;)V", (void *) Android_JNI_addRecordMusic},
        {"start",          "()V",                   (void *) Android_JNI_decodeMusicAlsoPlay},
        {"pause",          "()V",                   (void *) Android_JNI_pause},
        {"resume",         "()V",                   (void *) Android_JNI_resume},
        {"stop",           "()V",                   (void *) Android_JNI_PLAY_stop}
};

/**
 * 播放相关函数
 */
static JNINativeMethod mNativePlayMethods[] = {
        {"initSurface",   "(Ljava/lang/Object;)V",    (void **) Android_JNI_PLAY_initSurface},
        {"setDataSource", "(Ljava/lang/String;)V",    (void **) Android_JNI_PLAY_setDataSource},
        {"setDataSource", "(Ljava/util/ArrayList;)V", (void **) Android_JNI_PLAY_setDataSources},
        {"start",         "()V",                      (void **) Android_JNI_PLAY_start},
        {"setMediaCodec", "(Z)V",                     (void **) Android_JNI_PLAY_setMediaCodec},
        {"setPause",      "(Z)V",                     (void **) Android_JNI_PLAY_setPause},
        {"stop",          "()V",                      (void **) Android_JNI_PLAY_stop},
        {"progress",      "()D",                      (void **) Android_JNI_PLAY_progress},
        {"setPlaySpeed",  "(D)V",                     (void **) Android_JNI_PLAY_setPlaySpeed},
        {"setPlayVolume", "(I)V",                     (void **) Android_JNI_PLAY_setPlayVolume},
        {"seekTo",        "(D)I",                     (void **) Android_JNI_PLAY_seekTo}
};


/**
 * 音视频编辑
 */
static JNINativeMethod mNativeEditorMethods[] = {
        {"avStartMerge",    "(Ljava/lang/String;Ljava/lang/String;)V",     (void **) Android_JNI_EDITOR_avStartMerge},
        {"avMergeProgress", "()I",                                         (void **) Android_JNI_EDITOR_avMergeProgress},
        {"addAVFile",       "(Lcom/devyk/aveditor/entity/MediaEntity;)V",  (void **) Android_JNI_EDITOR_addAVFile},
        {"insertAVFile",    "(ILcom/devyk/aveditor/entity/MediaEntity;)V", (void **) Android_EDITOR_JNI_insertAVFile},
        {"addAVFiles",      "(Ljava/util/ArrayList;)V",                    (void **) Android_JNI_EDITOR_addAVFiles},
        {"insertAVFiles",   "(ILjava/util/ArrayList;)V",                   (void **) Android_JNI_EDITOR_insertAVFiles},
        {"addMusicFile",    "(JJLjava/lang/String;II)V",                   (void **) Android_JNI_EDITOR_addMusicFile},
        {"removeAVFile",    "(I)V",                                        (void **) Android_JNI_EDITOR_removeAVFile}
};


/**
 * 音视频封装包相关
 */
static JNINativeMethod mNativeMuxerMethods[] = {
        {"native_initMuxer", "(Ljava/lang/String;IIIIIII)V", (void **) Android_JNI_muxer_initMuxer},
        {"native_enqueue",   "([BZJ)V",                      (void **) Android_JNI_muxer_enqueue},
        {"native_close",     "()V",                          (void **) Android_JNI_muxer_close},
};

/**
 * 速率控制
 */
static JNINativeMethod mNativeSpeedMethods[] = {
        {"initSpeedController", "(IIIDD)V", (void **) Android_JNI_SPEED_initSpeedController},
        {"putData",             "(I[BI)I",  (void **) Android_JNI_SPEED_putData},
        {"getData",             "(I[SI)I",  (void **) Android_JNI_SPEED_getData},
        {"close",               "(I)V",     (void **) Android_JNI_SPEED_close},
        {"setRecordSpeed",      "(ID)V",    (void **) Android_JNI_SPEED_setRecordSpeed},

};

/**
 * JNI 退出执行
 * @param vm
 * @param reserved 拿到  Java 虚拟机的唯一指针变量
 */
void JNI_OnUnload(JavaVM *vm, void *reserved) {

}

/**
 * System.load 执行
 * @param vm
 * @param reserved 拿到  Java 虚拟机的唯一指针变量
 */
jint JNI_OnLoad(JavaVM *javaVM, void *pVoid) {
    jVM = javaVM;
    JNIEnv *jniEnv;
    if (javaVM->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_6) != JNI_OK)
        return JNI_ERR;

    //Mp3 文件解码
    jclass javaClass = jniEnv->FindClass(NATIVE_MUSIC_ENCODE_PATH);
    jniEnv->RegisterNatives(javaClass, NativeMethod, sizeof(NativeMethod) / sizeof(NativeMethod[0]));
    jniEnv->DeleteLocalRef(javaClass);

    //播放
    jclass nativePlayMethodClass = jniEnv->FindClass(JNI_PLAY_JAVA_PATH);
    jniEnv->RegisterNatives(nativePlayMethodClass, mNativePlayMethods, NELEM(mNativePlayMethods));
    jniEnv->DeleteLocalRef(nativePlayMethodClass);


    //封装器
    jclass nativeMuxerMethodClass = jniEnv->FindClass(JNI_MUXER_JAVA_PATH);
    jniEnv->RegisterNatives(nativeMuxerMethodClass, mNativeMuxerMethods, NELEM(mNativeMuxerMethods));
    jniEnv->DeleteLocalRef(nativeMuxerMethodClass);

    //速率控制
    jclass nativeSpeedMethodClass = jniEnv->FindClass(JNI_SPEED_JAVA_PATH);
    jniEnv->RegisterNatives(nativeSpeedMethodClass, mNativeSpeedMethods, NELEM(mNativeSpeedMethods));
    jniEnv->DeleteLocalRef(nativeSpeedMethodClass);

    //音视频编辑
    jclass nativeEditorMethodClass = jniEnv->FindClass(JNI_EDITOR_JAVA_PATH);
    jniEnv->RegisterNatives(nativeEditorMethodClass, mNativeEditorMethods, NELEM(mNativeEditorMethods));
    jniEnv->DeleteLocalRef(nativeEditorMethodClass);

    LOGE("FFMPEG CONFIG %s \n", avutil_configuration());
    LOGE("FFMPEG VERSION%s \n", av_version_info());


    if (AVToolsBuilder::getInstance()->getPlayEngine()->initMediaCodec(javaVM) == 0) {
        LOGE("FFMPEG MediaCodec init success! \n");
    } else {
        LOGE("FFMPEG MediaCodec init error! \n");
    }


    return JNI_VERSION_1_6;

}

