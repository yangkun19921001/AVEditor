package com.devyk.aveditor.mediacodec

import android.annotation.TargetApi
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.config.VideoConfiguration
import com.devyk.aveditor.entity.Speed


import java.util.concurrent.locks.ReentrantLock
import kotlin.experimental.and

/**
 * <pre>
 *     author  : devyk on 2020-06-15 21:46
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoEncodec
 *
 *
 *     @see https://www.cnblogs.com/tinywan/p/6402007.html H264编码profile & level控制
 * </pre>
 */
public abstract class BaseVideoEncoder : IVideoCodec {

    private var mMediaCodec: MediaCodec? = null
    protected var mPause: Boolean = false
    private var mHandlerThread: HandlerThread? = null
    protected var mEncoderHandler: Handler? = null
    protected var mConfiguration = VideoConfiguration.createDefault()
    private var mBufferInfo = MediaCodec.BufferInfo()
    @Volatile
    private var isStarted: Boolean = false
    private val encodeLock = ReentrantLock()
    private lateinit var mSurface: Surface
    public val TAG = this.javaClass.simpleName
    private val IDR = 5
    private val SPS = 7
    private val BYTES_HEADER = byteArrayOf(0, 0, 0, 1)

    private var mNewFormat: MediaFormat? = null


    protected var mPts = 0L

    /**
     * 播放速度
     */
    private var mSpeed = Speed.NORMAL.value
    private var prevOutputPTSUs: Long = 0

    /**
     * 准备硬编码工作
     */
    override fun prepare(videoConfiguration: VideoConfiguration) {
        videoConfiguration?.run {
            mConfiguration = videoConfiguration
            mMediaCodec = VideoMediaCodec.getVideoMediaCodec(mConfiguration)
            LogHelper.e(TAG, "prepare success!")
        }
    }

    /**
     * 渲染画面销毁了 open 子类可以重写
     */
    protected open fun onSurfaceDestory(surface: Surface?) {
    }

    /**
     * 可以创建渲染画面了 open 子类可以重写
     */
    protected open fun onSurfaceCreate(surface: Surface?) {

    }


    /**
     * 创建一个输入型的 Surface
     */
    open fun getSurface(): Surface? {
        return mSurface
    }


    /**
     * 开始编码
     */
    override fun start() {
        mHandlerThread = HandlerThread("Media-Video-Encode")

        mHandlerThread?.run {
            this.start()
            mEncoderHandler = Handler(getLooper())
//            mBufferInfo = MediaCodec.BufferInfo()
            //必须在  mMediaCodec?.start() 之前
            mSurface = mMediaCodec!!.createInputSurface()
            mMediaCodec?.start()

            isStarted = true
            //必须在  mMediaCodec?.start() 之后
            onSurfaceCreate(mSurface)
        }
    }


    public fun loopEncode() {
        mEncoderHandler?.post(swapDataRunnable)
    }

    /**
     * 编码的线程
     */
    private val swapDataRunnable = Runnable { drainEncoder() }


    /**
     * 停止编码
     */
    override fun stop() {
        if (!isStarted) return
        encodeLock.lock()
        isStarted = false
        mEncoderHandler?.removeCallbacks(swapDataRunnable)
        mHandlerThread?.quit()
        //提交一个空的缓冲区
        mMediaCodec?.signalEndOfInputStream()
        releaseEncoder()
        encodeLock.unlock()
//
//        if (!RecordSemaphore.isStart)
//            RecordSemaphore.mSemaphore.release(1)
    }

    /**
     * 释放编码器
     */
    private fun releaseEncoder() {
        onSurfaceDestory(getSurface())
        mMediaCodec?.stop()
        mMediaCodec?.release()
        mMediaCodec = null
    }


