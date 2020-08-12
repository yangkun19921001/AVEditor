//
// Created by 阳坤 on 2020-05-23.
//

#include "IPlayer.h"
#include "../builder/AVPlayerBuilder.h"
#include "../../../../../../../../../Android/NDK/android-ndk-r17c/sources/cxx-stl/llvm-libc++/include/cstdint"

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
        //return false;
    }
    if (!adecode || !adecode->open(demux->getAInfo())) {
        LOGE("adecode->Open %s failed!", path);
        //return false;
    }

    //重采样 有可能不需要，解码后或者解封后可能是直接能播放的数据
    //if(outPara.sample_rate <= 0)
    outPara = demux->getAInfo();
    if (!resample || !resample->open(demux->getAInfo(), outPara)) {
        LOGE("resample->Open %s failed!", path);
    }
    mux.unlock();
    return 1;
}

void IPlayer::close() {
    mux.lock();
    //2 先关闭主体线程，再清理观察者
    //同步线程
    IThread::stop();
    //解封装
    if (demux)
        demux->stop();
    //解码
    if (vdecode)
        vdecode->stop();
    if (adecode)
        adecode->stop();
    if (audioPlay)
        audioPlay->stop();

    //2 清理缓冲队列
    if (vdecode)
        vdecode->clear();
    if (adecode)
        adecode->clear();
    if (audioPlay)
        audioPlay->clear();

    //3 清理资源
    if (audioPlay)
        audioPlay->close();
    if (videoView)
        videoView->close();
    if (vdecode)
        vdecode->close();
    if (adecode)
        adecode->close();
    if (demux)
        demux->close();
    mux.unlock();


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
        if (!audioPlay || !vdecode) {
            mux.unlock();
            sleep();
            continue;
        }
        //同步
        //获取音频的pts 告诉视频
        int apts = audioPlay->pts;
        //XLOGE("apts = %d",apts);
        vdecode->synPts = apts;

        mux.unlock();
        sleep();
    }
}

double IPlayer::playPos() {
    double pos = 0.0;
    mux.lock();

    int total = 0;
    if (demux)
        total = demux->totalDuration;
    if (total > 0) {
        if (vdecode) {
            pos = (double) vdecode->pts / (double) total + 0.01;
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
    if (audioPlay)
        audioPlay->startPlayer(outPara);
    IThread::start();
    mux.unlock();

    return true;
}

void IPlayer::initMediaCodec(void *javaVM) {
    AVPlayerBuilder::initMediaCodec(javaVM);

}

uint64_t IPlayer::getTotalDuration() {
    if (demux)
        return demux->totalDuration;
    else
        return 0;
}
