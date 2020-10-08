//
// Created by 阳坤 on 2020-08-31.
//

#ifndef IKAVEDIT_IEDITOR_H
#define IKAVEDIT_IEDITOR_H


#include <deque>
#include <jni.h>
#include "../base/IObserver.h"
#include "../entity/MediaEntity.h"


using namespace std;

/**
 * 负责音视频编辑的抽象类
 */
class IEditor : public IObserver {

protected:

    char * outPath = 0;

public:
    /**
     * 初始化资源
     */
    virtual int open(const char *url, deque<MediaEntity *> medialists);

    /**
     * 有新的数据更新
     * @param data
     */
    virtual void update(AVData data) = 0;
    virtual void onComplete() = 0;

    /**
     *开始编辑
     * @return
     */
    virtual int start();

    /**
     * 停止编辑
     */
    virtual int close() = 0;

    /**
     * 设置需要合并的资源
     * @param jniEnv
     * @param lists
     */
    virtual void setMergeSource(JNIEnv *jniEnv, jobject lists) = 0;

    virtual deque<MediaEntity *> getMergeSource() = 0;



protected:
    /**
     * 子线程入口
     */
    virtual void main() = 0;


};


#endif //IKAVEDIT_IEDITOR_H
