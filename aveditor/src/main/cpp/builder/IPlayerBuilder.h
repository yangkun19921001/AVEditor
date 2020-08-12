//
// created by 阳坤 on 2020-05-23.
//

#ifndef IAVEDIT_IPLAYERBUILDER_H
#define IAVEDIT_IPLAYERBUILDER_H


#include "play/IPlayer.h"

/**
 * 构建模块的抽象类
 */
class IPlayerBuilder {
public:
    /**
     * 构建一个 IPlayer 组合
     * @param index
     * @return
     */
    virtual IPlayer *builderPlayer(unsigned char index = 0);

protected:
    /**
     * 创建解复用模块
     * @return
     */
    virtual IDemux *createDemux() = 0;

    /**
     * 创建解码模块
     * @return
     */
    virtual IDecode *createDecode() = 0;

    /**
     * 创建重采样模块
     * @return
     */
    virtual IResample *createResample() = 0;

    /**
     * 创建视频播放模块
     * @return
     */
    virtual IVideoPlayer *createVideoPlayer() = 0;

    /**
     * 创建音频播放模块
     * @return
     */
    virtual IAudioPlayer *createAudioPlayer() = 0;

    /**
     * 构建播放组合管理模块
     * @param index
     * @return
     */
    virtual IPlayer *createPlayer(unsigned char index = 0) = 0;

};


#endif //IAVEDIT_IPLAYERBUILDER_H