    /**
     * 动态码率设置
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun setEncodeBps(bps: Int) {
        if (mMediaCodec == null) {
            return
        }
        LogHelper.d(TAG, "bps :" + bps * 1024)
        val bitrate = Bundle()
        bitrate.putInt(MediaCodec.PARAMETER_KEY_VIDEO_BITRATE, bps * 1024)
        mMediaCodec?.setParameters(bitrate)
    }

    /**
     * 解码函数
     */
    private fun drainEncoder() {
        var outBuffers = mMediaCodec?.getOutputBuffers()
        if (!isStarted) {
            // if not running anymore, complete stream
            mMediaCodec?.signalEndOfInputStream()
        }
        while (isStarted) {
            encodeLock.lock()
            if (mMediaCodec != null) {
                val outBufferIndex = mMediaCodec?.dequeueOutputBuffer(mBufferInfo!!, 12000)



                if (outBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    onVideoOutformat(mMediaCodec?.outputFormat)
                }

                if (outBufferIndex!! < 0) {
                    Thread.sleep(10)
                    encodeLock.unlock()
                    continue
                }

                val bb = outBuffers!![outBufferIndex!!]


                if (mBufferInfo.size != 0) {
                    if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM && mBufferInfo.presentationTimeUs < 0) {
                        mBufferInfo.presentationTimeUs = 0
                    }
                    LogHelper.e(
                        TAG,
                        "视频时间戳：${mBufferInfo!!.presentationTimeUs} ---> ${mBufferInfo!!.presentationTimeUs / 1000_000}"
                    )
                    if (!mPause) {
                        onVideoEncode(bb, mBufferInfo!!)
                    }
                    prevOutputPTSUs = mBufferInfo.presentationTimeUs
                    mMediaCodec?.releaseOutputBuffer(outBufferIndex, false)
                }
                encodeLock.unlock()
            } else {
                encodeLock.unlock()
                break
            }

        }
    }


    /**
     * 绘制
     */
    public fun drawEncode() {
        encodeLock.lock()
        var outBuffers = mMediaCodec?.getOutputBuffers()
        if (!isStarted) {
            // if not running anymore, complete stream
            mMediaCodec?.signalEndOfInputStream()
        }

        if (mMediaCodec != null) {
            val outBufferIndex = mMediaCodec?.dequeueOutputBuffer(mBufferInfo!!, 12000)



            if (outBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                onVideoOutformat(mMediaCodec?.outputFormat)
            }

            if (outBufferIndex!! < 0) {
                Thread.sleep(10)
                encodeLock.unlock()
                return
            }

            val bb = outBuffers!![outBufferIndex!!]

            // config data sps/pps
            if ((mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                // The codec config_ data was pulled out when we got the
                // INFO_OUTPUT_FORMAT_CHANGED status. The MediaMuxer won't
                // accept
                // a single big blob -- it wants separate csd-0/csd-1 chunks --
                // so simply saving this off won't work.
                if (mBufferInfo.size != 0) {
                    if (!mPause) {
                        onVideoEncode(bb, mBufferInfo!!)
                    }
                    mBufferInfo.size = 0
                }
            }

            if (mBufferInfo.size != 0 && mBufferInfo.presentationTimeUs > prevOutputPTSUs) {
                LogHelper.e(
                    TAG,
                    "视频时间戳：${mBufferInfo!!.presentationTimeUs} ---> ${mBufferInfo!!.presentationTimeUs / 1000_000}"
                )
                if (!mPause) {
                    onVideoEncode(bb, mBufferInfo!!)
                }
                prevOutputPTSUs = mBufferInfo.presentationTimeUs
                mMediaCodec?.releaseOutputBuffer(outBufferIndex, false)
            }
        }
        encodeLock.unlock()
    }


    private fun isH264StartCode(code: Int): Boolean {
        return code == 0x01
    }

    private fun isH264StartCodePrefix(code: Int): Boolean {
        return code == 0x00
    }

    private fun findAnnexbStartCodecIndex(data: ByteArray, offset: Int): Int {
        var index = offset
        var cursor = offset
        while (cursor < data.size) {
            val code = data[cursor++].toInt()
            if (isH264StartCode(code) && cursor >= 3) {
                val firstPrefixCode = data[cursor - 3].toInt()
                val secondPrefixCode = data[cursor - 2].toInt()
                if (isH264StartCodePrefix(firstPrefixCode) && isH264StartCodePrefix(secondPrefixCode)) {
                    break
                }
            }
        }
        index = cursor
        return index
    }

    private fun splitIDRFrame(h264Data: ByteArray): ByteArray {
        var result = h264Data
        var offset = 0
        do {
            offset = findAnnexbStartCodecIndex(h264Data, offset)
            val naluSpecIndex = offset
            if (naluSpecIndex < h264Data.size) {
                val naluType = (h264Data[naluSpecIndex] and 0x1F).toInt()
                if (naluType == IDR) {
                    val idrFrameLength = h264Data.size - naluSpecIndex + 4
                    result = ByteArray(idrFrameLength)
                    System.arraycopy(BYTES_HEADER, 0, result, 0, 4)
                    System.arraycopy(h264Data, naluSpecIndex, result, 4, idrFrameLength - 4)
                    break
                }
            }
        } while (offset < h264Data.size)
        return result
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

    public fun isStart(): Boolean = isStarted

    abstract fun onVideoOutformat(outputFormat: MediaFormat?)

}