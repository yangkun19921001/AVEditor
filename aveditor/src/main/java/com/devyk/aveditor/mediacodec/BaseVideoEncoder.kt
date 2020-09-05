package com.devyk.aveditor.mediacodec

import android.annotation.TargetApi
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.config.VideoConfiguration
import com.devyk.aveditor.entity.Speed
import com.tencent.mars.xlog.Log


import java.util.concurrent.locks.ReentrantLock


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
    private var mNewFormat: MediaFormat? = null


    protected var mPts = 0L

    /**
     * 播放速度
     */
    private var mSpeed = Speed.NORMAL.value


    /**
     * 帧
     */
    private var mFrameIndex = 0;

    /**
     * 显示 PTS
     */
    var lastVideoFrameTimeUs = -1L


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
    override fun start(speed: Speed) {
        mSpeed = speed.value
        mFrameIndex = 0
        mHandlerThread = HandlerThread("Media-Video-Encode")
        mPts = 0
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
        mFrameIndex = 0
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
     * 视频时间戳计算公式 : video_ts = n * ts_freq / fps;
    n : 第n帧视频
    ts_freq : 选定的时间戳的采样频率
    fps : 视频帧率
     */
    public fun drawEncode() {
        encodeLock.lock()
        //检查时间戳是否有误
        var detectTimeError = false
        //视频一帧的的时间戳
        val VIDEO_FRAME_TIME_US = (1000 * 1000f / mConfiguration.fps).toInt()
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

            var pts = getPTSUs().toDouble()
            //视频变速处理
            pts /= mSpeed
            //做变速处理
            mBufferInfo.presentationTimeUs = pts.toLong()


            // config data sps/pps
            if ((mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                if (mBufferInfo.size != 0) {
                    if (!mPause) {
                        onVideoEncode(bb, mBufferInfo!!)
                    }
                    mBufferInfo.size = 0
                }
            }
            if (mBufferInfo.size != 0) {
                if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM && mBufferInfo.presentationTimeUs < 0) {
                    mBufferInfo.presentationTimeUs = 0
                }

                if (!mPause) {
//                    mPts = (mFrameIndex * 1000 / mConfiguration.fps / mSpeed).toLong()
                    LogHelper.e(
                        TAG,
                        "视频时间戳  mPts :${mPts/1000}     Encode :${mBufferInfo.presentationTimeUs}   PTS/1000000:${mBufferInfo.presentationTimeUs / 1000000} "
                    )


                    onVideoEncode(bb, mBufferInfo!!)

                    mFrameIndex++;
                }
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


    public fun isStart(): Boolean = isStarted

    protected fun getPTSUs(): Long {
        if (mPts == 0L)
            mPts = (System.nanoTime() / 1000.0f).toLong();
        return ((System.nanoTime() / 1000.0f - mPts)).toLong()
//        if (mPts == 0L)
//            mPts = (System.nanoTime() / 1000.0f / mSpeed).toLong();
//        return ((System.nanoTime() / 1000.0f / mSpeed - mPts)).toLong()
    }


    abstract fun onVideoOutformat(outputFormat: MediaFormat?)

}