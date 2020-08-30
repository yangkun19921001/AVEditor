#ifndef TRINITY_MP4_MUXER_H
#define TRINITY_MP4_MUXER_H

#include <stdint.h>
#include <string>
#include "audio_packet_queue.h"
#include "video_packet_queue.h"


#define MAX(a, b)  (((a) > (b)) ? (a) : (b))
#define MIN(a, b)  (((a) < (b)) ? (a) : (b))

extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/opt.h"
};

#define AUDIO_QUEUE_ABORT_ERR_CODE               -100200
#define VIDEO_QUEUE_ABORT_ERR_CODE               -100201


class Mp4Muxer {
 public:
    Mp4Muxer();
    virtual ~Mp4Muxer();
    virtual int Init(const char* path, int video_width, int video_height, int frame_rate, int video_bit_rate,
            int audio_sample_rate, int audio_channels, int audio_bit_rate,
                     char* tag_name);

    virtual void RegisterAudioPacketCallback(int (*audio_packet)(AudioPacket**, void* context), void* context);
    virtual void RegisterVideoPacketCallback(int (*video_packet)(VideoPacket**, void* context), void* context);

    int Encode();

    virtual int Stop();

    typedef int (*AudioPacketCallback) (AudioPacket**, void* context);
    typedef int (*VideoPacketCallback) (VideoPacket**, void* context);

    int enqueue(uint8_t *audioData, int isAudio,int size, int pts);

protected:
    uint32_t FindStartCode(uint8_t *in_buffer, uint32_t in_ui32_buffer_size, uint32_t in_ui32_code,
                           uint32_t &out_ui32_processed_bytes);

    void ParseH264SequenceHeader(uint8_t *in_buffer, uint32_t in_ui32_size, uint8_t **in_sps_buffer, int &in_sps_size,
                                 uint8_t **in_pps_buffer, int &in_pps_size);

    virtual int WriteVideoFrame(AVFormatContext* oc, AVStream* st);

    virtual int WriteAudioFrame(AVFormatContext* oc, AVStream* st);

    virtual void CloseVideo(AVFormatContext* oc, AVStream* st);

    virtual void CloseAudio(AVFormatContext* oc, AVStream* st);

    /** 8、获取视频流的时间戳(秒为单位的double) **/
    virtual double GetVideoStreamTimeInSecs();

    /** 9、获取音频流的时间戳(秒为单位的double) **/
    double GetAudioStreamTimeInSecs();

    int BuildVideoStream();

    int BuildAudioStream();


protected:
    // sps and pps data
    uint8_t *header_data_;
    int header_size_;
    AVFormatContext* format_context_;
    AVStream* video_stream_;
    AVCodecContext* video_codec_context_;
    AVStream* audio_stream_;
    AVCodecContext* audio_codec_context_;
    AVBSFContext* bsf_context_;
    double duration_;
    double last_audio_packet_presentation_time_mills_;
    int last_video_presentation_time_ms_;
    int video_width_;
    int video_height_;
    float video_frame_rate_;
    int video_bit_rate_;
    int audio_sample_rate_;
    int audio_channels_;
    int audio_bit_rate_;
    AudioPacketCallback audio_packet_callback_;
    void* audio_packet_context_;
    VideoPacketCallback video_packet_callback_;
    void* video_packet_context_;
    bool write_header_success_;
};


#endif  // TRINITY_MP4_MUXER_H
