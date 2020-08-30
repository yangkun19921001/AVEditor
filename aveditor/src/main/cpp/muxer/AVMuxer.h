//
// Created by 阳坤 on 2020-08-20.
//

#ifndef IKAVEDIT_AVMUXER_H
#define IKAVEDIT_AVMUXER_H


#include <string>
#include "IMuxer.h"
#include "mp4_muxer.h"
#define MIN(a, b)  (((a) < (b)) ? (a) : (b))
#define is_start_code(code)    (((code) & 0x0ffffff) == 0x01)

class AVMuxer : public IMuxer {

protected:

    /**
     * 输出的文件路径
     */
    char *outUrl = 0;

    /**
     * 推流或者封装器是否初始化成功
     */
    int isInitSucceed = 0;

    /**
     * Mp4 封住器
     */
    Mp4Muxer *mp4Muxer = 0;

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
                     char* tag_name);

    /**
     * 开始合并音视频线程
     */
    virtual int start();

    /**
     * 清理缓存资源
     * @return
     */
    virtual void clear();

    /**
     * 关闭封装器
     */
    virtual void close();

    /**
     * 将音视频数据入队
     * @param avData
     * @return
     */
    virtual int enqueue(AVData avData);

    /**
     * 有数据就会出队
     * @param data
     */
    virtual int dequeue();

};


#endif //IKAVEDIT_AVMUXER_H
