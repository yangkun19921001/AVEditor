//
// Created by 阳坤 on 2020-05-22.
//

#include "KAVResample.h"

bool KAVResample::open(AVParameter in, AVParameter out) {
    close();
    mux.lock();
    //音频重采样上下文初始化
    actx = swr_alloc();
    actx = swr_alloc_set_opts(actx,
                              av_get_default_channel_layout(out.channels),
                              AV_SAMPLE_FMT_S16, out.sample_rate,
                              av_get_default_channel_layout(in.para->channels),
                              (AVSampleFormat) in.para->format, in.para->sample_rate,
                              0, 0);

    int re = swr_init(actx);
    if (re != 0) {
        mux.unlock();
        LOGE("swr_init failed!");
        return false;
    } else {
        LOGI("swr_init success!");
    }
    outChannels = in.para->channels;
    outFormat = AV_SAMPLE_FMT_S16;
    mux.unlock();
    return true;
}

void KAVResample::close() {
    mux.lock();
    if (actx) {
        swr_free(&actx);
    }
    mux.unlock();
}

AVData KAVResample::resample(AVData indata) {
    if (indata.size <= 0 || !indata.data) return AVData();
    mux.lock();
    if (!actx) {
        mux.unlock();
        return AVData();
    }

    //XLOGE("indata pts is %d",indata.pts);
    AVFrame *frame = (AVFrame *) indata.data;

    //输出空间的分配
    AVData out;
    int outsize = outChannels * frame->nb_samples * av_get_bytes_per_sample((AVSampleFormat) outFormat);
    if (outsize <= 0)return AVData();
    out.alloc(outsize);
    uint8_t *outArr[2] = {0};
    outArr[0] = out.data;
    int len = swr_convert(actx, outArr, frame->nb_samples, (const uint8_t **) frame->data, frame->nb_samples);
    if (len <= 0) {
        mux.unlock();
        out.drop();
        return AVData();
    }
    out.pts = indata.pts;
    mux.unlock();
    return out;
}
