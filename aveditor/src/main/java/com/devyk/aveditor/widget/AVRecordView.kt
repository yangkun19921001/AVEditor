package com.devyk.aveditor.widget

import android.content.Context
import android.opengl.EGLContext
import android.util.AttributeSet
import com.devyk.aveditor.config.AudioConfiguration
import com.devyk.aveditor.config.VideoConfiguration
import com.devyk.aveditor.controller.StreamController
import com.devyk.aveditor.entity.Speed
import com.devyk.aveditor.stream.packer.Packer
import com.devyk.aveditor.stream.packer.PackerType
import com.devyk.aveditor.stream.packer.mp4.MP4Packer
import com.devyk.aveditor.utils.LogHelper
import javax.microedition.khronos.opengles.GL10


/**
 * <pre>
 *     author  : devyk on 2020-08-08 18:44
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVRecordView
 * </pre>
 */
public class AVRecordView : AVCameraView {


    private var mVideoConfiguration = VideoConfiguration.createDefault()
    private var mAudioConfiguration = AudioConfiguration.createDefault()


    private var mSpeed = Speed.VERY_FAST.value

    /**
     * 音视频流控制器
     */
    private var mStreamController: StreamController? = null


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        //实例化数据流的控制器
        mStreamController = StreamController()
    }


    /**
     * 设置音频编码和采集的参数
     */
    fun setAudioConfigure(audioConfiguration: AudioConfiguration) {
        this.mAudioConfiguration = audioConfiguration
    }

    /**
     * 设置视频编码参数
     */
    fun setVideoConfigure(videoConfiguration: VideoConfiguration) {
        this.mVideoConfiguration = videoConfiguration
    }


    /**
     * 配置打包器
     */
    fun setPaker(packer: PackerType, outPath: String?) {
        mStreamController?.setPacker(packer, outPath)
    }

    /**
     * 设置需要录制的音频源
     */
    public fun setRecordAudioSource(recordAudioSource: String?) {
        LogHelper.e("SORT->", "setRecordAudioSource")
        mStreamController?.setRecordAudioSource(recordAudioSource)

    }

    public fun setBgMusicSource(bgMusic: String) {}

    /**
     * 开始录制
     */
    fun startRecord(speed: Speed) {
        LogHelper.e("SORT->", "startRecord")
        mStreamController?.start()
    }

    fun setPause() {
        mStreamController?.pause()
    }

    fun setResume() {
        mStreamController?.resume()
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        mStreamController?.stop()
    }

    override fun onSurfaceCreated(textureId: Int, eglContext: EGLContext) {
        super.onSurfaceCreated(textureId, eglContext)
        mStreamController?.prepare(context, textureId, eglContext)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
    }

    override fun onDrawFrame(mMtx: FloatArray) {
        super.onDrawFrame(mMtx)
    }


    /**
     * 录制视频处理完成的纹理
     */
    override fun onRecordTextureId(showScreenTexture: Int, surfaceTexureTimestamp: Long) {
        super.onRecordTextureId(showScreenTexture, surfaceTexureTimestamp)
        mStreamController?.onRecordTexture(showScreenTexture, surfaceTexureTimestamp)
    }
}