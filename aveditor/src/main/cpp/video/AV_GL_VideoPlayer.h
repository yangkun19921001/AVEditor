//
// Created by 阳坤 on 2020-05-22.
//

#ifndef IAVEDIT_AV_GL_VIDEOPLAYER_H
#define IAVEDIT_AV_GL_VIDEOPLAYER_H


#include "base/data/AVData.h"
#include "IVideoPlayer.h"
#include "ITexture.h"
#include "AVTxture.h"
#include <jni.h>
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

    /**
     * 是否在 native 端进行渲染
     */
    int isNativeRender = 1;

    JNIEnv *env = 0;
    JavaVM *jvm = 0;
    jobject obj = 0;
    jmethodID mYuvToJavaMethodId;
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

    virtual void setNativeRender(JavaVM*javaVM,JNIEnv *env,jobject obj,int isRender=1) ;

    void toJava(AVData data);


};


#endif //IAVEDIT_AV_GL_VIDEOPLAYER_H
