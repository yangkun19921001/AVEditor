//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IAVEDIT_AV_GL_VIDEOPLAYER_H
#define IAVEDIT_AV_GL_VIDEOPLAYER_H


#include "base/data/AVData.h"
#include "IVideoPlayer.h"
#include "ITexture.h"
#include "AVTxture.h"

/**
 * 具体视频模块播放
 */
class AV_GL_VideoPlayer : public IVideoPlayer {

protected:
    /**
     * native window
     */
    void *pNativeWindow;

    ITexture *texture;

    /**
     * 互斥锁
     */
    mutex mux;
public:
    /**
     * 设置渲染的 window
     * @param window
     */
    virtual void setRender(void *window);

    /**
     * 渲染数据
     */
     virtual void render(AVData data);

     /**
      * 关闭
      */
      virtual void close();

};


#endif //IAVEDIT_AV_GL_VIDEOPLAYER_H
