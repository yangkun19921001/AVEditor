//
// Created by 阳坤 on 2020-05-22.
//

#include "AVDecode.h"


FILE *file = 0;

int AVDecode::initMediaCodec(void *vm) {
    return av_jni_set_java_vm(vm, 0);
}


AVCodec *getCodec(AVCodecParameters *parameters) {
    AVCodec *codec;
    switch (parameters->codec_id) {
        case AV_CODEC_ID_H264:
            codec = avcodec_find_decoder_by_name(H264_MEDIACODEC);//硬解码264
            if (codec == NULL) {
                LOGE("Couldn't find Codec. H264_MEDIACODEC\n");
                return NULL;
            }
            LOGD("avcodec_find_decoder_by_name(H264_MEDIACODEC) success!");
            break;
        case AV_CODEC_ID_MPEG4:
            codec = avcodec_find_decoder_by_name(MPEG4_MEDIACODEC);//硬解码mpeg4
            if (codec == NULL) {
                LOGE("Couldn't find Codec. MPEG4_MEDIACODEC\n");
                return NULL;
            }
            LOGD("avcodec_find_decoder_by_name(MPEG4_MEDIACODEC) success!");
            break;
        case AV_CODEC_ID_HEVC:
            codec = avcodec_find_decoder_by_name(HEVC_MEDIACODEC);//硬解码265
            if (codec == NULL) {
                LOGE("Couldn't find Codec.HEVC_MEDIACODEC \n");
                return NULL;
            }
            LOGD("avcodec_find_decoder_by_name(HEVC_MEDIACODEC); success!");
            break;
        default:
            codec = avcodec_find_decoder(parameters->codec_id);//软解
            if (codec == NULL) {
                LOGE("Couldn't find Codec.\n");
                return NULL;
            }
            break;
    }
    return codec;
}

int AVDecode::open(AVParameter par, int isMediaCodec) {
    int ret = 0;
    //打开之前先清理之前预留的资源
    close();
    //保证解码参数不为空
    if (!par.para)return ret;
    mux.lock();
    //拿到解码参数
    AVCodecParameters *parameters = par.para;
    //先根据解码参数的 id 找到解码器
    AVCodec *codec = avcodec_find_decoder(parameters->codec_id);
    //支持硬件解码
    if (isMediaCodec) {
        codec = getCodec(parameters);
    }

    //如果没有找到硬件解码，将使用默认解码器
    if (!codec) {
        codec = avcodec_find_decoder(parameters->codec_id);//软解
        if (codec == NULL) {
            LOGE("Couldn't find Codec.\n");
            return NULL;
        }
    }

    LOGI("avcodec_find_decoder  success !  isMediacodec:%d", isMediaCodec);
    //创建解码器上下文
    this->pCodec = avcodec_alloc_context3(codec);
    //将解码参数赋值到解码上下文中
    ret = avcodec_parameters_to_context(this->pCodec, parameters);
    if (ret < 0) {
        mux.unlock();
        close();
        LOGE("avcodec_parameters_to_context error! %d", ret);
        return false;
    }
    //打印解码信息
    LOGE("-------------------解码信息-------------------\n");
    LOGE("width:%d \n", pCodec->width);
    LOGE("height:%d \n", pCodec->height);
    LOGE("rate:%d \n", pCodec->bit_rate);
    LOGE("pix_fmt:%d \n", pCodec->pix_fmt);
    LOGE("channels:%d \n", pCodec->channels);
    LOGE("sample_rate:%d \n", pCodec->sample_rate);
    LOGE("codec_id:%d \n", pCodec->codec_id);
    LOGE("-------------------解码信息-------------------\n");


    //指定多线程解码数量
    pCodec->thread_count = 4;
    //打开解码器
    ret = avcodec_open2(pCodec, codec, 0);
    if (ret != 0) {
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf) - 1);
        LOGE("%s", buf);
        mux.unlock();
        return false;
    }
    if (pCodec->codec_type == AVMEDIA_TYPE_VIDEO) {
        this->isAudio = false;
    } else {
        this->isAudio = true;
    }

    //时间基
    timebase = par.timebase;
    LOGI("avcodec_open2 open success! 是否是音频解码器: %d", this->isAudio);
//    file = fopen("sdcard/test_yuv_11_decode.yuv", "wb");
    mux.unlock();
    return true;
}

int AVDecode::close() {
    IDecode::clear();
    mux.lock();
    pts = 0;
//    if (pFrame)
//        av_frame_free(&pFrame);
    if (pCodec) {
        avcodec_close(pCodec);
        avcodec_free_context(&pCodec);
        pCodec = 0;
    }
    mux.unlock();
    return 1;
}

int AVDecode::clear() {
    IDecode::clear();
    mux.lock();
    if (pCodec)
        avcodec_flush_buffers(pCodec);
    mux.unlock();
    return true;
}

int AVDecode::sendPacket(AVData data) {
    //判断是否是空数据。
    if (!data.data || data.size <= 0)return 0;
    mux.lock();
    if (!pCodec) {
        mux.unlock();
        return false;
    }


    int ret = avcodec_send_packet(pCodec, reinterpret_cast<const AVPacket *>(data.data));
    mux.unlock();
    return ret == 0 ? 1 : 0;
}

