package com.devyk.aveditor.video.filter

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.utils.OpenGLUtils

/**
 * <pre>
 *     author  : devyk on 2020-08-08 14:23
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseFBOFilter
 * </pre>
 */
public open class BaseFBOFilter : BaseFilter {
    //FBO id
    protected var mFrameBuffers: IntArray? = null
    //fbo 纹理id
    protected var mFrameBufferTextures: IntArray? = null

    constructor(context: Context?, vertexShaderId: Int, fragmentShaderId: Int) :
            super(context, vertexShaderId, fragmentShaderId)

    override fun onReady(width: Int, height: Int) {
        super.onReady(width, height)
        if (mFrameBuffers != null) {
            destroyFrameBuffers()
        }
        //fbo的创建 (缓存)
        //1、创建fbo （离屏屏幕）
        mFrameBuffers = IntArray(1)
        // 1、创建几个fbo 2、保存fbo id的数据 3、从这个数组的第几个开始保存
        GLES20.glGenFramebuffers(mFrameBuffers!!.size, mFrameBuffers, 0)

        //2、创建属于fbo的纹理
        mFrameBufferTextures = IntArray(1) //用来记录纹理id
        //创建纹理
        OpenGLUtils.glGenTextures(mFrameBufferTextures!!)

        //让fbo与 纹理发生关系
        //创建一个 2d的图像
        // 目标 2d纹理+等级 + 格式 +宽、高+ 格式 + 数据类型(byte) + 像素数据
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures!![0])
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mSurfaceWidth, mSurfaceHeight,
            0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
        )
        // 让fbo与纹理绑定起来 ， 后续的操作就是在操作fbo与这个纹理上了
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers!![0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D, mFrameBufferTextures!![0], 0
        )
        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    fun destroyFrameBuffers() {
        //删除fbo的纹理
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0)
            mFrameBufferTextures = null
        }
        //删除fbo
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0)
            mFrameBuffers = null
        }
    }

    override fun changeCoordinate() {
        super.changeCoordinate(floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f))
    }


    public fun getTextureId():Int= mFrameBufferTextures!![0]

    override fun release() {
        super.release()
        destroyFrameBuffers()
    }

}
