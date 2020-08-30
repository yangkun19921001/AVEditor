//
// Created by 阳坤 on 2020-05-21.
//

#include <muxer/AVMuxer.h>
#include "AVDemux.h"


AVDemux::AVDemux() {
    static int isFirst = true;
    if (isFirst) {
        isFirst = false;
        //注册所有解封装器,已经过时了
//        av_register_all();
        //注册所有解码器,已经过时了
//        avcodec_register_all();
        //网络环境初始化
        avformat_network_init();
    }
//    this->TAG = "AVDemux %s";
}

static void ffmpeg_log_callback(void *ptr, int level, const char *fmt, va_list vl) {
    va_list vl2;
    char line[1024];
    static int print_prefix = 1;
    va_copy(vl2, vl);
    av_log_format_line(ptr, level, fmt, vl2, line, sizeof(line), &print_prefix);
    va_end(vl2);
    LOGE("%s \n", line);
}

/**
 * 打开资源
 * @param source  source 保证是可靠的
 * @return 1 is success
 */
int AVDemux::open(const char *source) {
    //打开之前，先关闭之前未关闭的资源
    close();
    mux.lock();
    //1. 打开资源输入流 0 is success
    int ret = avformat_open_input(&pFormatCtx, source, 0, 0);
    if (ret != 0) {
        mux.unlock();
        close();
        char error_meg[1024] = {0};
        av_strerror(ret, error_meg, sizeof(error_meg));
        LOGE("open source failed :%s", source);
        return false;
    }
    LOGI("open source success :%s", source);

    //2. 读取 source 信息 >=0 success
    ret = avformat_find_stream_info(pFormatCtx, 0);
    if (ret < 0) {
        mux.unlock();
        close();
        char error_meg[1024] = {0};
        av_strerror(ret, error_meg, sizeof(error_meg));
        LOGE("find stream info failed :%s error:%s \n", source,error_meg);

        return false;
    }
//    av_log_set_callback(ffmpeg_log_callback);

    //打印 AV meta info
    av_dump_format(pFormatCtx, 0, source, 0);

    //先解锁，不然会造成死锁
    mux.unlock();

    //3. 读取音视频流信息
    AVParameter videoPar = getVInfo();
    if (!videoPar.para) {
        LOGE("find video info failed :%s", source);
        mVideoPacketExist = false;
    }
    AVParameter audioPar = getAInfo();
    if (!audioPar.para) {
        LOGE("find audio info failed :%s", source);
        mAudioPacketExist = false;
    }
    //读取媒体文件总长度 ms
    this->totalDuration = pFormatCtx->duration / 1000;

    LOGE("open source success :%s", source);
    LOGE("source totalDuration :%lld  pFormatCtx->duration:%lld", totalDuration, pFormatCtx->duration);
    return true;
}

AVParameter AVDemux::getVInfo() {
    mux.lock();
    if (!pFormatCtx) {
        mux.unlock();
        LOGE("GetVPara failed! ic is NULL！");
        return AVParameter();
    }
    //获取了视频流索引
    int re = av_find_best_stream(pFormatCtx, AVMEDIA_TYPE_VIDEO, -1, -1, 0, 0);
    if (re < 0) {
        mux.unlock();
        LOGE("av_find_best_stream failed!");
        return AVParameter();
    }
    video_stream_index = re;
    AVParameter para;
    para.para = pFormatCtx->streams[re]->codecpar;

    switch (pFormatCtx->streams[re]->codecpar->format) {
        case AV_PIX_FMT_YUV420P:
            LOGE("Video Info  格式:%s 码率:%d 宽:%d  高:%d", "AV_PIX_FMT_YUV420P",
                 pFormatCtx->streams[re]->codecpar->bit_rate, pFormatCtx->streams[re]->codecpar->width,
                 pFormatCtx->streams[re]->codecpar->height);
            break;
        case AV_PIX_FMT_NV21:
            LOGE("Video Info  格式:%s 码率:%d 宽:%d  高:%d", "AV_PIX_FMT_NV21",
                 pFormatCtx->streams[re]->codecpar->bit_rate, pFormatCtx->streams[re]->codecpar->width,
                 pFormatCtx->streams[re]->codecpar->height);
            break;
        case AV_PIX_FMT_NV12:
            LOGE("Video Info  格式:%s 码率:%d  宽:%d  高:%d", "AV_PIX_FMT_NV12",
                 pFormatCtx->streams[re]->codecpar->bit_rate, pFormatCtx->streams[re]->codecpar->width,
                 pFormatCtx->streams[re]->codecpar->height);
            break;
    }

    para.format = pFormatCtx->streams[re]->codecpar->format;
    para.timebase = pFormatCtx->streams[re]->time_base;
    mux.unlock();
    return para;
}