AVData AVDecode::getDecodeFrame() {
    mux.lock();
    if (!pCodec) {
        mux.unlock();
        return AVData();
    }
//    if (!pFrame)
    AVFrame *pFrame = av_frame_alloc();

    //拿到解码之后的数据 PCM/H264 0 is ok.
    int ret = avcodec_receive_frame(pCodec, pFrame);

    if (ret != 0) {
        mux.unlock();
        char buf[1024] = {0};
        av_strerror(ret, buf, sizeof(buf) - 1);
//        LOGE("avcodec_receive_frame error %s %d isAudio:%d", buf, ret, isAudio);
        return AVData();
    }

    AVData deData;
    AVFrame *newFrame = av_frame_clone(pFrame);
    deData.data = (unsigned char *) newFrame;
    if (pCodec->codec_type == AVMEDIA_TYPE_VIDEO) {
        deData.size = (pFrame->linesize[0] + pFrame->linesize[1] + pFrame->linesize[2]) * pFrame->height;
        deData.width = pFrame->width;
        deData.height = pFrame->height;
        deData.pts = pFrame->pts;
        pts = deData.pts;
        deData.isAudio = 0;

//        LOGE("AVFrame width=%d height=%d \n",pFrame->width,pFrame->height);
//        for (int i = 0; i < AV_NUM_DATA_POINTERS; ++i) {
//            LOGE("AVFrame data linesize[%d]=%d \n",i,pFrame->linesize[i]);
//        }

        deData.datas[0] = static_cast<unsigned char *>(malloc(deData.width * deData.height));
        deData.datas[1] = static_cast<unsigned char *>(malloc(deData.width / 2 * deData.height / 2));
        deData.datas[2] = static_cast<unsigned char *>(malloc(deData.width / 2 * deData.height / 2));

        unsigned char *y = deData.datas[0];
        unsigned char *u = deData.datas[1];
        unsigned char *v = deData.datas[2];

        for (int i = 0; i < deData.height; i++) {
            memcpy(y + deData.width * i,
                   pFrame->data[0] + pFrame->linesize[0] * i,
                   deData.width);
        }
        for (int j = 0; j < deData.height / 2; j++) {
            memcpy(u + deData.width / 2 * j,
                   pFrame->data[1] + pFrame->linesize[1] * j,
                   deData.width / 2);
        }
        for (int k = 0; k < deData.height / 2; k++) {
            memcpy(v + deData.width / 2 * k,
                   pFrame->data[2] + pFrame->linesize[2] * k,
                   deData.width / 2);
        }


//        memcpy(deData.datas, pFrame->data, sizeof(deData.datas));

        /*        unsigned char * y = static_cast<unsigned char *>(malloc(deData.width * deData.height));
        unsigned char * u = static_cast<unsigned char *>(malloc(deData.width/2 * deData.height/2));
        unsigned char * v = static_cast<unsigned char *>(malloc(deData.width/2 * deData.height/2));



        for (int i = 0; i < deData.height; i++) {
            memcpy(y + deData.width * i,
                   pFrame->data[0] + pFrame->linesize[0] * i,
                   deData.width);
        }
        for (int j = 0; j < deData.height / 2; j++) {
            memcpy(u + deData.width / 2 * j,
                   pFrame->data[1] + pFrame->linesize[1] * j,
                   deData.width / 2);
        }
        for (int k = 0; k < deData.height / 2; k++) {
            memcpy(v + deData.width / 2 * k,
                   pFrame->data[2] + pFrame->linesize[2] * k,
                   deData.width / 2);
        }
*/
//        fwrite(deData.datas[0], 1, deData.width * deData.height, file);    //Y
//        fwrite(deData.datas[1], 1, deData.width * deData.height / 4, file);  //U
//        fwrite(deData.datas[2], 1, deData.width * deData.height / 4, file);  //V

//        free(y);
//        free(u);
//        free(v);

//
//        LOGE("解码成功! AVMEDIA_TYPE_VIDEO");
    } else if (pCodec->codec_type == AVMEDIA_TYPE_AUDIO) {
        deData.pts = pFrame->pts;
        pts = deData.pts;
        //样本字节数 * 单通道样本数 * 通道数
        deData.size = av_get_bytes_per_sample((AVSampleFormat) pFrame->format) * pFrame->nb_samples * 2;
        deData.isAudio = 1;
//        LOGE("解码成功! AVMEDIA_TYPE_AUDIO");
        memcpy(deData.datas, pFrame->data, sizeof(deData.datas));
    } else {
        mux.unlock();
//        LOGE("解码成功! UNKOWN");
        return AVData();
    }
    deData.format = pFrame->format;
//    memcpy(deData.datas, pFrame->data, sizeof(deData.datas));
    av_frame_free(&pFrame);
    pFrame = 0;
    mux.unlock();
    return deData;
}

/**
 * 是否打开硬件解码器
 * @param isOpenMediaCodec
 */
void AVDecode::setMediaCodec(bool isMediaCodec) {
    this->isMediaCodec_ = isMediaCodec;
}

/**
 * 是否要硬件解码
 * @return
 */
bool AVDecode::isMediaCodec() {
    return this->isMediaCodec_;
}

