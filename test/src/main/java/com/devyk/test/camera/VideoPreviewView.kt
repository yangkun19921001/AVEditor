package com.devyk.avedit.camera

import android.content.Context
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * <pre>
 *     author  : devyk on 2020-05-29 23:04
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoPreviewView 视频预览控件
 * </pre>
 */
public class VideoPreviewView : SurfaceView, SurfaceHolder.Callback {
    private var TAG = this.javaClass.simpleName

    private lateinit var mCallback: OnPreviewViewCallback


    constructor(context: Context?) : this(context, null) {

    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0) {

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mCallback?.resetRendererSize(width,height);

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mCallback?.destorySurface();

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mCallback?.createSurface(holder?.surface,width,height);
    }


    public fun addPreviewCallBack(callback:OnPreviewViewCallback) {
        mCallback = callback
    }



    public interface OnPreviewViewCallback{
        fun createSurface(surface: Surface?, width: Int, height: Int){}
        fun resetRendererSize(width: Int, height: Int){}
        fun destorySurface() {


        }

    }
}