AVParameter AVDemux::getAInfo() {
    mux.lock();
    if (!pFormatCtx) {
        mux.unlock();
        LOGE("GetVPara failed! ic is NULL！");
        return AVParameter();
    }
    //获取了音频流索引
    int re = av_find_best_stream(pFormatCtx, AVMEDIA_TYPE_AUDIO, -1, -1, 0, 0);
    if (re < 0) {
        mux.unlock();
        LOGE("av_find_best_stream failed!");
        return AVParameter();
    }
    audio_stream_index = re;
    AVParameter para;
    //拿到解码参数
    para.para = pFormatCtx->streams[re]->codecpar;
    //音频声音通道
    para.channels = pFormatCtx->streams[re]->codecpar->channels;
    para.duration = pFormatCtx->streams[re]->duration;
    //采样率
    para.sample_rate = pFormatCtx->streams[re]->codecpar->sample_rate;
    LOGE("Audio Info 音频通道 %d 采样率：%lld 格式:%d", para.channels, para.sample_rate,
         pFormatCtx->streams[re]->codecpar->format);
    para.format = pFormatCtx->streams[re]->codecpar->format;
    para.timebase = pFormatCtx->streams[re]->time_base;
    mux.unlock();
    return para;
}


AVData AVDemux::read() {
    mux.lock();
    if (!pFormatCtx) {
        mux.unlock();
        return AVData();
    }
    //初始化 AVPacket
    AVPacket *packet = av_packet_alloc();
    //读取一帧音视频流 0 is success!
    int ret = av_read_frame(pFormatCtx, packet);

    if (ret == AVERROR_EOF) {//代表读取完成了
        mux.unlock();
        av_packet_free(&packet);
        packet = 0;
        return AVData();
    }

    if (ret != 0) {
        mux.unlock();
        av_packet_free(&packet);
        packet = 0;
        return AVData();
    }
    //这里说明 读取成功了
    AVData avData;

    avData.size = packet->size;

    if (packet->stream_index == audio_stream_index) {
        avData.isAudio = true;
        avData.data = (unsigned char *) (packet);
//        LOGE("读取到音频数据");
    } else if (packet->stream_index == video_stream_index) {
        avData.isAudio = false;
        avData.data = (unsigned char *) (packet);
//        LOGE("读取到视频数据");
    } else {//先暂时不用管其它的
        LOGE("读取到其它数据流 -- %d ", packet->stream_index);
        mux.unlock();
        av_packet_free(&packet);
        packet = 0;
        return AVData();
    }
    //计算 pts 值
    //转换pts -> 毫秒
    packet->pts = packet->pts * (1000 * r2d(pFormatCtx->streams[packet->stream_index]->time_base));
    packet->dts = packet->dts * (1000 * r2d(pFormatCtx->streams[packet->stream_index]->time_base));
    avData.pts = packet->pts;
    mux.unlock();

//    if (!muxer){
//        muxer = new AVMuxerEngine();
//        muxer->initMuxer("sdcard/aveditor/ffmpeg_muxer.mp4","mp4");
//        muxer->start();
//    }
//    avData.data = reinterpret_cast<unsigned char *>(av_packet_clone(packet));
//    muxer->enqueue(avData);
    return avData;
}

int AVDemux::seekTo(double pos) {
    if (pos < 0 || pos > 1 || this->totalDuration <= 0) {
        LOGE("Seek value must 0.0~1.0");
        return false;
    }
    int re = false;
    mux.lock();
    if (!pFormatCtx) {
        mux.unlock();
        return false;
    }
    //清理读取的缓冲
    avformat_flush(pFormatCtx);
    long long seekPts = 0;
    seekPts = pFormatCtx->streams[video_stream_index]->duration * pos;

//    //往后跳转到关键帧
    re = av_seek_frame(pFormatCtx, video_stream_index, seekPts, AVSEEK_FLAG_FRAME/*关键帧*/ |
                                                                AVSEEK_FLAG_BACKWARD/*往视频后面找关键帧*/);//音频不存在 B 帧的概念移动到哪里就播放到哪里。如果视频移动播放的时候没有关键帧那么就会导致解码失败，必须移动到关键帧处


//    seekPts = pFormatCtx->duration * pos;
    //往后跳转到关键帧
//    re = av_seek_frame(pFormatCtx, -1, seekPts,
//                                                                AVSEEK_FLAG_BACKWARD/*往视频后面找关键帧*/);//音频不存在 B 帧的概念移动到哪里就播放到哪里。如果视频移动播放的时候没有关键帧那么就会导致解码失败，必须移动到关键帧处


    mux.unlock();
    return re >= 0;
}


int AVDemux::close() {
    mux.lock();
    if (pFormatCtx)
        avformat_close_input(&pFormatCtx);//关闭
    mux.unlock();

    return false;
}