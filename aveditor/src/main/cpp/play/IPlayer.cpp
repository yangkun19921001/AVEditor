//
// Created by 阳坤 on 2020-05-23.
//

#include <builder/AVToolsBuilder.h>
#include "IPlayer.h"
#include "../builder/AVPlayerBuilder.h"

IPlayer *IPlayer::getInstance(unsigned char index) {
    static IPlayer p[188];
    return &p[index];
}

int IPlayer::open(const char *path, int mediacodec) {
    close();
    mux.lock();
    //解封装
    if (!demux || !demux->open(path)) {
        mux.unlock();
        LOGE("demux->Open %s failed!", path);
        return 0;
    }

    //解码 解码可能不需要，如果是解封之后就是原始数据
    if (!vdecode || !vdecode->open(demux->getVInfo(), mediacodec)) {
        LOGE("vdecode->Open %s failed!", path);
//        return false;
    }
    if (!adecode || !adecode->open(demux->getAInfo())) {
        LOGE("adecode->Open %s failed!", path);
//        return false;
    }

    //重采样 有可能不需要，解码后或者解封后可能是直接能播放的数据
    //if(outPara.sample_rate <= 0)
    outPara = demux->getAInfo();
    /**
        * 将音频信息传递给转移模块
        */
    if (transfer && transfer->REGISTER_AUDIO_TRANSFER_MODEL) {
        transfer->open(outPara);
    }

    if (!resample || !resample->open(demux->getAInfo(), outPara)) {
        LOGE("resample->Open %s failed!", path);
    }
    isPlayComplete = true;
    totalDuration = demux->totalDuration;

    mux.unlock();
    return 1;
}

void IPlayer::close() {
    LOGD("IPlayer:close in");
    mux.lock();
    //2 先关闭主体线程，再清理观察者
    //同步线程
    IThread::stop();
    LOGD("IPlayer:close in 1");
    //解封装
    if (demux) {
        demux->stop();

    }
    LOGD("IPlayer:close in 2");
    //解码
    if (vdecode) {
        vdecode->stop();
    }
    LOGD("IPlayer:close in 3");
    if (adecode) {
        adecode->stop();
    }
    LOGD("IPlayer:close in 4");
    if (audioPlay) {
        audioPlay->stop();
    }
    LOGD("IPlayer:close in 5");
    //2 清理缓冲队列
    if (vdecode) {
        vdecode->clear();
    }

    LOGD("IPlayer:close in 6");
    if (adecode) {
        adecode->clear();
    }

    LOGD("IPlayer:close in 7");
    if (audioPlay) {
        audioPlay->clear();
    }
    LOGD("IPlayer:close in 8");
    //3 清理资源
    if (audioPlay) {
        audioPlay->close();
    }
    LOGD("IPlayer:close in 9");
    if (videoView) {
        videoView->close();
    }

    LOGD("IPlayer:close in 10");
    if (vdecode) {
        vdecode->close();
    }
    LOGD("IPlayer:close in 11");
    if (adecode) {
        adecode->close();
    }
    LOGD("IPlayer:close in 12");
    if (demux) {
        demux->close();
    }
    mux.unlock();
    LOGD("IPlayer: close out");

}

void IPlayer::initView(void *win) {
    if (videoView) {
        videoView->close();
        videoView->setRender(win);
    }

}

int IPlayer::seekTo(double pos) {
    bool re = false;
    if (!demux) return false;

    //暂停所有线程
    setPause(true);
    mux.lock();
    //1. 先 seek 看看是否成功
    re = demux->seekTo(pos); //seek跳转到关键帧
    if (!re) {
        mux.unlock();
        setPause(false);
        return false;
    }
    //清理缓冲
    //2 清理缓冲队列
    if (adecode)
        adecode->clear();
    if (vdecode)
        vdecode->clear(); //清理缓冲队列，清理ffmpeg的缓冲
    if (audioPlay)
        audioPlay->clear();

    if (!vdecode) {
        mux.unlock();
        setPause(false);
        return re;
    }
    //3. 解码到实际需要显示的帧
    long long seekPts = static_cast<long long int>(pos * demux->totalDuration);
    while (!isExit) {//这里会导致 卡顿
        AVData pkt = demux->read();
        if (pkt.size <= 0)break;
        if (pkt.isAudio) {
            if (pkt.pts < seekPts) {
                pkt.drop();
                continue;
            }
            //写入缓冲队列
            demux->send(pkt);
            continue;
        }

        //解码需要显示的帧之前的数据
        vdecode->sendPacket(pkt);
        pkt.drop();
        AVData data = vdecode->getDecodeFrame();
        if (data.size <= 0) {
            continue;
        }
        if (data.pts >= seekPts) {
            LOGE("seek success！");
            break;
        }
    }
    mux.unlock();
    setPause(false);
    return re;
}

