package com.devyk.aveditor.mediacodec

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.config.AudioConfiguration
import com.devyk.aveditor.entity.Speed

import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-06-13 23:53
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseCoder
 * </pre>
 */
abstract class BaseAudioCodec(private var mAudioConfiguration: AudioConfiguration?) : IAudioCodec {
    private var mMediaCodec: MediaCodec? = null


    internal var mBufferInfo = MediaCodec.BufferInfo()

    private var TAG = javaClass.simpleName
    private var mPts = 0L

    /**
     * 播放速度
     */
    private var mSpeed = Speed.NORMAL.value
    private var prevOutputPTSUs: Long = 0
    private var mNewFormat: MediaFormat? = null

    /**
     * 编码完成的函数自己不处理，交由子类处理
     */
    abstract fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo);

    public fun setAudioConfiguration(audioConfiguration: AudioConfiguration) {
        mAudioConfiguration = audioConfiguration
    }

    @Synchronized
    override fun start() {
        mMediaCodec = AudioMediaCodec.getAudioMediaCodec(mAudioConfiguration!!)
        mMediaCodec!!.start()
        Log.e("encode", "--start")
    }

    /**
     * 将数据入队 java.lang.IllegalStateException
     */
    @Synchronized
    override fun enqueueCodec(input: ByteArray?) {
        if (mMediaCodec == null) {
            return
        }
        val inputBuffers = mMediaCodec!!.inputBuffers
        var outputBuffers = mMediaCodec!!.outputBuffers
        val inputBufferIndex = mMediaCodec!!.dequeueInputBuffer(12000)

        if (inputBufferIndex >= 0) {
            val inputBuffer = inputBuffers[inputBufferIndex]
            inputBuffer.clear()
            inputBuffer.put(input)
            mMediaCodec!!.queueInputBuffer(inputBufferIndex, 0, input!!.size, 0, 0)
        }
        var outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo, 12000)
        if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            onAudioOutformat(mMediaCodec?.outputFormat)
        }

        while (outputBufferIndex >= 0) {
            val outputBuffer = outputBuffers?.get(outputBufferIndex)
            outputBuffer?.let { outputBuffer ->
                if (mBufferInfo.size != 0) {
                    mBufferInfo.presentationTimeUs = getPTSUs()
                    LogHelper.e(TAG, "音频时间戳：${mBufferInfo!!.presentationTimeUs / 1000_000}")
                    onAudioData(outputBuffer, mBufferInfo)
                    prevOutputPTSUs = mBufferInfo.presentationTimeUs
                    mMediaCodec!!.releaseOutputBuffer(outputBufferIndex, false)
                    outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo, 0)
                }
            }
        }
    }


    abstract fun onAudioOutformat(outputFormat: MediaFormat?)

    @Synchronized
    override fun stop() {
        if (mMediaCodec != null) {
            mMediaCodec!!.stop()
            mMediaCodec!!.release()
            mMediaCodec = null
        }
    }

    protected fun getPTSUs(): Long {
        var result = System.nanoTime() / 1000L
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs) {
            result = prevOutputPTSUs - result + result
        }
        return result
    }

    /**
     * 获取输出的格式
     */
    public fun getOutputFormat(): MediaFormat? = mMediaCodec?.outputFormat


}