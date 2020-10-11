package com.devyk.aveditor.jni

import com.devyk.aveditor.muxer.JavaMp4Muxer

/**
 * <pre>
 *     author  : devyk on 2020-08-20 17:32
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVMuxerEngine
 * </pre>
 */
public class AVJavaMuxerEngine : INativeMuxer {

    public override fun initMuxer(
        outPath: String?,
        videoWidth: Int,
        videoHeight: Int,
        frame_rate: Int,
        videoBitRate: Int,
        audioSampleRate: Int,
        audioChannels: Int,
        audioBitRate: Int
    ) {
        native_initMuxer(
            outPath,
            videoWidth,
            videoHeight,
            frame_rate,
            videoBitRate,
            audioSampleRate,
            audioChannels,
            audioBitRate
        )
    }

    override fun enqueue(byteArray: ByteArray?, isAudio: Boolean, pts: Long) {
        native_enqueue(byteArray, isAudio, pts)
    }

    override fun close() {
        native_close()
    }


    private external fun native_enqueue(byteArray: ByteArray?, isAudio: Boolean, pts: Long);

    private external fun native_close()

    private external fun native_initMuxer(
        outPath: String?,
        videoWidth: Int,
        videoHeight: Int,
        frameRate: Int,
        videoBitRate: Int,
        audioSampleRate: Int,
        audioChannels: Int,
        audioBitRate: Int
    )


    public fun javaMergeVieo(inPath:ArrayList<String>,outPath:String){
        val javaMp4Muxer = JavaMp4Muxer(inPath, outPath)
        javaMp4Muxer.merge()
    }
}