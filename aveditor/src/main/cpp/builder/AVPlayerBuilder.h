//
// Created by 阳坤 on 2020-05-23.
//

#ifndef IAVEDIT_AVPLAYERBUILDER_H
#define IAVEDIT_AVPLAYERBUILDER_H


#include "IPlayerBuilder.h"
#include "demux/AVDemux.h"
#include "decode/AVDecode.h"
#include "resample/AVResample.h"
#include "video/AV_GL_VideoPlayer.h"
#include "audio/AV_SL_AudioPlayer.h"

class AVPlayerBuilder : public IPlayerBuilder {
public:
    /**
     * 初始化硬件解码
     * @param vm
     */
    static void initMediaCodec(void *vm);

    static AVPlayerBuilder *getInstance() {
        static AVPlayerBuilder ff;
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
    * 将解码完成的数据转移
    * @return
    */
    virtual ITransfer *createTransfer();

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


#endif //IAVEDIT_AVPLAYERBUILDER_H
