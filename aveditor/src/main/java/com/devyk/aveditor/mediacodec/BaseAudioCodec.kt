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


    /**
     * 将数据入队 java.lang.IllegalStateException
     */
    var lastAudioFrameTimeUs: Long = -1

    var AAC_FRAME_TIME_US = 0;

    var detectTimeError = false

    /**
     * 编码完成的函数自己不处理，交由子类处理
     */
    abstract fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo);

    public fun setAudioConfiguration(audioConfiguration: AudioConfiguration) {
        mAudioConfiguration = audioConfiguration
    }

    @Synchronized
    override fun start(speed: Speed) {
        AAC_FRAME_TIME_US = 1024 * 1000 * 1000 / mAudioConfiguration?.frequency!!
        mSpeed = speed.value
        mPts = 0
        frameIndex = 0
        mMediaCodec = AudioMediaCodec.getAudioMediaCodec(mAudioConfiguration!!)
        mMediaCodec!!.start()
        Log.e("encode", "--start")
    }




    /**
     * 音频时间戳计算公式 : audio_ts = n * frame_size * ts_freq / sample_rate
    n : 第n帧音频
    ts_freq : 选定的时间戳的采样频率
    frame_size : 一帧音频的采样数 也即是帧的大小
    sample_rate : 音频的采样频率
     */
    @Synchronized
    override fun <T> enqueueCodec(input: T?) {
        if (mMediaCodec == null) {
            return
        }
        val inputBuffers = mMediaCodec!!.inputBuffers
        var outputBuffers = mMediaCodec!!.outputBuffers
        val inputBufferIndex = mMediaCodec!!.dequeueInputBuffer(12000)

        var inputSize = 0
        if (inputBufferIndex >= 0) {
            if (input is ByteArray) {
                val inputBuffer = inputBuffers[inputBufferIndex]
                inputBuffer.clear()
                inputBuffer.put(input)
                inputSize = input.size
            } else if (input is ShortArray) {
                val inputBuffer = inputBuffers[inputBufferIndex].asShortBuffer()
                inputBuffer.clear()
                inputBuffer.put(input)
                inputBuffer.position(0);
                inputSize = input.size
            }
            var pts = frameIndex * inputSize * 1000 / mAudioConfiguration?.frequency!!
            LogHelper.i(TAG,"audio queuePcmBuffer $pts size:$inputSize");
            mMediaCodec!!.queueInputBuffer(inputBufferIndex, 0, inputSize, pts, 0)
            frameIndex++
        }
        var outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo, 12000)
        if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            onAudioOutformat(mMediaCodec?.outputFormat)
        }

        while (outputBufferIndex >= 0) {
            val outputBuffer = outputBuffers?.get(outputBufferIndex)
            outputBuffer?.let { outputBuffer ->
                if (mBufferInfo.size != 0) {
                    if (!detectTimeError && lastAudioFrameTimeUs != -1L && mBufferInfo.presentationTimeUs < lastAudioFrameTimeUs + AAC_FRAME_TIME_US) {
                        //某些情况下帧时间戳会出错
                        LogHelper.e(TAG,"audio 时间戳错误，lastAudioFrameTimeUs:" + lastAudioFrameTimeUs + " " +
                                "info.presentationTimeUs:" + mBufferInfo.presentationTimeUs);
                        detectTimeError = true;
                    }
                    if (detectTimeError) {
                        mBufferInfo.presentationTimeUs = lastAudioFrameTimeUs + AAC_FRAME_TIME_US;
                        LogHelper.e(TAG,"audio 时间戳错误，使用修正的时间戳:" + mBufferInfo.presentationTimeUs);
                        detectTimeError = false;
                    }
                    if (mBufferInfo.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        lastAudioFrameTimeUs = mBufferInfo.presentationTimeUs;
                    }
//                    mBufferInfo.presentationTimeUs = (getPTSUs()).toLong()
                    LogHelper.e(
                        TAG,
                        "语音时间戳：${mBufferInfo!!.presentationTimeUs} ---> ${mBufferInfo!!.presentationTimeUs / 1000_000} }"
                    )

                    onAudioData(outputBuffer, mBufferInfo)
                    mMediaCodec!!.releaseOutputBuffer(outputBufferIndex, false)
                    outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo, 0)
                }
            }
        }
    }


    abstract fun onAudioOutformat(outputFormat: MediaFormat?)

    @Synchronized
    override fun stop() {
        frameIndex = 0;
        if (mMediaCodec != null) {
            mMediaCodec!!.stop()
            mMediaCodec!!.release()
            mMediaCodec = null
        }
    }

    protected fun getPTSUs(): Long {
        if (mPts == 0L)
            mPts = (System.nanoTime() / 1000).toLong();

//        var result = System.nanoTime() / 1000L
//        // presentationTimeUs should be monotonic
//        // otherwise muxer fail to write
//        if (result < prevOutputPTSUs) {
//            result = prevOutputPTSUs - result + result
//        }
//        return result
        return ((System.nanoTime() / 1000 - mPts).toLong())
    }


    /**
     * 获取输出的格式
     */
    public fun getOutputFormat(): MediaFormat? = mMediaCodec?.outputFormat


    var frameIndex = 0L;
    private fun getCurFramePts(): Long {
        var pts = frameIndex * (1000 / 25 / 0.25) * 1000
        return pts.toLong()
    }

}