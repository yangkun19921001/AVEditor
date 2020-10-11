package com.devyk.aveditor.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.devyk.aveditor.callback.IYUVDataListener
import com.devyk.aveditor.callback.OnSelectFilterListener
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.jni.IPlayer
import com.devyk.aveditor.jni.JNIManager
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.helper.AVFilterType
import com.devyk.aveditor.video.renderer.AVEditorRenderer
import com.devyk.aveditor.video.renderer.AVRecordRenderer


/**
 * <pre>
 *     author  : devyk on 2020-05-21 16:51
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVEditorView 负责视频编辑的控件,在 Native 端进行软解码，没有传入 Surface 所以没有在 Native 渲染，
 *                转为 Java 端处理渲染，加滤镜编辑等工作
 * </pre>
 */
class AVEditorView : GLSurfaceView, IYUVDataListener{
    private var mEditorRenderer: AVEditorRenderer? = null
    /**
     * 播放器
     */
    private var mIPlayer: IPlayer? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setEGLContextClientVersion(2)
        mEditorRenderer = AVEditorRenderer(getContext())
        setRenderer(mEditorRenderer)
        //主动刷新模式
//        renderMode = RENDERMODE_CONTINUOUSLY
        renderMode = RENDERMODE_WHEN_DIRTY
        mIPlayer = JNIManager.getAVPlayEngine()
        mIPlayer?.setYUVDataCallback(this)
    }

    /**
     * 设置编辑源
     */
    public fun setEditSource(source: String?) = mIPlayer?.setDataSource(source)


    /**
     * 设置是否在 native 端渲染
     */
    public fun setNativeRender(isNativeRender: Boolean) = mIPlayer?.setNativeRender(isNativeRender)

    /**
     * 播放
     */
    public fun start() {
        mIPlayer?.setNativeRender(false)
        mIPlayer?.start()
    }

    /**
     * 停止
     */
    public fun stop() {
        mIPlayer?.stop()
    }

    /**
     * 播放进度
     */
    public fun progress(): Double = mIPlayer?.progress()!!

    /**
     * 暂停
     */
    public fun setPause(status: Boolean) = mIPlayer?.setPause(status)

    /**
     * 指定跳转到某个时间点播放
     */
    public fun seekTo(seek: Double): Int? = mIPlayer?.seekTo(seek)

    override fun onYUV420pData(width: Int, height: Int, y: ByteArray, u: ByteArray, v: ByteArray) {
        mEditorRenderer?.setYUVData(width, height, y, u, v)
        requestRender()
    }


    /**
     * 内部包含已有的滤镜
     */
    fun setGPUImageFilter(type: AVFilterType?, listener: OnSelectFilterListener) {
        queueEvent {
            val gpuImageFilter = mEditorRenderer?.setGPUImageFilter(type)
            listener?.onSelectFilter(gpuImageFilter)
        }
//        requestRender()
    }

    /**
     * 添加 GPUImage 滤镜
     */
    @Synchronized
    fun <gpuImageFilter : GPUImageFilter> setGPUImageFilter(filter: gpuImageFilter) {
        queueEvent {
            mEditorRenderer?.setGPUImageFilter(filter)
        }
//        requestRender()
    }

    /**
     * 添加水印
     */
    public fun addWatermark(watermark: Watermark?) {
        mEditorRenderer?.addWatermark(watermark)
    }


}