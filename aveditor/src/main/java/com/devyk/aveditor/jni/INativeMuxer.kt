package com.devyk.aveditor.jni

import com.devyk.aveditor.stream.packer.PackerType

/**
 * <pre>
 *     author  : devyk on 2020-08-20 17:32
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is INativeMuxer
 * </pre>
 */

public interface INativeMuxer {

//    const char *outPath, const char *outFormat = MP4,
//    int video_width, int video_height,
//    int frame_rate, int video_bit_rate,
//    int audio_sample_rate, int audio_channels, int audio_bit_rate

    fun initMuxer(
        outPath: String?,
        videoWidth: Int,
        videoHeight: Int,
        frame_rate: Int,
        videoBitRate: Int,
        audioSampleRate: Int,
        audioChannels: Int,
        audioBitRate: Int
    );

    fun enqueue(byteArray: ByteArray?, isAudio: Boolean = false, pts: Long);

    fun close()

}