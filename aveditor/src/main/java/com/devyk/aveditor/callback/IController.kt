package com.devyk.aveditor.callback

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-07-15 22:13
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IController
 * </pre>
 */
public interface IController {

    fun start()

    fun pause()

    fun resume()

    fun stop()

    fun setMute(isMute: Boolean) {}

    fun setAudioDataListener(audioDataListener: OnAudioDataListener) {}
    fun setVideoDataListener(videoDataListener: OnVideoDataListener) {}
    fun setVideoBps(bps:Int){}



    public interface OnAudioDataListener {
        /**
         * 当 Audio 编码数据的时候
         */
        fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo);

        /**
         * 编码的输出格式
         */
        fun onAudioOutformat(outputFormat: MediaFormat?)


        fun onError(error:String?);


    }

    public interface OnVideoDataListener {
        /**
         * 当 Audio 编码数据的时候
         */
        fun onVideoData(bb: ByteBuffer?, bi: MediaCodec.BufferInfo?);

        /**
         * 编码的输出格式
         */
        fun onVideoOutformat(outputFormat: MediaFormat?);

        fun onError(error:String?);
    }
}