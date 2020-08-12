package com.devyk.aveditor.widget

import android.content.Context
import android.opengl.EGLContext
import android.util.AttributeSet
import com.devyk.aveditor.config.AudioConfiguration
import com.devyk.aveditor.config.VideoConfiguration
import com.devyk.aveditor.controller.StreamController
import com.devyk.aveditor.stream.packer.Packer
import com.devyk.aveditor.video.filter.IFilter
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
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
public class AVRecordView<T : IFilter,G : GPUImageFilter> : AVCameraView<T,G> {


    private var mVideoConfiguration = VideoConfiguration.createDefault()
    private var mAudioConfiguration = AudioConfiguration.createDefault()

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
    fun setPaker(packer: Packer){
        mStreamController?.setPacker(packer)
    }

    /**
     * 开始录制
     */
    fun startRecord() {
        mStreamController?.start()
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        mStreamController?.stop()
    }

    override fun onSurfaceCreated(textureId: Int, eglContext: EGLContext) {
        super.onSurfaceCreated(textureId, eglContext)
        mStreamController?.prepare(context,textureId,eglContext)
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
        super.onRecordTextureId(showScreenTexture, getSurfaceTexureTimestamp())
        mStreamController?.onRecordTexture(showScreenTexture,surfaceTexureTimestamp)

    }
}