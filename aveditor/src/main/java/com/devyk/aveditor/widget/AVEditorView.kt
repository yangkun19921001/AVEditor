package com.devyk.aveditor.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.devyk.aveditor.jni.IPlayer
import com.devyk.aveditor.jni.JNIManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * <pre>
 *     author  : devyk on 2020-05-21 16:51
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVPlayView
 * </pre>
 */

class AVEditorView : GLSurfaceView, SurfaceHolder.Callback, GLSurfaceView.Renderer, Runnable {


    private var isQueryPos = true;

    private var lister: OnProgressListener? = null;

    private var mIPlayer: IPlayer? = null

    private var isExit = false

    private var mLock = java.lang.Object()

    /**
     * 上一次获取的进度
     */
    var preTime = -1


    constructor(context: Context?) : this(context, null) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setRenderer(this)
        isExit = false
        mIPlayer = JNIManager.getAVPlayEngine()
        Thread(this).start()
    }


    override fun surfaceCreated(holder: SurfaceHolder?) {
        initSurface(holder!!.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, w: Int, h: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isExit = true
    }

    override fun onDrawFrame(gl: GL10?) {
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    }
//    override fun onClick(v: View?) {
//        isPause = !isPause
//        setPause(isPause)
//
//    }

    override fun run() {
        while (true) {
            if (isExit) {
                return
            }
            var progress = progress().toInt()
            if (progress != preTime)
                lister?.onProgressChanged(progress)
            Thread.sleep(40)
            preTime = progress;
        }
    }

    /**
     * init 初始化
     */
    private fun initSurface(surface: Any) = mIPlayer?.initSurface(surface)

    /**
     * 设置播放源
     */
    public fun setDataSource(source: String?) = mIPlayer?.setDataSource(source)

    /**
     * 播放
     */
    public fun start() {
        preTime - 1
        mIPlayer?.start()
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

    /**
     * 设置硬件解码播放
     */
    public fun setMediaCodec(isMediaCodec: Boolean) =mIPlayer?.setMediaCodec(isMediaCodec)

    /**
     * 停止
     */
    public fun stop() {
        mIPlayer?.stop()
    }

    /**
     * 播放进度监听
     */
    public fun addProgressListener(progress: OnProgressListener) {
        lister = progress;
    }

    public interface OnProgressListener {
        fun onProgressChanged(progress: Int)
    }

}