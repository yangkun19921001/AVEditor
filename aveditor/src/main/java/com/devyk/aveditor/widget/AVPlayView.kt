package com.devyk.aveditor.widget

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder
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

class AVPlayView : GLSurfaceView, SurfaceHolder.Callback, GLSurfaceView.Renderer, Runnable {


    private var isQueryPos = true;

    private var lister: OnProgressListener? = null;

    companion object {
        init {
            System.loadLibrary("aveditor");
        }
    }

    constructor(context: Context?) : this(context, null) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setRenderer(this)
//        setOnClickListener(this)
        Thread(this).start()

    }


    override fun surfaceCreated(holder: SurfaceHolder?) {
        initView(holder!!.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, w: Int, h: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isQueryPos = false;
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
        while (isQueryPos) {

            lister?.onProgressChanged((progress()).toInt());
            if (progress() >= 100)
                break;
            Thread.sleep(40)
        }
    }

    /**
     * init 初始化
     */
    private external fun initView(surface: Any)

    /**
     * 设置播放源
     */
    public external fun setDataSource(source: String)

    /**
     * 播放
     */
    public external fun start()

    /**
     * 播放
     */
    public external fun progress(): Double

    /**
     * 暂停
     */
    public external fun setPause(status: Boolean)

    /**
     * 指定跳转到某个时间点播放
     */
    public external fun seekTo(seek: Double): Int;

    /**
     * 停止
     */
    public external fun stop()



    public fun addProgressListener(progress: OnProgressListener) {
        lister = progress;
    }

    public interface OnProgressListener {
        fun onProgressChanged(progress: Int)
    }

}