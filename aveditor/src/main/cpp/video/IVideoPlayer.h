//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IAVEDIT_IVIDEOPLAYER_H
#define IAVEDIT_IVIDEOPLAYER_H


#include <jni.h>
#include "base/IObserver.h"

/**
 * 抽象视频播放
 */
class IVideoPlayer : public IObserver {


public:
    /**
     * 设置 render
     */
    virtual void setRender(void *surface) = 0;

    /**
     * 渲染的数据
     */
    virtual void render(AVData data) = 0;

    /**
     * 有新的数据更新
     */
    virtual void update(AVData data);

    /**
     * 关闭
     */
    virtual void close() = 0;

    /**
     * 设置是否在 native 端进行渲染
     * @param isRender
     */
    virtual void setNativeRender(JavaVM*javaVM,JNIEnv *env,jobject obj,int isRender) = 0;
};


#endif //IAVEDIT_IVIDEOPLAYER_H
