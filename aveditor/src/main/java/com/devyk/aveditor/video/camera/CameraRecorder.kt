package com.devyk.aveditor.video.camera

import android.content.Context
import android.opengl.EGLContext
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import com.devyk.aveditor.entity.Speed
import com.devyk.aveditor.mediacodec.VideoEncoder
import com.devyk.aveditor.utils.EGLUtils

/**
 * <pre>
 *     author  : devyk on 2020-07-11 15:18
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is CameraRecorder 摄像头录制
 * </pre>
 */
public class CameraRecorder(context: Context, textureId: Int, eglContext: EGLContext?) : VideoEncoder() {


    protected var mEGLContext: EGLContext?
    protected var mSurface: Surface? = null
    private var mEGLUtils: EGLUtils? = null
    private var mContext: Context? = null
    private var mHandler: Handler? = null
    private var mTextureId = -1;
    private var isPause = false

    init {
        this.mEGLContext = eglContext
        mContext = context
        mTextureId = textureId
    }


    /**
     * surface 创建的时候开始进行 GL 线程渲染
     */
    override fun onSurfaceCreate(surface: Surface?) {
        mSurface = surface
        val handlerThread = HandlerThread("EGL-Thread")
        handlerThread.start()
        val looper = handlerThread.looper
        // 用于其他线程 通知子线程
        mHandler = Handler(looper)
        //子线程： EGL的绑定线程 ，对我们自己创建的EGL环境的opengl操作都在这个线程当中执行
        mHandler?.post {
            //创建我们的EGL环境 (虚拟设备、EGL上下文等)
            mEGLUtils = EGLUtils(mContext, mConfiguration.width, mConfiguration.height, surface, mEGLContext)

        }
    }


    override fun start(speed: Speed) {
        super.start(speed)

    }

    override fun stop() {
        super.stop()
        mEGLUtils?.release()
    }

    public fun pause() {
        isPause = true
    }

    public fun resume() {
        isPause = false
    }

    override fun getSurface(): Surface? {
        if (mSurface != null)
            return mSurface
        return super.getSurface()
    }

    fun onRecordTexture(showScreenTexture: Int, surfaceTexureTimestamp: Long) {
        if (isStart() && !isPause)
            mHandler?.post {
                mEGLUtils?.draw(showScreenTexture, surfaceTexureTimestamp)
                drawEncode()
            }
    }

    /**
     * 编码完成的 H264 数据
     *   00 00 00 01 06:  SEI信息
     *   00 00 00 01 67:  0x67&0x1f = 0x07 :SPS
     *   00 00 00 01 68:  0x68&0x1f = 0x08 :PPS
     *   00 00 00 01 65:  0x65&0x1f = 0x05: IDR Slice
     */
//    fun onVideoEncodes(bb: ByteBuffer?, bi: MediaCodec.BufferInfo) {
//        var h264Arrays = ByteArray(bi.size)
//        bb?.position(bi.offset)
//        bb?.limit(bi.offset + bi.size)
//        bb?.get(h264Arrays)
//        val tag = h264Arrays[4].and(0x1f).toInt()
//        if (tag == 0x07) {//sps
//            LogHelper.e(TAG, " SPS " + h264Arrays.size)
//        } else if (tag == 0x08) {//pps
//            LogHelper.e(TAG, " PPS ")
//        } else if (tag == 0x05) {//关键字帧
//            LogHelper.e(TAG, " 关键帧 " + h264Arrays.size)
//        } else {
//            //普通帧
//            LogHelper.e(TAG, " 普通帧 " + h264Arrays.size)
//        }
//    }


}