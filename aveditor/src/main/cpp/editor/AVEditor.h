//
// Created by 阳坤 on 2020-08-31.
//

#ifndef IKAVEDIT_AVEDITOR_H
#define IKAVEDIT_AVEDITOR_H


#include "IEditor.h"
#include "../muxer/mp4_muxer.h"
#include "../demux/AVDemux.h"

#define READING 1
#define NOT_READING 0
#define READ_COMPLETE 2

class AVEditor : public IEditor {


protected:

    /**
     * Mp4 打包器
     */
    Mp4Muxer *mp4Muxer = 0;

    /**
     * 将数据解封装
     */
    IDemux *demux = 0;

    /**
     * 互斥锁
     */
    std::mutex mux;

    /**
     * 当前解封装的目标索引
     */
    int nextIndex = 0;

    /**
     * 进度
     */
    int progress = 0;


    /**
    * 当前片段的时长
    */
    int totalDuration = 0;

    /**
     * 是否是 第一段数据
     */
     int isAFirst = false;
     int isVFirst = false;

    int curAPts = 0;
    int curVPts = 0;

    int64_t preAPts = 0;
    int64_t preVPts = 0;


    /**
     * 当前读取数据的状态
     */
    int curStatus = NOT_READING;


    AVBitStreamFilterContext *mAudioFilter = 0;
    AVBitStreamFilterContext *mVideoFilter = 0;

    AVParameter vParameter;
    AVParameter aParameter;


public:
    /**
     * 初始化资源
     */
    virtual int open(const char *url, deque<MediaEntity *> medialists);

    /**
     * 有新的数据更新
     * @param data
     */
    virtual void update(AVData data);

    virtual void onComplete();

    /**
     *开始编辑
     * @return
     */
    virtual int start();

    /**
     * 停止编辑
     */
    virtual int close();

    /**
 * 设置下一个片段播放路径
 */
    virtual MediaEntity *getNextDataSource();

    /**
     * 播放下一个片段
     */
    virtual int playNext();

    /**
     * 语音时间戳
     * @return
     */
    virtual int64_t getAPTSUs();

    /**
      * 视频时间戳
     * @return
     */
    virtual int64_t getVPTSUs();


protected:
    /**
     * 子线程入口
     */
    virtual void main();
};


#endif //IKAVEDIT_AVEDITOR_H
