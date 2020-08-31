//
// Created by 阳坤 on 2020-05-23.
//

#include "AVPlayerBuilder.h"


IDemux *AVPlayerBuilder::createDemux() {
    IDemux *demux = new AVDemux();
    return demux;
}

IDecode *AVPlayerBuilder::createDecode() {
    IDecode *decode = new AVDecode();
    return decode;
}

IResample *AVPlayerBuilder::createResample() {
    IResample *resample = new AVResample();
    return resample;
}

IVideoPlayer *AVPlayerBuilder::createVideoPlayer() {
    IVideoPlayer *videoPlayer = new AV_GL_VideoPlayer();
    return videoPlayer;
}

IAudioPlayer *AVPlayerBuilder::createAudioPlayer() {
    IAudioPlayer *audioPlayer = new AV_SL_AudioPlayer();
    return audioPlayer;
}

IPlayer *AVPlayerBuilder::createPlayer(unsigned char index) {
    return IPlayer::getInstance(index);
}

int AVPlayerBuilder::initMediaCodec(void *vm) {
    return   AVDecode::initMediaCodec(vm);

}

ITransfer *AVPlayerBuilder::createTransfer() {
    return new AVAudioTransfer();
}
