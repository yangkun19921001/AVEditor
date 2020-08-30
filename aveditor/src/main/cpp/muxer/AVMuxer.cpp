//
// Created by 阳坤 on 2020-08-20.
//

#include "AVMuxer.h"


/**
 * 初始化默认
 */
int AVMuxer::init(const char *path, int video_width, int video_height, int frame_rate, int video_bit_rate,
                  int audio_sample_rate, int audio_channels, int audio_bit_rate, char *tag_name) {

    isInitSucceed = -1;
    outUrl = new char[strlen(path) + 1];
    strcpy(outUrl, path);


    mp4Muxer = new Mp4Muxer();
    if (mp4Muxer->Init(outUrl, video_width, video_height, frame_rate, video_bit_rate, audio_sample_rate, audio_channels,
                       audio_bit_rate, tag_name) < 0) {
        LOGE("Mp4Muxer init fail.");
        mp4Muxer->Stop();
        return -1;
    } else {
        isInitSucceed = 1;
    }
    return isInitSucceed;
}

int AVMuxer::start() {
    int ret = IMuxer::start();
    return ret;
}

void AVMuxer::clear() {
    close();
}

void AVMuxer::close() {

    if (mp4Muxer) {
        mp4Muxer->Stop();
        mp4Muxer = NULL;
    }

}

int AVMuxer::enqueue(AVData avData) {
    int ret = 0;
    if (mp4Muxer)
        mp4Muxer->enqueue(avData.data, avData.isAudio, avData.size, avData.pts);
    return ret;
}


/**
 * 这里 < 0 退出
 * @return
 */
int AVMuxer::dequeue() {
    int ret = 0;
    if (mp4Muxer)
        ret = mp4Muxer->Encode();
    return ret;
}







