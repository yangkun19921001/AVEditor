package com.devyk.aveditor.video.renderer


import android.content.Context
import android.opengl.GLSurfaceView
import android.widget.Toast
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.entity.YUVEntity
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.utils.ThreadUtils
import com.devyk.aveditor.video.filter.*
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.helper.AVFilterFactory
import com.devyk.aveditor.video.filter.helper.AVFilterType
import com.tencent.mars.xlog.Log
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * <pre>
 *     author  : devyk on 2020-10-11 00:28
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVEditorRenderer
 * </pre>
 */

public class AVEditorRenderer(context: Context) : GLSurfaceView.Renderer {


    private var mContext: Context? = null
    /**
     * 默认 YUV 渲染
     */
    private var mYUVRenderer: YUVFilter? = null

    /**
     * 渲染到屏幕中
     */
    private var mScreenFilter: ScreenFilter? = null


    /**
     * YUV data 数据队列
     */
    private var mLinkedList = LinkedList<YUVEntity>()

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

    /**
     * 是否绘制 GPUImage
     */
    var isGpuimageDraw = false

    init {
        mContext = context;
    }


    /**
     * 显示控件的宽高
     */
    private var mSurfaceWidth = -1
    private var mSurfaceHeight = -1


    /**
     * 设置 YUV 过滤器
     */
    fun <T : BaseFBOFilter> setYuvFilter(t: T) {
        mYUVRenderer = t as YUVFilter;
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        initFilter()
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.mSurfaceWidth = width
        this.mSurfaceHeight = height
        onFilterReady(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        onFilterDraw()
    }

    /**
     * 初始化过滤器
     */
    private fun initFilter() {
        mFilters.clear()
        mAddFilters.clear()

        //1、添加 YUV 预览过滤
        mFilters.add(YUVFilter(mContext))


        //默认添加水印
        watermark?.let { watermark ->
            checkNotNull(watermark.bitmap)
            val watermarkFilter = WatermarkFilter(mContext)
            watermarkFilter.setWatermark(watermark)
            mFilters.add(watermarkFilter)
        }


        //2、添加外部的过滤器
        if (mAddFilters.size > 0)
            mFilters.addAll(mAddFilters)


        //3、最后显示
        mFilters.add(ScreenFilter(mContext))

        //4、清理外部添加的过滤器，避免重复添加
        mAddFilters.clear()
    }

    /**
     * 过滤器准备工作
     */
    private fun onFilterReady(width: Int, height: Int) {
        if (mFilters.size > 0)
            for (filter in mFilters) {
                filter.onReady(width, height)

            }
    }

    /**
     * 绘制过滤器
     */
    private fun onFilterDraw() {
        val yuvEntity = poll()
        var fboTextureId = -1;
        yuvEntity?.let {
            if (yuvEntity.width != 0 && yuvEntity.height != 0) {
                //绘制过滤器，用于视频美化或者其它处理
                if (mFilters.size > 0) {
                    try {
                        fboTextureId = (mFilters.get(mDrawFilterIndex) as YUVFilter)?.onDrawFrame(
                            yuvEntity.width,
                            yuvEntity.height,
                            yuvEntity.y,
                            yuvEntity.u,
                            yuvEntity.v
                        )!!
                        //这里的纹理需要进行录制
                        var showScreenTexture = onDrawFrameFilter(fboTextureId)
                    } catch (err: Exception) {
                        LogHelper.e(Log.TAG, err?.message)
                    }
                }

            }
        }
    }


    /**
     * 绘制过滤器
     */
    private fun onDrawFrameFilter(textureId: Int): Int {
        if (mDrawFilterIndex >= mFilters.size - 1) {
            mDrawFilterIndex = 0
            isGpuimageDraw = false
            return textureId
        }
        val filter = mFilters.get(++mDrawFilterIndex)

        var gpuTextID = textureId

        LogHelper.d("onDrawFrameFilter", "in")

        //TODO-----这里有个 bug 如果设置了水印，那么必须在水印过滤器之前设置滤镜，在屏幕渲染之前就会有问题，先暂时这样限制
        //适配 GPUImageFilter,在水印之后
        if (filter is WatermarkFilter && mGPUImageFilter != null) {
            gpuTextID = mGPUImageFilter?.onDrawFrame(gpuTextID)!!
            isGpuimageDraw = true
            Log.d("onDrawFrameFilter", "WatermarkFilter")
        }

        if (filter is ScreenFilter && mGPUImageFilter != null && !isGpuimageDraw) {
            gpuTextID = mGPUImageFilter?.onDrawFrame(gpuTextID)!!
            LogHelper.d("onDrawFrameFilter", "ScreenFilter")
        }
        LogHelper.d("onDrawFrameFilter", "out")
        return onDrawFrameFilter(filter?.onDrawFrame(gpuTextID))

    }

    fun setYUVData(width: Int, height: Int, y: ByteArray, u: ByteArray, v: ByteArray) {
        offer(YUVEntity(width, height, y, u, v))
    }


    /**
     * 添加水印
     */
    fun addWatermark(watermark: Watermark?) {
        this.watermark = watermark
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

    private fun onReadyGPUImageFilter(gpuimage: GPUImageFilter?) {
        gpuimage?.let { gpuimage ->
            gpuimage.init()
            gpuimage.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight)
            gpuimage.onInputSizeChanged(mSurfaceWidth, mSurfaceHeight)
        }

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
     * 加入队列
     *
     * @param data
     */
    fun offer(data: YUVEntity) {

        synchronized(this) {
            mLinkedList.offer(data)
        }

    }

    /**
     * 从队列取出
     *
     * @return
     */
    fun poll(): YUVEntity? {
        synchronized(this) {
            //            return mLinkedList.poll()
            return mLinkedList.remove()
//            return null
        }
    }

    /**
     * 当 surface销毁的时候回调
     */
    fun onSurfaceDestroyed() {
        for (filter in mFilters)
            filter?.release()
        mFilters.clear()
        mAddFilters.clear()
    }
}