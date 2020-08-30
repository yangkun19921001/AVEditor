package com.devyk.aveditor.controller

import android.content.Context
import android.media.MediaCodec
import android.media.MediaFormat
import android.opengl.EGLContext
import com.devyk.aveditor.callback.IController
import com.devyk.aveditor.callback.OnVideoEncodeListener
import com.devyk.aveditor.config.VideoConfiguration
import com.devyk.aveditor.entity.Speed
import com.devyk.aveditor.video.camera.CameraRecorder

import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-07-15 22:07
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoController Camera 预览和 H264 编码的控制
 * </pre>
 */

public class VideoController(context: Context, textureId:Int, eglContext: EGLContext?, videoConfiguration: VideoConfiguration) : IController,
    OnVideoEncodeListener {

    private var mCameraVideoController: CameraRecorder? = null

    private var mListener : IController.OnVideoDataListener?=null

    init {
        mCameraVideoController = CameraRecorder(context, textureId, eglContext)
        mCameraVideoController?.prepare(videoConfiguration)
        mCameraVideoController?.setOnVideoEncodeListener(this)
    }
    override fun start(speed: Speed) {
        mCameraVideoController?.start()
    }

    override fun stop() {
        mCameraVideoController?.stop()
    }

    override fun pause() {
        mCameraVideoController?.pause()
    }

    override fun resume() {
        mCameraVideoController?.resume()
    }


    override fun onVideoEncode(bb: ByteBuffer?, bi: MediaCodec.BufferInfo?) {
        mListener?.onVideoData(bb,bi)
    }

    override fun onVideoOutformat(outputFormat: MediaFormat?) {
        mListener?.onVideoOutformat(outputFormat)
    }


    override fun setVideoBps(bps: Int) {
        mCameraVideoController?.setEncodeBps(bps)
    }

    override fun setVideoDataListener(videoDataListener: IController.OnVideoDataListener) {
        mListener = videoDataListener
    }

    fun onRecordTexture(showScreenTexture: Int, surfaceTexureTimestamp: Long) {
        mCameraVideoController?.onRecordTexture(showScreenTexture,surfaceTexureTimestamp)
    }


}
