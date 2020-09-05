//
// Created by 阳坤 on 2020-08-31.
//

#include <iostream>
#include "AVEditor.h"

int AVEditor::open(const char *url, deque<MediaEntity *> medialists) {
    close();
    int ret = IEditor::open(url, medialists);

    mux.lock();
    demux = new AVDemux();
    mp4Muxer = new Mp4Muxer();
    //订阅复用器的数据
    demux->registers(this);
    //恢复默认第一个位置
    nextIndex = 0;
    MediaEntity *mediaEntity = getNextDataSource();
    //解封装
    if (!demux || !demux->open(mediaEntity->path)) {
        mux.unlock();
        LOGE("demux->Open %s failed!", mediaEntity->path);
        return 0;
    }
    totalDuration = demux->totalDuration;
    vParameter = demux->getVInfo();
    aParameter = demux->getAInfo();


    if (mp4Muxer->Init(url, vParameter.para->width, vParameter.para->height, 25, vParameter.para->bit_rate,
                       aParameter.para->sample_rate, aParameter.para->channels, aParameter.para->bit_rate,
                       "DevYK->AVTools") < 0) {
        mp4Muxer->Stop();
        ret = 0;
    }

    if (!mAudioFilter) {
        // 创建AAC滤波器
        mAudioFilter = av_bitstream_filter_init("aac_adtstoasc");
        if (!mAudioFilter) {
            // 创建失败
            LOGE("Init aac filter fail.");
            return false;
        }
    }

    if (!mVideoFilter) {
        // 创建H264滤波器
        mVideoFilter = av_bitstream_filter_init("h264_mp4toannexb");
        if (!mVideoFilter) {
            // 创建失败
            LOGE("Init h264 filter fail.");
            return false;
        }
    }


    curVPts = 0;
    curVPts = 0;
    preAPts = 0;
    preVPts = 0;
    isAFirst = true;
    isVFirst = true;
    mux.unlock();
    return ret;
}


void AVEditor::onComplete() {
    LOGE("收到数据: 读取到了结束包...");
    int ret = playNext();
    isAFirst = false;
    isVFirst = false;
    if (ret == 1) {
//        close();
        LOGE("收到数据: 读取到了结束包 下一个片段也解封装完了...");
    }
    return;
}


int64_t getTimeStamp() {
    struct timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    return now.tv_sec * 1000000000LL + now.tv_nsec;
}


int64_t AVEditor::getAPTSUs() {
    if (curAPts == 0)
        curAPts = getTimeStamp() / 1000;
    return getTimeStamp() / 1000 - curAPts;
}

int pts = 0;

int64_t AVEditor::getVPTSUs() {
    if (pts == 0)
        pts = getTimeStamp() / 1000;
    return getTimeStamp() / 1000 - pts;
}

int i = 0;

void AVEditor::update(AVData data) {


    if (mp4Muxer && (AVPacket *) data.data) {
        AVPacket *pck = (AVPacket *) data.data;
        pck->pts = pck->pts * 1000;

        LOGE("收到数据  before：isAudio:%d size:%d endPacket:%d pts:%d  模拟 PTS:%d", data.isAudio, data.size, data.endPacket,
             pck->pts, curAPts);
        int cursor = 0;
        if (data.isAudio) {
            if (!isAFirst) {
                preAPts += curAPts;
                isAFirst = true;
//                return;
            }
            pck->pts += preAPts;
            av_bitstream_filter_filter(mAudioFilter, aParameter.codec, NULL, &pck->data, &pck->size, pck->data,
                                       pck->size, 0);
            LOGE("当前 PTS Audio :%d totalDur:%d", pck->pts / 1000, totalDuration);
            mp4Muxer->enqueue(pck->data + cursor, data.isAudio,
                              pck->size - cursor, pck->pts);

            curAPts = pck->pts;
        } else {
            if (!isVFirst) {
                preVPts += curVPts;
                isVFirst = true;
//                return;
            }
            pck->pts += preVPts;
            av_bitstream_filter_filter(mVideoFilter, vParameter.codec, NULL, &pck->data, &pck->size, pck->data,
                                       pck->size, 0);
            int nalu_type = (pck->data[4] & 0x1F);
            cursor = 0;
            LOGE("当前 PTS Video :%d totalDur:%d", pck->pts / 1000, totalDuration);
//            curVPts = preVPts + pck->pts;
            if (nalu_type == H264_NALU_TYPE_SEQUENCE_PARAMETER_SET) {
                mp4Muxer->enqueue(vParameter.codec->extradata, data.isAudio, vParameter.codec->extradata_size,
                                  pck->pts);
                cursor = vParameter.codec->extradata_size;
            }
            LOGE("收到数据  before：isAudio:%d size:%d endPacket:%d 模拟 PTS %d", data.isAudio, data.size, data.endPacket,
                 i += 40);
            if (nalu_type == H264_NALU_TYPE_IDR_PICTURE) {
                mp4Muxer->enqueue(vParameter.codec->extradata, data.isAudio, vParameter.codec->extradata_size,
                                  pck->pts);
                cursor = 0;
            }
            mp4Muxer->enqueue(pck->data + cursor, data.isAudio,
                              pck->size - cursor, pck->pts);

            curVPts = pck->pts;

        }
    }
}

