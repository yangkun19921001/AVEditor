package com.devyk.aveditor.widget

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGLContext
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.devyk.aveditor.callback.ICameraOpenListener
import com.devyk.aveditor.callback.OnSelectFilterListener
import com.devyk.aveditor.config.CameraConfiguration
import com.devyk.aveditor.video.AVEditorRenderer
import com.devyk.aveditor.video.camera.CameraHolder
import com.devyk.aveditor.video.filter.IFilter
import javax.microedition.khronos.opengles.GL10

import com.devyk.aveditor.entity.Speed
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.helper.AVFilterType


/**
 * <pre>
 *     author  : devyk on 2020-08-07 22:52
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVCameraView 相机处理 View
 * </pre>
 */
public open class AVCameraView : GLSurfaceView,
    AVEditorRenderer.OnRendererListener,
    SurfaceTexture.OnFrameAvailableListener {

    private var mFps = 20
    private var mPreviewWidth = 720
    private var mPreviewHeight = 1280
    private var mBack = true
    private val mSpeed: Speed? = null//模式：快速/慢速/常速




    protected var mCameraOpenListener: ICameraOpenListener? = null

    /**
     * 相机预览默认配置
     */
    private var mCameraConfiguration = CameraConfiguration.createDefault()

    protected lateinit var mRenderer: AVEditorRenderer


    /**
     * 需要渲染的纹理 ID
     */
    private var mTextureId = -1

    /**
     * EGL 上下文环境
     */
    private var mEGLContext: EGLContext? = null


    /**
     * Camera 纹理
     */
    private var mSurfaceTextureView: SurfaceTexture? = null


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    protected fun init(context: Context?) {
        //1、配置 OpenGL ES 使用版本
        setEGLContextClientVersion(2)

        //2、设置渲染器
        mRenderer = AVEditorRenderer(context)
        setRenderer(mRenderer)

        //3、设置渲染模式，有手动和自动模式，这里我们选择手动调用 requestRender 渲染
        renderMode = RENDERMODE_WHEN_DIRTY

        //4、设置渲染监听
        mRenderer.setOnRendererListener(this)
    }

    /**
     * Camera 刷新回调
     */
    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        /**
         * 由于 咱们初始化传递的是 RENDERMODE_WHEN_DIRTY 手动渲染模式，当 Camera 有新的数据，我们就应该请求刷新
         */
        requestRender()
    }

    /**
     * 渲染器创建
     * @see AVEditorRenderer.OnRendererListener
     *
     */
    override open fun onSurfaceCreated(textureId: Int, eglContext: EGLContext) {
        mEGLContext = eglContext
        mTextureId = textureId
    }

    /**
     * 设置预览视频的参数
     */
    fun setCameraConfigure(cameraConfiguration: CameraConfiguration) {
        this.mCameraConfiguration = cameraConfiguration
    }

    /**
     * 渲染器改变了,需要重启预览
     * @see AVEditorRenderer.OnRendererListener
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        startPreview()
    }

    override fun onDrawFrame(mMtx: FloatArray) {
        mSurfaceTextureView?.let { surfaceTexture ->
            surfaceTexture.updateTexImage()
            surfaceTexture.getTransformMatrix(mMtx)
        }
    }

    /**
     * 由于在 GLSurfaceView 内部没有回调可以判断该控件是否销毁了，固在当前重写  surfaceDestroyed ，将销毁生命周期传递下去
     */
    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        super.surfaceDestroyed(holder)
    }

    /**
     * 开始预览
     */
    public fun startPreview() {
        // Camera 预览配置
        mCameraConfiguration = CameraConfiguration.Builder()
            .setFacing(if (mBack) CameraConfiguration.Facing.BACK else CameraConfiguration.Facing.FRONT)
            .setFps(mFps)
            .setPreview(mPreviewHeight, mPreviewWidth)
            .build()

        CameraHolder.instance().setConfiguration(mCameraConfiguration)
        CameraHolder.instance().openCamera()
        mSurfaceTextureView = SurfaceTexture(mTextureId)
        CameraHolder.instance().setSurfaceTexture(mSurfaceTextureView!!, this);
        CameraHolder.instance().startPreview();
    }

    /**
     * 释放 Camera 资源的时候调用
     */
    public open fun stopPreview() {
        mRenderer?.onSurfaceDestroyed()
        CameraHolder.instance().stopPreview()
        CameraHolder.instance().releaseCamera()
        CameraHolder.instance().release()
    }

    override fun onRecordTextureId(showScreenTexture: Int, surfaceTexureTimestamp: Long) {
    }


    /**
     * 切换相机
     */
    @Synchronized
    public open fun switchCamera(): Boolean {
        return CameraHolder.instance().switchCamera()
    }

    /**
     * 内部包含已有的滤镜
     */
    fun setGPUImageFilter(type: AVFilterType?, listener: OnSelectFilterListener) {
        queueEvent {
            val gpuImageFilter = mRenderer?.setGPUImageFilter(type)
            listener?.onSelectFilter(gpuImageFilter)
        }
        requestRender()
    }

    /**
     * 添加 GPUImage 滤镜
     */
    @Synchronized
    fun <gpuImageFilter : GPUImageFilter> setGPUImageFilter(filter: gpuImageFilter) {
        queueEvent {
            mRenderer?.setGPUImageFilter(filter)
        }
        requestRender()
    }

    /**
     * 添加水印
     */
    public fun addWatermark(watermark: Watermark?) {
        mRenderer?.addWatermark(watermark)
    }


}