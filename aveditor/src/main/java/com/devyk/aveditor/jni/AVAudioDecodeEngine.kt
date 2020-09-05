package com.devyk.aveditor.jni

import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.utils.LogHelper.TAG

/**
 * <pre>
 *     author  : devyk on 2020-08-12 17:01
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVAudioDecodeEngine 负责文件解码
 * </pre>
 */
public class AVAudioDecodeEngine : IMusicDecode {


    private var mListener: IMusicDecode.OnDecodeListener? = null

    /**
     * C++ 实现
     * 初始化解码器
     */
    public external override fun addRecordMusic(musicPath: String?)

    /**
     * C++ 实现
     * 开始解码
     */
    public external override fun start()

    /**
     * C++ 实现
     * 暂停解码
     */
    public external override fun pause()

    /**
     * 恢复解码
     */
    public external override fun resume()

    /**
     * C++ 实现
     * 停止解码
     */
    public external override fun stop()

    /**
     * C++ 调用
     */
    fun onPCMData(data: ByteArray) {
        data?.let {
            mListener?.let {
                it.onDecodeData(data)
            }
        }
    }

    /**
     * C++ 调用
     */
    fun onDecodeStart(sampleRate: Int, channels: Int, sampleFormat: Int) {
        LogHelper.e(TAG, "sampleRate:${sampleRate} channels:${channels} sampleFormat:${sampleFormat}")
        mListener?.let {
            it.onDecodeStart(sampleRate, channels, sampleFormat)
        }
    }

    /**
     * C++ 调用
     */
    fun onDecodeStop() {
        LogHelper.e(TAG, "onDecodeStop")
        mListener?.let {
            it.onDecodeStop()
        }

    }

    override fun addOnDecodeListener(listener: IMusicDecode.OnDecodeListener) {
        mListener = listener
    }

}