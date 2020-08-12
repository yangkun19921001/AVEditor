package com.devyk.aveditor.controller

import android.media.MediaCodec
import android.media.MediaFormat
import com.devyk.aveditor.audio.AudioProcessor
import com.devyk.aveditor.callback.IController
import com.devyk.aveditor.callback.OnAudioEncodeListener
import com.devyk.aveditor.config.AudioConfiguration
import com.devyk.aveditor.mediacodec.AudioEncoder

import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-07-15 22:05
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioController 音频采集和音频编码的控制
 * </pre>
 */

public class AudioController(audioConfiguration: AudioConfiguration) : IController, AudioProcessor.OnRecordListener,
    OnAudioEncodeListener {


    /**
     * 音频采集-》编解码 需要用到的默认参数
     */
    private var mAudioConfiguration = AudioConfiguration.createDefault()

    /**
     * 音频编解码用到的实体程序
     */
    private lateinit var mAudioEncoder: AudioEncoder

    /**
     * 音频采集用到的实体程序
     */
    private lateinit var mAudioProcessor: AudioProcessor

    /**
     * 音频数据的监听
     */
    private var mAudioDataListener: IController.OnAudioDataListener? = null


    init {
        mAudioConfiguration = audioConfiguration
        mAudioProcessor = AudioProcessor()
        mAudioEncoder = AudioEncoder(mAudioConfiguration)
        mAudioProcessor.init(
            mAudioConfiguration.audioSource,
            mAudioConfiguration.frequency,
            mAudioConfiguration.channelCount
        )
        mAudioProcessor.addRecordListener(this)
        mAudioEncoder.setOnAudioEncodeListener(this)
    }


    /**
     * 触发 开始
     */
    override fun start() {
        mAudioProcessor.startRcording()
    }

    /**
     * 触发 暂停
     */
    override fun pause() {
        mAudioProcessor.setPause(true)
    }

    /**
     * 触发恢复
     */
    override fun resume() {
        mAudioProcessor.setPause(false)
    }

    /**
     * 触发停止
     */
    override fun stop() {
        mAudioProcessor.stop()

    }

    /**
     * 当采集 PCM 数据的时候返回
     */
    override fun onPcmData(pcmData: ByteArray) {
        mAudioEncoder?.enqueueCodec(pcmData)
    }

    /**
     * 当开始采集
     */
    override fun onStart(sampleRate: Int, channels: Int, sampleFormat: Int) {
        mAudioEncoder?.start()
    }

    /**
     * 设置禁言
     */
    override fun setMute(isMute: Boolean) {
        super.setMute(isMute)
        mAudioProcessor?.setMute(isMute)


    }

    override fun onStop() {
        super.onStop()
        mAudioEncoder?.stop()
    }

    /**
     * 当采集出现错误
     */
    override fun onError(meg: String?) {
        mAudioDataListener?.onError(meg)
    }

    /**
     * 当 Audio 编码数据的时候
     */
    override fun onAudioEncode(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        mAudioDataListener?.onAudioData(bb, bi)
    }

    /**
     * 编码的输出格式
     */
    override fun onAudioOutformat(outputFormat: MediaFormat?) {
        mAudioDataListener?.onAudioOutformat(outputFormat)
    }

    override fun setAudioDataListener(audioDataListener: IController.OnAudioDataListener) {
        super.setAudioDataListener(audioDataListener)
        mAudioDataListener = audioDataListener
    }

}