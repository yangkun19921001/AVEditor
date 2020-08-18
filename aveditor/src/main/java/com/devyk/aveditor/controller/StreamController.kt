package com.devyk.aveditor.controller

import RtmpPacker
import android.content.Context
import android.media.MediaCodec
import android.media.MediaFormat
import android.opengl.EGLContext
import android.provider.ContactsContract
import com.devyk.aveditor.callback.IController
import com.devyk.aveditor.config.AudioConfiguration
import com.devyk.aveditor.config.VideoConfiguration
import com.devyk.aveditor.stream.PacketType
import com.devyk.aveditor.stream.packer.Packer
import com.devyk.aveditor.stream.packer.PackerType
import com.devyk.aveditor.stream.packer.mp4.MP4Packer
import com.devyk.aveditor.stream.sender.Sender
import com.devyk.aveditor.utils.FileUtils
import com.devyk.aveditor.utils.LogHelper
import java.io.File

import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

/**
 * <pre>
 *     author  : devyk on 2020-07-15 22:05
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is StreamController
 * </pre>
 */
public class StreamController : IController.OnAudioDataListener, IController.OnVideoDataListener,
    Packer.OnPacketListener {


    private var TAG = javaClass.simpleName


    private var recordAudioSource: String? = null


    /**
     * 音频数据的管理
     */
    private var mAudioController: AudioController? = null
    /**
     * 视频数据的管理
     */
    private var mVideoController: VideoController? = null
    /**
     * 音频采集编码默认配置
     */
    private var mAudioConfiguration = AudioConfiguration.createDefault()
    /**
     * 视频编码默认配置
     */
    private var mVideoConfiguration = VideoConfiguration.createDefault()
    /**
     * 打包器
     */
    private var mPacker: Packer? = null
    /**
     * 发送器
     */
    private var mSender: Sender? = null


    private var mContext: Context? = null
    private var mTextureId = 0
    private var mEGLContext: EGLContext? = null


    /**
     * 设置音频编码和采集的参数
     */
    fun setAudioConfigure(audioConfiguration: AudioConfiguration) {
        this.mAudioConfiguration = audioConfiguration
    }

    /**
     * 设置视频的编码参数
     */
    fun setVideoConfigure(videoConfiguration: VideoConfiguration) {
        this.mVideoConfiguration = videoConfiguration
    }


    /**
     * 设置打包器
     */
    fun setPacker(packer: PackerType, outPath: String? = getCurData()) {
        this.mPacker = MP4Packer(outPath)
        when (packer) {
            PackerType.MP4 -> {
                this.mPacker = MP4Packer(outPath)
            }
            PackerType.FLV -> {
                this.mPacker = MP4Packer(outPath)
            }
            PackerType.RTMP -> {
                this.mPacker = RtmpPacker()
            }
        }
    }

    private fun getCurData(fileType:String?="mp4"): String? {
        val filePath = "${mContext?.cacheDir}${File.separator}${SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Date())}/.${fileType}"
        var exists = FileUtils.createFileByDeleteOldFile(
            "${mContext?.cacheDir}${File.separator}${SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Date())}/.${fileType}"
        )
        return if (exists) filePath else null
    }

    /**
     * 设置发送器
     */
    fun setSender(sender: Sender) {
        this.mSender = sender
    }


    /**
     *  @see start 之前必须调用 prepare
     */
    fun prepare(context: Context?, textureId: Int, eglContext: EGLContext?) {
        this.mContext = context?.applicationContext
        this.mTextureId = textureId
        this.mEGLContext = eglContext
        init()
    }

    private fun init(

    ) {
        mContext?.let { context ->
            if (mPacker == null) {
                setPacker(PackerType.MP4)
            }

            mAudioController = AudioController(mAudioConfiguration)
            mVideoController = VideoController(context, mTextureId, mEGLContext, mVideoConfiguration)
            mPacker?.setPacketListener(this)
            mAudioController?.setAudioDataListener(this)
            mVideoController?.setVideoDataListener(this)

        }
    }

    fun start() {
        LogHelper.e("SORT->", "start")
        if (mAudioController == null || mVideoController == null)
            init()
        mAudioController?.setRecordAudioSource(recordAudioSource)
        mAudioController?.start()
        mVideoController?.start()


    }

    fun pause() {
        mAudioController?.pause()
        mVideoController?.pause()

    }

    fun resume() {
        mAudioController?.resume()
        mVideoController?.resume()
    }

    fun stop() {
        mAudioController?.stop()
        mVideoController?.stop()
        mAudioController = null
        mVideoController = null
        mPacker?.stop()
    }

    fun setMute(isMute: Boolean) {
        mAudioController?.setMute(isMute)
    }

    fun setVideoBps(bps: Int) {
        mVideoController?.setVideoBps(bps)
    }

    override fun onError(error: String?) {
        LogHelper.e(TAG, error!!)
    }


    /**
     * 音频编码之后的数据交于打包器处理
     */
    override fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        mPacker?.onAudioData(bb, bi)
    }

    /**
     * 视频编码数据交于打包
     */
    override fun onVideoData(bb: ByteBuffer?, bi: MediaCodec.BufferInfo?) {
        mPacker?.onVideoData(bb, bi)
    }

    /**
     * 音频输出格式
     */
    override fun onAudioOutformat(outputFormat: MediaFormat?) {
        mPacker?.onAudioOutformat(outputFormat)
    }

    /**
     * 视频输出格式
     */
    override fun onVideoOutformat(outputFormat: MediaFormat?) {
        //这里拿到输出格式是为了打包用
        mPacker?.onVideoOutformat(outputFormat)
    }


    /**
     * 打包完成的数据，准备发送
     */
    override fun onPacket(byteArray: ByteArray, packetType: PacketType) {
        mSender?.onData(byteArray, packetType)
    }

    override fun onPacket(sps: ByteArray?, pps: ByteArray?, packetType: PacketType) {
        mSender?.onData(sps!!, pps!!, packetType)
    }

    /**
     * 交于 Video 进行虚拟渲染
     */
    fun onRecordTexture(showScreenTexture: Int, surfaceTexureTimestamp: Long) {
        mVideoController?.onRecordTexture(showScreenTexture, surfaceTexureTimestamp)
    }

    /**
     * 设置录制音频的音频源，支持任意音频
     */
    fun setRecordAudioSource(recordAudioSource: String?) {
        LogHelper.e("SORT->", "mAudioController setRecordAudioSource")
        this.recordAudioSource = recordAudioSource

    }
}