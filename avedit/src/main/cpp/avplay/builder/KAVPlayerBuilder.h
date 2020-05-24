//
// Created by 阳坤 on 2020-05-23.
//

#ifndef IKAVEDIT_KAVPLAYERBUILDER_H
#define IKAVEDIT_KAVPLAYERBUILDER_H


#include "IPlayerBuilder.h"
#include "../demux/KAVDemux.h"
#include "../decode/KAVDecode.h"
#include "../resample/KAVResample.h"
#include "../video/KAV_GL_VideoPlayer.h"
#include "../audio/KAV_SL_AudioPlayer.h"

class KAVPlayerBuilder : public IPlayerBuilder {
public:
    /**
     * 初始化硬件解码
     * @param vm
     */
    static void initMediaCodec(void *vm);

    static KAVPlayerBuilder *getInstance() {
        static KAVPlayerBuilder ff;
        return &ff;
    }

protected:
    /**
   * 创建解复用模块
   * @return
   */
    virtual IDemux *createDemux();

    /**
     * 创建解码模块
     * @return
     */
    virtual IDecode *createDecode();

    /**
     * 创建重采样模块
     * @return
     */
    virtual IResample *createResample();

    /**
     * 创建视频播放模块
     * @return
     */
    virtual IVideoPlayer *createVideoPlayer();

    /**
     * 创建音频播放模块
     * @return
     */
    virtual IAudioPlayer *createAudioPlayer();

    /**
     * 构建播放组合管理模块
     * @param index
     * @return
     */
    virtual IPlayer *createPlayer(unsigned char index = 0);

};


#endif //IKAVEDIT_KAVPLAYERBUILDER_H