int AVEditor::start() {
    mux.lock();
    int ret = 0;
    ret = IEditor::start();
    if (!demux || !demux->start()) {
        mux.unlock();
        LOGE("demux->Start failed!");
        return ret;
    }
    mux.unlock();
    return 1;
}

int AVEditor::close() {
    int ret = 0;
    mux.lock();
    ret = IEditor::close();
    //解封装
    if (demux) {
        demux->stop();
    }
    if (mp4Muxer) {
        mp4Muxer->Stop();
        delete mp4Muxer;
        mp4Muxer = NULL;
    }

    if (mAudioFilter) {
        av_bitstream_filter_close(mAudioFilter);
        mAudioFilter = 0;
    }


    if (mVideoFilter) {
        av_bitstream_filter_close(mVideoFilter);
        mVideoFilter = 0;
    }

    mux.unlock();
    return ret;
}


/**
 * 该模块子线程入口
 */
void AVEditor::main() {
    int pts = 0;
    int pts_ = 0;
    while (!isExit) {
        if (isPause()) {
            sleep(10);
            continue;
        }
        int64_t basePts = getTimeStamp();

//        if (pts == 0)
//            pts = basePts / 1000;
//        pts_ = getTimeStamp() / 1000 - pts;
//        LOGE("模拟 PTS:%lld", pts_ / 1000);
        if (mp4Muxer) {
            int ret = mp4Muxer->Encode();
            if (ret < 0)
                break;
        }
    }
    close();
    LOGE("收到数据: 合并封装器退出成功");

}


MediaEntity *AVEditor::getNextDataSource() {
    if (medialists.size() > 0) {
        if (nextIndex == medialists.size()) {//判断超出的界限
            nextIndex = 0;
            return NULL;
        }
        MediaEntity *mediaEntity = medialists.at(nextIndex);
        LOGD("nextIndex %d, set datasource:%s mediaLists.size():%d", nextIndex, mediaEntity->path, medialists.size());
        nextIndex++;
        return mediaEntity;
    }
    return NULL;
}


int AVEditor::playNext() {
    int ret = 0;
    mux.lock();
    MediaEntity *data = getNextDataSource();
    if (data) {
        if (demux) {
            demux->notifyAll();
            demux->close();
        }
        //销毁订阅
        demux->unRegisters(this);

        //解封装
        if (!demux || !demux->open(data->path)) {
            LOGE("demux->Open %s failed!", data->path);
            mux.unlock();
            return 0;
        }
        vParameter = demux->getVInfo();
        aParameter = demux->getAInfo();
        //重新订阅
        demux->registers(this);
        if (!demux || !demux->start()) {
            mux.unlock();
            LOGE("demux->Start failed!");
            return 0;
        }
        totalDuration += demux->totalDuration;
    } else {
        LOGD("play not datasource");
        ret = 1;
    }

    mux.unlock();
    return ret;
}




