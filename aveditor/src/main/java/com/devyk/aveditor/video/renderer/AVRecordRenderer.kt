package com.devyk.aveditor.video

import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLContext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.video.filter.*
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.helper.AVFilterFactory
import com.devyk.aveditor.video.filter.helper.AVFilterType

import com.tencent.mars.xlog.Log.TAG
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * <pre>
 *     author  : devyk on 2020-08-07 23:01
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVRecordRenderer 负责视频处理相关的渲染
 * </pre>
 */
public class AVRecordRenderer(context: Context?) :
    GLSurfaceView.Renderer {

    /**
     * 通过 OpenGL 创建的纹理 ID
     */
    private var mTextureId = IntArray(1)

    /**
     * 显示控件的宽高
     */
    private var mSurfaceWidth = -1
    private var mSurfaceHeight = -1

    /**
     * 矩阵
     */
    private val mMtx = FloatArray(16)

    /**
     * 渲染监听
     */
    protected var mListener: OnRendererListener? = null


    /**
     * 过滤器集合
     */
    private var mFilters = arrayListOf<IFilter>()

    /**
     * 外部添加的
     */
    private var mAddFilters = arrayListOf<IFilter>()

    /**
     * 外部添加第三方的
     */
    protected var mGPUImageFilter: GPUImageFilter? = null

    /**
     * 添加水印
     */
    private var watermark: Watermark? = null

    /**
     * 绘制所有的过滤器索引，链式调用
     */
    private var mDrawFilterIndex = 0;


    private var mContext: Context? = null


    init {
        mContext = context

    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        mFilters.clear()
        mAddFilters.clear()

        //1、通过 Opengl 接口生成一个纹理 ID
        GLES20.glGenTextures(mTextureId.size, mTextureId, 0)


        //2 初始化过滤器实例
        mFilters.add(CameraFilter(mContext))

        //默认添加水印
        watermark?.let { watermark ->
            checkNotNull(watermark.bitmap)
            val watermarkFilter = WatermarkFilter(mContext)
            watermarkFilter.setWatermark(watermark)
            mFilters.add(watermarkFilter)
        }

//        mFilters.add(BeautyFilter(mContext) as T)

        //3、添加外部过滤器
        if (mAddFilters.size > 0)
            mFilters.addAll(mAddFilters)


        //4、最后显示
        mFilters.add(ScreenFilter(mContext))
        //渲染线程的EGL上下文
        val eglContext = EGL14.eglGetCurrentContext()


        //2、创建好的纹理 ID 暴露出去,将生成的纹理ID 交于 SurfaceTexture
        mListener?.onSurfaceCreated(mTextureId[0], eglContext)

        mAddFilters.clear()

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.mSurfaceWidth = width
        this.mSurfaceHeight = height
        //1、当改变窗口的时候开始预览
        mListener?.onSurfaceChanged(gl, width, height)
        //2、准备过滤器，用于视频美化或者其它处理
        onReadyFilter()


    }

    private fun onReadyGPUImageFilter(gpuimage: GPUImageFilter?) {
        gpuimage?.let { gpuimage ->
            gpuimage.init()
            gpuimage.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight)
            gpuimage.onInputSizeChanged(mSurfaceWidth, mSurfaceHeight)
        }

    }

    /**
     * 所有过滤器重新准备
     */
    private fun onReadyFilter() {
        for (filter in mFilters) {
            filter.onReady(mSurfaceWidth, mSurfaceHeight)
        }
    }


    /**
     * 当手动调用 requestRenderer 回调该绘制
     */


    override fun onDrawFrame(gl: GL10?) {


        //清理屏幕 :告诉opengl 需要把屏幕清理成什么颜色
        if (isStart) {
            var pts = getCurFramePts()
//            onDraw(pts)
//            frameIndex++
//             pts = getCurFramePts()
            onDraw(System.nanoTime())
//            frameIndex++
        } else
            onDraw(System.nanoTime())
    }

    private fun onDraw(nanoTime: Long) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        //执行上一个：glClearColor配置的屏幕颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        //回调给 Camera 纹理交于处理显示矩阵
        mListener?.onDrawFrame(mMtx)

        //绘制过滤器，用于视频美化或者其它处理
        if (mFilters.size > 0) {
            try {
                if (mFilters.get(mDrawFilterIndex) is CameraFilter)
                    (mFilters.get(mDrawFilterIndex) as CameraFilter).setMatrix(mMtx)

                //这里的纹理需要进行录制
                var showScreenTexture = onDrawFrameFilter(mFilters.get(mDrawFilterIndex)?.onDrawFrame(mTextureId[0]))


                mListener?.onRecordTextureId(showScreenTexture, nanoTime)


            } catch (err: Exception) {
                LogHelper.e(TAG, err?.message)
            }
        }

        frameIndex++
    }


    /**
     * 绘制过滤器
     */
    private fun onDrawFrameFilter(textureId: Int): Int {
        if (mDrawFilterIndex >= mFilters.size - 1) {
            mDrawFilterIndex = 0
            return textureId
        }
        val filter = mFilters.get(++mDrawFilterIndex)

        var gpuTextID = textureId


        //适配 GPUImageFilter,在水印之后
        if (filter is WatermarkFilter && mGPUImageFilter != null)
            gpuTextID = mGPUImageFilter?.onDrawFrame(gpuTextID, filter.getVData()!!, filter.getFData()!!)!!

        return onDrawFrameFilter(filter?.onDrawFrame(gpuTextID))

    }

    /**
     *
     */

    /**
     * 当 surface销毁的时候回调
     */
    fun onSurfaceDestroyed() {
        for (filter in mFilters)
            filter?.release()
        mFilters.clear()
        mAddFilters.clear()
    }


    /**
     * 添加过滤器
     * 需要使用 FBO 需要继承 BaseFBOFilter,不需要的直接继承 BaseFilter
     */
    @Synchronized
    fun <T : IFilter> addFilter(filter: T) {
        mAddFilters.add(filter)
    }

    /**
     * 添加过滤器
     */
    @Synchronized
    fun <T : IFilter> addFilter(filters: ArrayList<T>) {
        mAddFilters.addAll(filters)
    }

    /**
     * 内部包含已有的滤镜
     */
    fun setGPUImageFilter(type: AVFilterType?): GPUImageFilter? {
        gpuDestory()
        mGPUImageFilter = mContext?.let { context -> AVFilterFactory.getFilters(context, type) }
        //3、初始化 GPUImageFilter
        onReadyGPUImageFilter(mGPUImageFilter)
        mGPUImageFilter?.let {
            return mGPUImageFilter as GPUImageFilter
        }

        return null
    }


    private fun gpuDestory() {
        mGPUImageFilter?.destroy()
        mGPUImageFilter = null
    }

    /**
     * 添加 GPUImage 滤镜
     */
    @Synchronized
    fun <gpuImageFilter : GPUImageFilter> setGPUImageFilter(filter: gpuImageFilter) {
        gpuDestory()
        mGPUImageFilter = filter
        //3、初始化 GPUImageFilter
        onReadyGPUImageFilter(mGPUImageFilter)
    }

    /**
     * 添加水印
     */
    fun addWatermark(watermark: Watermark?) {
        this.watermark = watermark
    }


    public interface OnRendererListener {
        fun onSurfaceCreated(textureId: Int, eglContext: EGLContext)
        fun onSurfaceChanged(gl: GL10?, width: Int, height: Int)
        fun onDrawFrame(mMtx: FloatArray)
        fun onRecordTextureId(showScreenTexture: Int, surfaceTexureTimestamp: Long)
    }

    /**
     * 设置渲染监听
     */
    public fun setOnRendererListener(listener: OnRendererListener) {
        mListener = listener
    }


    var isStart = false;
    fun startRecord() {
        frameIndex = 0
        isStart = true
        pts = -1
    }

    fun stopRecord() {
        frameIndex = 0
        isStart = false
        pts = -1
    }

    var frameIndex = 0L;
//    private fun getCurFramePts(): Long {
//        var pts = frameIndex * (1000 / 25) * 1000
//        return pts.toLong()
//    }


    var pts = -1L;
    private fun getCurFramePts(): Long {
        if (pts == -1L)
            pts = System.nanoTime()
//        return ((System.nanoTime() - pts) / 0.5).toLong()
        return ((System.nanoTime() - pts)).toLong()
    }


}