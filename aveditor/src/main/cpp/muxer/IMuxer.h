//
// Created by 阳坤 on 2020-08-20.
//

#ifndef IKAVEDIT_IMUXER_H
#define IKAVEDIT_IMUXER_H


#include "../base/IObserver.h"
#include "../utils/AVQueue.h"


#define FLV "flv"
#define MP4 "mp4"


/**
 * 音视频封装父类
 */
class IMuxer : public IObserver {

public:
    /**
     * 初始化
     * @param videoOutputURI
     * @param videoWidth
     * @param videoHeight
     * @param videoFrameRate
     * @param videoBitRate
     * @param audioSampleRate
     * @param audioChannels
     * @param audioBitRate
     * @return
     */
    virtual int init(const char* path, int video_width, int video_height, int frame_rate, int video_bit_rate,
                     int audio_sample_rate, int audio_channels, int audio_bit_rate,
                     char* tag_name) = 0;

    /**
     * 开始合并音视频线程
     */
    virtual int start();

    /**
    * 这里有新的音视频数据的更新
    * @param data
    */
    virtual void update(AVData data);

    /**
     * 清理缓存资源
     * @return
     */
    virtual void clear() = 0;

    /**
     * 关闭封装器
     */
    virtual void close() = 0;

    /**
     * 将音视频数据入队
     * @param avData
     * @return
     */
    virtual int enqueue(AVData avData) = 0;

    /**
     * 有数据就会出队
     * @param data
     */
    virtual int dequeue() = 0;

protected:
    /**
     * 子线程入口
     */
    virtual void main();


};


#endif //IKAVEDIT_IMUXER_H
