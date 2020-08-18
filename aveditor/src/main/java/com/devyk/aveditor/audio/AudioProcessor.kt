package com.devyk.aveditor.audio


import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.audio.AudioUtils.AUDIO_CHANNEL_CONFIG
import com.devyk.aveditor.audio.AudioUtils.AUDIO_FROMAT
import com.devyk.aveditor.audio.AudioUtils.SAMPLE_RATE_IN_HZ
import com.devyk.aveditor.audio.AudioUtils.getBufferSize
import java.util.*

/**
 * <pre>
 *     author  : devyk on 2020-07-15 21:16
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioProcessor
 * </pre>
 */
public class AudioProcessor : ThreadImpl() {
    /**
     * 读取大小
     */
    private var mReadSize = 1024;

    /**
     * 录制监听
     */
    private var mRecordListener: OnRecordListener? = null


    /**
     * Java 中的锁
     */
    private val mLock = java.lang.Object()

    /**
     * 是否禁言
     */
    private var isMute = false

    /**
     * 初始化
     */
    public fun init(
        audioSource: Int = AudioUtils.AUDIO_SOURCE,
        sampleRateInHz: Int = AudioUtils.SAMPLE_RATE_IN_HZ,
        channelConfig: Int = AudioUtils.AUDIO_CHANNEL_CONFIG,
        audioFormat: Int = AudioUtils.AUDIO_FROMAT
    ) {
        try {
            if (AudioUtils.initAudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat)) {
                mReadSize = getBufferSize()
            }
        } catch (error: Exception) {
            mRecordListener?.onError(error.message)
            LogHelper.e(LogHelper.TAG, error.message!!)
        }
    }

    override fun setPause(pause: Boolean) {
        super.setPause(pause)
        if (pause == true) {
            mRecordListener?.onPause()
        } else {
            // 恢复线程
            synchronized(mLock) {
                mLock.notifyAll()
            }
            mRecordListener?.onResume()
        }

    }


    /**
     * 开始执行
     */
    fun startRcording() {
        super.start { main() }
        AudioUtils.startRecord()
        mRecordListener?.onStart()
    }


    override fun stop() {
        super.stop()
        AudioUtils.stopRecord()
        mRecordListener?.onStop()
    }

    /**
     * 设置禁言
     */
    public fun setMute(mute: Boolean) {
        this.isMute = mute
    }

    public fun isMute() = isMute

    /**
     * 子线程执行的函数入口
     */
    public fun main() {
        var data = ByteArray(mReadSize);
        while (isRuning()) {
            val name = Thread.currentThread().name
            synchronized(mLock) {
                if (isPause()) {
                    mLock.wait()
                }
                if (isMute()) {
                    Arrays.fill(data, 0)
                    mRecordListener?.onPcmData(data)
                    return@synchronized
                }
                if (AudioUtils.read(data.size, data) > 0) {
                    mRecordListener?.onPcmData(data)
                }
            }
        }
    }


    public fun addRecordListener(listener: OnRecordListener) {
        mRecordListener = listener
    }

    public interface OnRecordListener {
        fun onStart(
            sampleRate: Int = SAMPLE_RATE_IN_HZ,
            channels: Int = AUDIO_CHANNEL_CONFIG,
            sampleFormat: Int = AUDIO_FROMAT
        )
        fun onError(meg: String?)
        fun onPcmData(byteArray: ByteArray);
        fun onPause(){}
        fun onResume(){}
        fun onStop(){}
    }
}