void IPlayer::setPause(bool isP) {
    mux.lock();
    IThread::setPause(isP);
    if (demux)
        demux->setPause(isP);
    if (vdecode)
        vdecode->setPause(isP);
    if (adecode)
        adecode->setPause(isP);
    if (audioPlay)
        audioPlay->setPause(isP);
    if (videoView)
        videoView->setPause(isP);

    mux.unlock();

}

void IPlayer::main() {
    while (!isExit) {
        mux.lock();
        if (!audioPlay || !vdecode || isPause()) {
            mux.unlock();
            sleep();
            continue;
        }
        //同步
        //获取音频的pts 告诉视频
        int apts = audioPlay->pts;
        //XLOGE("apts = %d",apts);
        vdecode->synPts = apts;


        //根据音频来判断是否播放完成
        if (demux->mAudioPacketExist && audioPlay->pts != 0L) {
            mux.unlock();
            if (audioPlay->pts / 1000L >= totalDuration / 1000 && !isPlayComplete) {
                LOGD("播放状态：complete audioPts:%d totalDuration:%ld nextIndex:%d", audioPlay->pts / 1000,
                     totalDuration / 1000, nextIndex);
                isPlayComplete = true;
                playNext();
            } else if (audioPlay->pts / 1000L < totalDuration / 1000) {
                isPlayComplete = false;
            }

            sleep();
            continue;
        }

        //如果没有音频数据，那么根据视频来判断
        if (demux->mAudioPacketExist && vdecode->pts != 0L) {
            mux.unlock();
            if (vdecode->pts / 1000 >= totalDuration / 1000 && !isPlayComplete) {
                LOGD("播放状态：comple videoPts:%d totalDuration:%ld", vdecode->pts / 1000, totalDuration / 1000);
                isPlayComplete = true;
                isExit = true;
                playNext();

            } else if (vdecode->pts / 1000L < totalDuration / 1000) {
                isPlayComplete = false;
            }
        }

        mux.unlock();
        sleep();
    }
}

double IPlayer::playPos() {
    double pos = 0.0;
    mux.lock();

    int total = 0;
    if (demux)
        total = demux->totalDuration / 1000;
    if (total > 0) {
        if (vdecode) {
            if (demux && demux->mAudioPacketExist)
                pos = ((double) (adecode->pts) / (double) (demux->totalDuration) + 0.01) * 100; //加 0.01 是为了更加精准
            else if (demux && demux->mVideoPacketExist && !demux->mAudioPacketExist) {
                pos = ((double) (vdecode->pts) / (double) (demux->totalDuration) + 0.01) * 100;
            }

//            LOGE("播放进度：%f  时长：%d",pos,total);
            if (pos >= 100) {
                mux.unlock();
                return 100;
            }

        }
    }
    mux.unlock();
    return pos;
}

int IPlayer::start() {
    mux.lock();


    if (vdecode)
        vdecode->start();

    if (!demux || !demux->start()) {
        mux.unlock();
        LOGE("demux->Start failed!");
        return false;
    }

    if (adecode)
        adecode->start();

    if (transfer && transfer->REGISTER_AUDIO_TRANSFER_MODEL) {
        transfer->startTransfer();
    }

    if (audioPlay && outPara.format != -1 && outPara.channels != -1 && outPara.sample_rate != -1)
        audioPlay->startPlayer(outPara);
    IThread::start();
    mux.unlock();

    return true;
}

int IPlayer::initMediaCodec(void *javaVM) {
    return AVPlayerBuilder::initMediaCodec(javaVM);

}

uint64_t IPlayer::getTotalDuration() {
    if (demux)
        return demux->totalDuration;
    else
        return 0;
}


/**
 * set多个 URL
 * @param jniEnv
 * @param lists
 */
