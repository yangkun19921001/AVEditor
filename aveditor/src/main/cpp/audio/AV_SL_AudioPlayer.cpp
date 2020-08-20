//
// Created by 阳坤 on 2020-05-22.
//

#include "AV_SL_AudioPlayer.h"

static SLObjectItf engineSL = NULL;
static SLEngineItf eng = NULL;
static SLObjectItf mix = NULL;
static SLObjectItf player = NULL;
static SLPlayItf iplayer = NULL;
static SLAndroidSimpleBufferQueueItf pcmQue = NULL;

AV_SL_AudioPlayer::AV_SL_AudioPlayer() {
    this->buffer = new unsigned char[1024 * 1024];
}

AV_SL_AudioPlayer::~AV_SL_AudioPlayer() {
    delete buffer;
    buffer = 0;
}

/**
 * 播放完一帧的回调
 * @param bf
 * @param contex
 */
static void play_pcm_callback(SLAndroidSimpleBufferQueueItf bf, void *contex) {
    AV_SL_AudioPlayer *ap = (AV_SL_AudioPlayer *) contex;
    if (!ap) {
        LOGE("PcmCall failed contex is null!");
        return;
    }
    ap->playCallback((void *) bf);
}

int AV_SL_AudioPlayer::startPlayer(AVParameter parameter) {
    //先设置暂停，有数据就恢复
    close();
    mux.lock();
    //1 创建引擎
    eng = createSL();
    if (eng) {
        LOGI("CreateSL success！ ");
    } else {
        mux.unlock();
        LOGE("CreateSL failed！ ");
        return false;
    }

    //2 创建混音器

    SLresult re = 0;
    re = (*eng)->CreateOutputMix(eng, &mix, 0, 0, 0);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        LOGE("SL_RESULT_SUCCESS failed!");
        return false;
    }
    re = (*mix)->Realize(mix, SL_BOOLEAN_FALSE);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        LOGE("(*mix)->Realize failed!");
        return false;
    }
    SLDataLocator_OutputMix outmix = {SL_DATALOCATOR_OUTPUTMIX, mix};
    SLDataSink audioSink = {&outmix, 0};

    //3 配置音频信息
    //缓冲队列
    SLDataLocator_AndroidSimpleBufferQueue que = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 10};
    //音频格式
    SLDataFormat_PCM pcm = {
            SL_DATAFORMAT_PCM,
            (SLuint32) parameter.channels,//    声道数
//            (SLuint32) parameter.sample_rate * 1000,
            static_cast<SLuint32>(OpenSLSampleRate((SLuint32) parameter.sample_rate * 1000)),
            SL_PCMSAMPLEFORMAT_FIXED_16,
            SL_PCMSAMPLEFORMAT_FIXED_16,
//            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
            static_cast<SLuint32>(GetChannelMask(parameter.channels)),
            SL_BYTEORDER_LITTLEENDIAN //字节序，小端
    };
    SLDataSource ds = {&que, &pcm};


    //4 创建播放器
    const SLInterfaceID ids[] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[] = {SL_BOOLEAN_TRUE};
    re = (*eng)->CreateAudioPlayer(eng, &player, &ds, &audioSink, sizeof(ids) / sizeof(SLInterfaceID), ids, req);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        LOGE("CreateAudioPlayer failed! %d", re);
        return false;
    } else {
        LOGI("CreateAudioPlayer success!");
    }
    (*player)->Realize(player, SL_BOOLEAN_FALSE);
    //获取player接口
    re = (*player)->GetInterface(player, SL_IID_PLAY, &iplayer);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        LOGE("GetInterface SL_IID_PLAY failed!");
        return false;
    }
    re = (*player)->GetInterface(player, SL_IID_BUFFERQUEUE, &pcmQue);
    if (re != SL_RESULT_SUCCESS) {
        mux.unlock();
        LOGE("GetInterface SL_IID_BUFFERQUEUE failed!");
        return false;
    }

    //设置回调函数，播放队列空调用
    (*pcmQue)->RegisterCallback(pcmQue, play_pcm_callback, this);

    //设置为播放状态
    (*iplayer)->SetPlayState(iplayer, SL_PLAYSTATE_PLAYING);

    //启动队列回调
    (*pcmQue)->Enqueue(pcmQue, "", 1);
    isExit = false;
    mux.unlock();

    LOGI("SLAudioPlay::StartPlay success!");
    return true;
}


