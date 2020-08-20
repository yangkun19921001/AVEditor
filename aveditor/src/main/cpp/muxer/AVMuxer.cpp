//
// Created by 阳坤 on 2020-08-20.
//

#include "AVMuxer.h"


/**
 * 初始化复用器
 * @param outPath
 * @param ourFormat
 * @return
 */
int AVMuxer::initMuxer(const char *outPath, const char *ourFormat) {
    int ret = 0;
    //声明一个输出格式的上下文
    ret = avformat_alloc_output_context2(&formatCtx, NULL, ourFormat, outPath);
    //7.打开网络输出流
    if (avio_open(&formatCtx->pb, outPath, AVIO_FLAG_WRITE) < 0) {
        LOGE("Could not open output URL '%s'", outPath);
    }

    ofmt = formatCtx->oformat;
    //8.写文件头部
    if (avformat_write_header(formatCtx, NULL) < 0) {
        LOGE("Error occurred when opening output URL");
    }
    return 0;
}

/**
 * 可以开始写入音视频数据了
 * @param avData
 */
void AVMuxer::dequeue(AVData avData) {
    if (!formatCtx)return;
    if (avData.isAudio) {

    } else {

    }
    AVPacket *avPacket = (AVPacket *) avData.data;
    int ret = av_interleaved_write_frame(formatCtx, avPacket);
    if (ret < 0) {
        LOGE("Error write frame %s", av_err2str(ret));
    }
}


/**
 * 清理资源
 */
void AVMuxer::clear() {
    IMuxer::clear();
}

void AVMuxer::close() {
    if (!formatCtx)return;
//9.收尾工作
    av_write_trailer(formatCtx);
    if (formatCtx && !(ofmt->flags & AVFMT_NOFILE)) {
        avio_close(formatCtx->pb);

    }
    avformat_free_context(formatCtx);
    formatCtx = 0;
}


