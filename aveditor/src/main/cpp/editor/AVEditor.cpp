//
// Created by 阳坤 on 2020-08-31.
//

#include <iostream>
#include <muxer/AVPacketPool.h>
#include <jni.h>
#include "AVEditor.h"



int AVEditor::open(const char *url, deque<MediaEntity *> mediaLists) {
//    close();
    int ret = IEditor::open(url, mediaLists);
    this->mediaLists = mediaLists;
    mux.lock();
    demux = new AVDemux();
    mp4Muxer = new Mp4Muxer();
    //订阅复用器的数据
    demux->registers(this);
    //恢复默认第一个位置
    nextIndex = 0;
    MediaEntity *mediaEntity = getNextDataSource();
    //解封装
    if (!mediaEntity->path || !demux || !demux->open(mediaEntity->path)) {
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


    ret = initAudioFilter("aac_adtstoasc");
    if (ret == 0) {
        // 创建失败
        LOGE("Init aac filter fail.");
        return false;
    }

    ret = initVideoFilter("h264_mp4toannexb");
    if (ret == 0) {
        LOGE("Init h264 filter fail.");
        return false;
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
    LOGI("AVEDitor Final is onComplete");
    int ret = playNext();
    isAFirst = false;
    isVFirst = false;
    if (ret == 1) {
        //现在开启合并
        ret = IEditor::start();
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
//        LOGE("收到数据  before：isAudio:%d size:%d endPacket:%d pts:%d  模拟 PTS:%d", data.isAudio, data.size, data.endPacket,
//             pck->pts, curAPts);
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
            mp4Muxer->enqueue(pck->data, data.isAudio,
                              pck->size, pck->pts);
            LOGE("当前 PTS Audio :%d totalDur:%d size:%d", pck->pts / 1000000, totalDuration, pck->size);

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
            if (nalu_type == H264_NALU_TYPE_SEQUENCE_PARAMETER_SET) {
                LOGI("AVEDitor Final is in pck->pts  :%d totalDur:%d nalu_type:%d size:%d", pck->pts / 1000,
                     totalDuration,
                     nalu_type, pck->size);
                mp4Muxer->enqueue(vParameter.codec->extradata, data.isAudio, vParameter.codec->extradata_size,
                                  pck->pts);
                cursor = vParameter.codec->extradata_size;
            }
//            LOGE("收到数据  before：isAudio:%d size:%d endPacket:%d 模拟 PTS %d", data.isAudio, data.size, data.endPacket,
//                 i += 40);

            nalu_type = ((pck->data + cursor)[4] & 0x1F);
            if (nalu_type == H264_NALU_TYPE_IDR_PICTURE ||
                nalu_type == H264_NALU_TYPE_SEQUENCE_PARAMETER_SET ||
                nalu_type == H264_NALU_TYPE_PICTURE_PARAMETER_SET ||
                nalu_type == H264_NALU_TYPE_NON_IDR_PICTURE) {
                LOGI("AVEDitor Final is in pck->pts  :%d totalDur:%d nalu_type:%d", pck->pts / 1000, totalDuration,
                     nalu_type);
                mp4Muxer->enqueue(pck->data + cursor, data.isAudio,
                                  pck->size - cursor, pck->pts);

                curVPts = pck->pts;
            }
        }
    }
}

int AVEditor::start() {
    mux.lock();
    int ret = 0;
//    ret = IEditor::start();
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
    if (mediaLists.size() > 0) {
        while (!mediaLists.empty()) {
            MediaEntity *media = mediaLists.front();
            LOGD("clear path:%s \n", media->path);
            delete[] media->path;
            delete media;
            mediaLists.pop_front();
            media->path = NULL;
        }
    }
    delete[](outPath);
    //解封装
    if (demux) {
        demux->stop();
    }
    if (mp4Muxer) {
        mp4Muxer->Stop();
        delete mp4Muxer;
        mp4Muxer = NULL;
    }


    closeAudioFilter();
    closeVideoFilter();

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
            if (ret < 0) {
                LOGE("收到数据: mp4Muxer->Encode() exit");
                break;
            }
        }
    }
    close();
    LOGE("收到数据: 合并封装器退出成功");

}


MediaEntity *AVEditor::getNextDataSource() {
    if (mediaLists.size() > 0) {
        if (nextIndex == mediaLists.size()) {//判断超出的界限
            nextIndex = 0;
            return NULL;
        }
        MediaEntity *mediaEntity = mediaLists.at(nextIndex);
        LOGD("nextIndex %d, set datasource:%s mediaLists.size():%d", nextIndex, mediaEntity->path, mediaLists.size());
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

        closeAudioFilter();
        closeVideoFilter();

        ret = initAudioFilter("aac_adtstoasc");
        if (ret == 0)
            return 0;
        ret = initVideoFilter("h264_mp4toannexb");
        if (ret == 0)
            return 0;

        //重新订阅
        demux->registers(this);
        if (!demux || !demux->start()) {
            mux.unlock();
            LOGE("demux->Start failed!");
            return 0;
        }
        totalDuration += demux->totalDuration;
        ret = 0;
    } else {
        LOGD("play not datasource");
        ret = 1;
    }

    mux.unlock();
    return ret;
}

int AVEditor::initAudioFilter(char *filterName) {
    if (!mAudioFilter) {
        // 创建AAC滤波器
        mAudioFilter = av_bitstream_filter_init(filterName);
        if (!mAudioFilter) {
            // 创建失败
            LOGE("Init aac filter fail.");
            return 0;
        }
    }
    return 1;
}

int AVEditor::initVideoFilter(char *filterName) {
    if (!mVideoFilter) {
        // 创建H264滤波器
        mVideoFilter = av_bitstream_filter_init(filterName);
        if (!mVideoFilter) {
            // 创建失败
            LOGE("Init h264 filter fail.");
            return 0;
        }
    }
    return 1;
}

int AVEditor::closeAudioFilter() {
    if (mAudioFilter) {
        av_bitstream_filter_close(mAudioFilter);
        mAudioFilter = 0;
    }
    return 1;
}

int AVEditor::closeVideoFilter() {
    if (mVideoFilter) {
        av_bitstream_filter_close(mVideoFilter);
        mVideoFilter = 0;
    }
    return 1;
}


/**
 * set多个 URL
 * @param jniEnv
 * @param lists
 */
void AVEditor::setMergeSource(JNIEnv *jniEnv, jobject lists) {
    if (mediaLists.size() > 0) {
        while (!mediaLists.empty()) {
            MediaEntity *media = mediaLists.front();
            if (media->path) {
                LOGD("clear path:%s \n", media->path);
                delete[](media->path);
                media->path = 0;
            }
            delete media;
            mediaLists.pop_front();

        }
    }

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
//        sprintf(newFilePath, "%s%c", media_path, 0);
        strcpy(newFilePath, media_path);

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

}

deque<MediaEntity *> AVEditor::getMergeSource() {
    return mediaLists;
}




