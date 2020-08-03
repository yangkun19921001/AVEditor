//
// Created by 阳坤 on 2020-05-23.
//

#include "KAVPlayerBuilder.h"



IDemux *KAVPlayerBuilder::createDemux() {
    IDemux *demux = new KAVDemux();
    return demux;
}

IDecode *KAVPlayerBuilder::createDecode() {
    IDecode *decode = new KAVDecode();
    return decode;
}

IResample *KAVPlayerBuilder::createResample() {
    IResample *resample = new KAVResample();
    return resample;
}

IVideoPlayer *KAVPlayerBuilder::createVideoPlayer() {
    IVideoPlayer *videoPlayer = new KAV_GL_VideoPlayer();
    return videoPlayer;
}

IAudioPlayer *KAVPlayerBuilder::createAudioPlayer() {
    IAudioPlayer *audioPlayer = new KAV_SL_AudioPlayer();
    return audioPlayer;
}

IPlayer *KAVPlayerBuilder::createPlayer(unsigned char index) {
    return IPlayer::getInstance(index);
}

void KAVPlayerBuilder::initMediaCodec(void *vm) {
    KAVDecode::initMediaCodec(vm);

}