void IPlayer::setDataSource(JNIEnv *jniEnv, jobject lists) {
    if (mediaLists.size() > 0) {
        while (!mediaLists.empty()) {
            MediaEntity *media = mediaLists.front();
            LOGD("clear path:%s \n", media->path);
            delete[](media->path);
            delete media;
            mediaLists.pop_front();

        }
    }

    nextIndex = 0;
    //获取 ArrayList 对象
    jclass listClass = jniEnv->GetObjectClass(lists);
    //获取 list size 方法
    jmethodID listSize = jniEnv->GetMethodID(listClass, "size", "()I");
    //获取 list get 方法
    jmethodID listGet = jniEnv->GetMethodID(listClass, "get", "(I)Ljava/lang/Object;");
    //执行 size
    jint size = jniEnv->CallIntMethod(lists, listSize);

    for (int i = 0; i < size; ++i) {
        //获取 MediaEntity 实体
        jobject mediaEntity = jniEnv->CallObjectMethod(lists, listGet, i);
        jclass mediaEntityClass = jniEnv->GetObjectClass(mediaEntity);

        //获取路径
        jfieldID path_field_id = jniEnv->GetFieldID(mediaEntityClass, "path", "Ljava/lang/String;");
        jstring path_string = static_cast<jstring>(jniEnv->GetObjectField(mediaEntity, path_field_id));
        const char *media_path = jniEnv->GetStringUTFChars(path_string, JNI_FALSE);
        char *newFilePath = new char[strlen(media_path) + 1];
        sprintf(newFilePath, "%s%c", media_path, 0);

        //获取开始时间
        jfieldID mediaStartDuration = jniEnv->GetFieldID(mediaEntityClass, "startDuration", "J");
        jlong startDuration = reinterpret_cast<jlong>(jniEnv->GetLongField(mediaEntity, mediaStartDuration));

        //获取结束时间
        jfieldID mediaStopDuration = jniEnv->GetFieldID(mediaEntityClass, "stopDuration", "J");
        jlong stopDuration = reinterpret_cast<jlong>(jniEnv->GetLongField(mediaEntity, mediaStopDuration));


        MediaEntity *mediabean = new MediaEntity();
        mediabean->path = newFilePath;
        mediabean->startDuration = startDuration;
        mediabean->stopDuration = stopDuration;
        mediaLists.push_back(mediabean);
        LOGD("path:%s startDuration:%ld  stopDuration:%ld ", newFilePath, startDuration, stopDuration);
        jniEnv->ReleaseStringUTFChars(path_string, media_path);
    }

    //默认设置第一个
    bool isNext = setNextDataSource();
    if (isNext) {
        LOGD("切换成功:%s", AVToolsBuilder::getInstance()->getPlayEngine()->getDataSource());
    }
}

bool IPlayer::setNextDataSource() {
    if (mediaLists.size() > 0) {
        if (nextIndex == mediaLists.size()) {//3
            nextIndex = 0;
            if (!isLoopPlay)return false;
        }
        MediaEntity *mediaEntity = mediaLists.at(nextIndex);
        AVToolsBuilder::getInstance()->getPlayEngine()->setDataSource(mediaEntity->path);
        LOGD("nextIndex %d, set datasource:%s mediaLists.size():%d", nextIndex, mediaEntity->path, mediaLists.size());
        nextIndex++;
        return true;
    }
    return false;
}


void IPlayer::playNext() {
    mux.lock();
    if (setNextDataSource()) {
        AVToolsBuilder::getInstance()->getPlayEngine()->getTransferInstance()->registerModel(false);
        if (vdecode)
            vdecode->close();
        if (adecode)
            adecode->close();
        if (demux)
            demux->close();
        //解封装
        if (!demux || !demux->open(AVToolsBuilder::getInstance()->getPlayEngine()->getDataSource())) {
            LOGE("demux->Open %s failed!", AVToolsBuilder::getInstance()->getPlayEngine()->getDataSource());
        }
        //解码 解码可能不需要，如果是解封之后就是原始数据
        if (!vdecode || !vdecode->open(demux->getVInfo(), false)) {
            LOGE("vdecode->Open %s failed!", AVToolsBuilder::getInstance()->getPlayEngine()->getDataSource());
        }
        if (!adecode || !adecode->open(demux->getAInfo())) {
            LOGE("adecode->Open %s failed!", AVToolsBuilder::getInstance()->getPlayEngine()->getDataSource());
        }

        totalDuration = demux->totalDuration;
    } else {
        LOGD("play not datasource");
    }
    mux.unlock();
}

void IPlayer::setPlayVolume(int v) {
    if (audioPlay)
        audioPlay->setPlayVolume(v);
}

void IPlayer::setPlaySpeed(double d) {
    if (audioPlay)
        audioPlay->setPlaySpeed(d);

}
