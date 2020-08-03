package com.devyk.avedit.camera

import android.graphics.SurfaceTexture
import android.view.Surface

/**
 * <pre>
 *     author  : devyk on 2020-05-29 23:25
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoPreviewScheduler
 * </pre>
 */

class VideoPreviewScheduler : VideoPreviewView.OnPreviewViewCallback, SurfaceTexture.OnFrameAvailableListener {


    private lateinit var mPreview: VideoPreviewView;


    private var isFirst = true


    constructor(preview: VideoPreviewView) {
        mPreview = preview;
    }

    /**
     * 创建 surface
     */
    override fun createSurface(surface: Surface?, width: Int, height: Int) {
        super.createSurface(surface, width, height)

        startPreview(surface, width, height)

    }

    private fun startPreview(surface: Surface?, width: Int, height: Int) {
        if (isFirst) {
            prepareEGLContext(surface, width, height)
            isFirst = false
        } else {
            createWindowSurface(surface)
        }
    }

    private external fun prepareEGLContext(surface: Surface?, width: Int, height: Int)
    private external fun createWindowSurface(surface: Surface?)
    /**
     * 当 Camera 捕获到新的一帧图像的时候会调用这个方法，因为更新纹理必须要在 EGLThread 中
     *
     */
    private external fun notifyFrameAvailable()


    /**
     * 更新窗口大小
     */
    override external fun resetRendererSize(width: Int, height: Int)

    /**
     * 销毁控件
     */
    override fun destorySurface() {
        super.destorySurface()
    }





    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        notifyFrameAvailable();
    }

    /**
     *
     * 当底层创建好 EGLContext 之后，回调回来配置 Camera ，返回 Camera 的配置信息，
     * 然后在 EGLTHread 线程中回调回来继续做 Camera 未完成的工作
     *
     */
    public fun setPreviewFormatNative(textureId: Int) {
        CameraHolder.instance().setSurfaceTexture(textureId, this);
    }

    /**
     * 当底层 EGLThread 更新纹理的时候调用这个方法
     */
    public fun updateTexImageFormNative() {
        CameraHolder.instance().updateTexImage()
    }

    /**
     * 当底层释放 Camera 资源的时候调用
     */
    public fun releaseCameraNative(){
        CameraHolder.instance().stopPreview()
        CameraHolder.instance().releaseCamera()

    }

}