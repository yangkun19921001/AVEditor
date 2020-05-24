//
// created by 阳坤 on 2020-05-23.
//

#include "IPlayerBuilder.h"

IPlayer *IPlayerBuilder::builderPlayer(unsigned char index) {
    IPlayer *play = createPlayer(index);

    //FFmpeg 解封装
    IDemux *de = createDemux();

    //FFmpeg 视频解码
    IDecode *vdecode = createDecode();

    //FFmpeg 音频解码
    IDecode *adecode = createDecode();

    //音视频解码模块订阅解封装之后产生的数据。
    de->registers(vdecode);
    de->registers(adecode);

    //视频渲染模块
    IVideoPlayer *view = createVideoPlayer();
    //渲染模块订阅视频解码的数据
    vdecode->registers(view);

    //音频重采样订阅音频解码模块
    IResample *resample = createResample();
    adecode->registers(resample);

    //音频播放观察重采样之后的数据
    IAudioPlayer *audioPlay = createAudioPlayer();
    resample->registers(audioPlay);

    play->demux = de;
    play->adecode = adecode;
    play->vdecode = vdecode;
    play->videoView = view;
    play->resample = resample;
    play->audioPlay = audioPlay;
    return play;
}
