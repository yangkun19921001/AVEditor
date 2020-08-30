//
// Created by 阳坤 on 2020-05-22.
//


#include <builder/AVToolsBuilder.h>
#include "AV_SL_AudioPlayer.h"

static SLObjectItf engineSL = NULL;
static SLEngineItf eng = NULL;
static SLObjectItf mix = NULL;
static SLObjectItf player = NULL;
static SLPlayItf iplayer = NULL;
static SLAndroidSimpleBufferQueueItf pcmQue = NULL;
//音量
static SLVolumeItf pcmVolumePlay = NULL;

AV_SL_AudioPlayer::AV_SL_AudioPlayer() {

}

AV_SL_AudioPlayer::~AV_SL_AudioPlayer() {
    delete[]buffer;
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
    this->buffer = new unsigned char[parameter.sample_rate * parameter.channels * 2];
    this->sd_buffer = new SAMPLETYPE[parameter.sample_rate * parameter.channels * 2];
    //初始化速率控制
    AVToolsBuilder::getInstance()->getSoundTouchEngine()->initSpeedController(parameter.channels,
                                                                              parameter.sample_rate, mPlaySpeed, 1);
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


    //设置音量接口
    (*player)->GetInterface(player, SL_IID_VOLUME, &pcmVolumePlay);
    setPlayVolume(this->curVolume);


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

    AVToolsBuilder::getInstance()->getSoundTouchEngine()->close();
//    finish()
    engineSL = NULL;
    eng = NULL;
    mix = NULL;
    player = NULL;
    iplayer = NULL;
    pcmQue = NULL;
    pcmVolumePlay = NULL;
    mux.unlock();

}

//void AV_SL_AudioPlayer::playCallback(void *p) {
//    if (!p)return;
//
//
//    SLAndroidSimpleBufferQueueItf bf = (SLAndroidSimpleBufferQueueItf) p;
//    //XLOGE("SLAudioPlay::PlayCall");
//
//  int size  =   readPcmData();
//
//    //阻塞
//    AVData d = getData();
//    if (d.size <= 0) {
//        LOGE("GetData() size is 0");
//        return;
//    }
//    if (!buffer)
//        return;
//    memcpy(buffer, d.data, d.size);
//
//    d.size = AVToolsBuilder::getInstance()->getSoundTouchEngine()->soundtouch(buffer,
//                                                                              &sd_buffer, d.size);
////    d.size = setSoundTouchData(d.size);
//    if (d.size <= 0)
//        return;
//
//    mux.lock();
//    if (pcmQue && (*pcmQue))
//        (*pcmQue)->Enqueue(pcmQue, sd_buffer, d.size);
//    mux.unlock();
//    d.drop();
//
//}

void AV_SL_AudioPlayer::playCallback(void *p) {
    if (!p)return;


    SLAndroidSimpleBufferQueueItf bf = (SLAndroidSimpleBufferQueueItf) p;
    //XLOGE("SLAudioPlay::PlayCall");

    int size = readPcmData();

    if (size <= 0)
        return;

    mux.lock();
    if (pcmQue && (*pcmQue))
        (*pcmQue)->Enqueue(pcmQue, sd_buffer, size);
    mux.unlock();

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

int AV_SL_AudioPlayer::readPcmData() {
    int num = 0;
    while (!isExit) {
        //阻塞
        AVData d = getData();
        if (d.size <= 0) {
            LOGE("GetData() size is 0");
            return 0;
        }
        memcpy(buffer, d.data, d.size);
        free(d.data);
        num = AVToolsBuilder::getInstance()->getSoundTouchEngine()->soundtouch(buffer, &sd_buffer, d.size);
        if (num == 0)
            continue;
//        d.drop();

        return num;
    }
}

/**
 * 设置播放的速率
 * @param v
 */
void AV_SL_AudioPlayer::setPlaySpeed(double v) {
    this->mPlaySpeed = v;
    AVToolsBuilder::getInstance()->getSoundTouchEngine()->setSpeed(this->mPlaySpeed);
}

/**
 * 设置播放的音量
 * @param percent
 */
void AV_SL_AudioPlayer::setPlayVolume(int percent) {
    this->curVolume = percent;
    if (pcmVolumePlay != NULL) {
        if (percent > 30) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -20);
        } else if (percent > 25) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -22);
        } else if (percent > 20) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -25);
        } else if (percent > 15) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -28);
        } else if (percent > 10) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -30);
        } else if (percent > 5) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -34);
        } else if (percent > 3) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -37);
        } else if (percent > 0) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -40);
        } else {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -100);
        }
    }
}





