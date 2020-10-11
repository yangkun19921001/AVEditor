package com.devyk.aveditor.video.renderer

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.R
import com.devyk.aveditor.video.filter.BaseFBOFilter
import com.tencent.mars.xlog.Log
import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-10-11 00:40
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is YUVRenderer
 * </pre>
 */

class YUVRenderer(context: Context) : BaseFBOFilter(context, R.raw.yuv_vertex_shader, R.raw.yuv_fragment_shader) {

    //顶点位置
    private val avPosition: Int
    //纹理位置
    private val afPosition: Int

    //shader  yuv变量
    private val sampler_y: Int
    private val sampler_u: Int
    private val sampler_v: Int
    private val textureId_yuv: IntArray

    //YUV数据
    private var width_yuv: Int = 0
    private var height_yuv: Int = 0
    private var y: ByteBuffer? = null
    private var u: ByteBuffer? = null
    private var v: ByteBuffer? = null

    val DEFAULT_TEXTURE_ID = -1

    //每一次取的总的点 大小
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val vertexCount = 4


    internal var yLen = 0
    internal var uLen = 0
    internal var vLen = 0

    init {
        //获取顶点坐标字段
        avPosition = GLES20.glGetAttribLocation(mGLProgramId, "av_Position")
        //获取纹理坐标字段
        afPosition = GLES20.glGetAttribLocation(mGLProgramId, "af_Position")
        //获取yuv字段
        sampler_y = GLES20.glGetUniformLocation(mGLProgramId, "sampler_y")
        sampler_u = GLES20.glGetUniformLocation(mGLProgramId, "sampler_u")
        sampler_v = GLES20.glGetUniformLocation(mGLProgramId, "sampler_v")

        textureId_yuv = IntArray(3)
        //创建3个纹理
        GLES20.glGenTextures(3, textureId_yuv, 0)

        //绑定纹理
        for (id in textureId_yuv) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
            //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            //解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }


    fun setYuv420p(width: Int, height: Int, yuv420p: ByteArray) {
        yLen = width * height
        uLen = yLen / 4
        vLen = yLen / 4
        this.width_yuv = width
        this.height_yuv = height
        try {
            this.y = ByteBuffer.wrap(getY(yuv420p))
            this.u = ByteBuffer.wrap(getU(yuv420p))
            this.v = ByteBuffer.wrap(getV(yuv420p))
        } catch (e: Exception) {

        }
    }

    fun setYuv420p(width: Int, height: Int, y: ByteArray, u: ByteArray, v: ByteArray) {
        this.width_yuv = width
        this.height_yuv = height
        try {
            this.y = ByteBuffer.wrap(y)
            this.u = ByteBuffer.wrap(u)
            this.v = ByteBuffer.wrap(v)
        } catch (e: Exception) {
            Log.e("setYuv420p", e.message)
        }
    }

    private fun getY(yuv420p: ByteArray): ByteArray {
        val y = ByteArray(yLen)
        System.arraycopy(yuv420p, 0, y, 0, yLen)
        return y
    }

    private fun getU(yuv420p: ByteArray): ByteArray {
        val u = ByteArray(uLen)
        System.arraycopy(yuv420p, yLen, u, 0, uLen)
        return u
    }

    private fun getV(yuv420p: ByteArray): ByteArray {
        val v = ByteArray(vLen)
        System.arraycopy(yuv420p, yLen + uLen, v, 0, vLen)
        return v
    }

    override fun onReady(width: Int, height: Int) {
        super.onReady(width, height)
    }


    fun onDrawFrame(width: Int, height: Int, y: ByteArray, u: ByteArray, v: ByteArray){
        this.width_yuv = width
        this.height_yuv = height
        try {
            this.y = ByteBuffer.wrap(y)
            this.u = ByteBuffer.wrap(u)
            this.v = ByteBuffer.wrap(v)
        } catch (e: Exception) {
            Log.e("setYuv420p", e.message)
        }


        onDrawFrame(DEFAULT_TEXTURE_ID);
    }

    override fun onDrawFrame(textureId: Int): Int {

        if (y == null || u == null || v == null) return textureId_yuv[0]

        //设置显示窗口
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight)

        //不调用的话就是默认的操作glsurfaceview中的纹理了。显示到屏幕上了
        //这里我们还只是把它画到fbo中(缓存)
        mFrameBuffers?.get(0)?.let { GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, it) }

        //传递坐标
        mGLVertexBuffer?.position(0)
        GLES20.glUseProgram(mGLProgramId)
        GLES20.glEnableVertexAttribArray(avPosition)
        GLES20.glVertexAttribPointer(
            avPosition,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            mGLVertexBuffer
        )

        mGLTextureBuffer?.position(0)
        GLES20.glEnableVertexAttribArray(afPosition)
        GLES20.glVertexAttribPointer(
            afPosition,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            mGLTextureBuffer
        )

        //激活纹理0来绑定y数据
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[0])
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_LUMINANCE,
            width_yuv,
            height_yuv,
            0,
            GLES20.GL_LUMINANCE,
            GLES20.GL_UNSIGNED_BYTE,
            y
        )

        //激活纹理1来绑定u数据
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[1])
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_LUMINANCE,
            width_yuv / 2,
            height_yuv / 2,
            0,
            GLES20.GL_LUMINANCE,
            GLES20.GL_UNSIGNED_BYTE,
            u
        )

        //激活纹理2来绑定u数据
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[2])
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_LUMINANCE,
            width_yuv / 2,
            height_yuv / 2,
            0,
            GLES20.GL_LUMINANCE,
            GLES20.GL_UNSIGNED_BYTE,
            v
        )

        //给fragment_shader里面yuv变量设置值   0 1 2 标识纹理x
        GLES20.glUniform1i(sampler_y, 0)
        GLES20.glUniform1i(sampler_u, 1)
        GLES20.glUniform1i(sampler_v, 2)

        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount)

        y!!.clear()
        u!!.clear()
        v!!.clear()
        y = null
        u = null
        v = null
        GLES20.glDisableVertexAttribArray(afPosition)
        GLES20.glDisableVertexAttribArray(avPosition)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)


        return mFrameBufferTextures?.get(0)!!
    }

    companion object {
        //每一次取点的时候取几个点
        internal val COORDS_PER_VERTEX = 2
    }
}