void AV_SL_AudioPlayer::close() {
    IAudioPlayer::clear();
    mux.lock();
    //停止播放
    if (iplayer && (*iplayer)) {
        (*iplayer)->SetPlayState(iplayer, SL_PLAYSTATE_STOPPED);
    }
    //清理播放队列
    if (pcmQue && (*pcmQue)) {
        (*pcmQue)->Clear(pcmQue);
    }
    //销毁player对象
    if (player && (*player)) {
        (*player)->Destroy(player);
    }
    //销毁混音器
    if (mix && (*mix)) {
        (*mix)->Destroy(mix);
    }

    //销毁播放引擎
    if (engineSL && (*engineSL)) {
        (*engineSL)->Destroy(engineSL);
    }

    engineSL = NULL;
    eng = NULL;
    mix = NULL;
    player = NULL;
    iplayer = NULL;
    pcmQue = NULL;
    mux.unlock();

}

void AV_SL_AudioPlayer::playCallback(void *p) {
    if (!p)return;


    SLAndroidSimpleBufferQueueItf bf = (SLAndroidSimpleBufferQueueItf) p;
    //XLOGE("SLAudioPlay::PlayCall");
    //阻塞
    AVData d = getData();
    if (d.size <= 0) {
        LOGE("GetData() size is 0");
        return;
    }
    if (!buffer)
        return;
    memcpy(buffer, d.data, d.size);
    mux.lock();
    if (pcmQue && (*pcmQue))
        (*pcmQue)->Enqueue(pcmQue, buffer, d.size);
    mux.unlock();
    d.drop();

}

SLEngineItf AV_SL_AudioPlayer::createSL() {
    SLresult re;
    SLEngineItf en;
    re = slCreateEngine(&engineSL, 0, 0, 0, 0, 0);
    if (re != SL_RESULT_SUCCESS) return NULL;
    re = (*engineSL)->Realize(engineSL, SL_BOOLEAN_FALSE);
    if (re != SL_RESULT_SUCCESS) return NULL;
    re = (*engineSL)->GetInterface(engineSL, SL_IID_ENGINE, &en);
    if (re != SL_RESULT_SUCCESS) return NULL;
    return en;
}

int AV_SL_AudioPlayer::OpenSLSampleRate(SLuint32 sampleRate) {
    int samplesPerSec = SL_SAMPLINGRATE_44_1;
    switch (sampleRate) {
        case 8000:
            samplesPerSec = SL_SAMPLINGRATE_8;
            break;
        case 11025:
            samplesPerSec = SL_SAMPLINGRATE_11_025;
            break;
        case 12000:
            samplesPerSec = SL_SAMPLINGRATE_12;
            break;
        case 16000:
            samplesPerSec = SL_SAMPLINGRATE_16;
            break;
        case 22050:
            samplesPerSec = SL_SAMPLINGRATE_22_05;
            break;
        case 24000:
            samplesPerSec = SL_SAMPLINGRATE_24;
            break;
        case 32000:
            samplesPerSec = SL_SAMPLINGRATE_32;
            break;
        case 44100:
            samplesPerSec = SL_SAMPLINGRATE_44_1;
            break;
        case 48000:
            samplesPerSec = SL_SAMPLINGRATE_48;
            break;
        case 64000:
            samplesPerSec = SL_SAMPLINGRATE_64;
            break;
        case 88200:
            samplesPerSec = SL_SAMPLINGRATE_88_2;
            break;
        case 96000:
            samplesPerSec = SL_SAMPLINGRATE_96;
            break;
        case 192000:
            samplesPerSec = SL_SAMPLINGRATE_192;
            break;
        default:
            samplesPerSec = SL_SAMPLINGRATE_44_1;
    }
    return samplesPerSec;
}

int AV_SL_AudioPlayer::GetChannelMask(int channels) {
    int channelMask = SL_SPEAKER_FRONT_CENTER;
    switch (channels) {
        case 1:
            channelMask = SL_SPEAKER_FRONT_CENTER;
            break;
        case 2:
            channelMask = SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT;
            break;
    }
    return channelMask;
}


/**
 * 初始化变音/变速器
 * @param sampleRate
 * @param channels
 * @param speed
 */
void AV_SL_AudioPlayer::initSoundTouch(int sampleRate, int channels, int speed) {

}
