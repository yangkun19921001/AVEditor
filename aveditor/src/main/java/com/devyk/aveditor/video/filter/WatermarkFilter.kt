package com.devyk.aveditor.video.filter

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.devyk.aveditor.R
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.utils.OpenGLUtils

/**
 * <pre>
 *     author  : devyk on 2020-08-09 16:42
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is WatermarkFilter
 * </pre>
 */

class WatermarkFilter(context: Context?) : BaseFBOFilter(context, R.raw.base_vertex, R.raw.base_frag) {

    private var mBitmap: Bitmap? = null
    private var mTextureId: IntArray? = null
    private var mWatermark: Watermark? = null
    private var mWeaterX = 0
    private var mWeaterY = 0


    override fun onReady(width: Int, height: Int) {
        super.onReady(width, height)
        // opengl 纹理 id
        // 把Bitmap 存放到opengl的纹理中
        mTextureId = IntArray(1)
        OpenGLUtils.glGenTextures(mTextureId!!)
        //表示后续的操作 就是作用于这个纹理上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId!![0])
        // 将 Bitmap与纹理id 绑定起来
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }


    override fun onDrawFrame(textureId: Int): Int {
        if (null == mBitmap) {
            return textureId
        }
        //设置显示窗口
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight)

        //不调用的话就是默认的操作glsurfaceview中的纹理了。显示到屏幕上了
        //这里我们还只是把它画到fbo中(缓存)
        mFrameBuffers?.get(0)?.let { GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, it) }

        //使用着色器
        GLES20.glUseProgram(mGLProgramId)

        //传递坐标
        mGLVertexBuffer?.position(0)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer)
        GLES20.glEnableVertexAttribArray(vPosition)

        mGLTextureBuffer?.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        //因为这一层是摄像头后的第一层，所以需要使用扩展的  GL_TEXTURE_EXTERNAL_OES
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(vTexture, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        //TODO 删掉 不然录制视频抖动
        //        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        onDrawStick()
        //返回fbo的纹理id
        return mFrameBufferTextures?.get(0)!!
    }

    private fun onDrawStick() {
        //帖纸画上去
        //开启混合模式 ： 将多张图片进行混合(贴图)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        // 绘制水印
        mWatermark?.let { watermark ->
            watermark.bitmap?.let { bitmap ->
                mWeaterX = mSurfaceWidth - bitmap.width - 50
                mWeaterY = mSurfaceHeight - bitmap.height - 100

                if (watermark.x != 0.0f || watermark.y != 0.0f) {
                    mWeaterX = watermark.x.toInt()
                    mWeaterY = watermark.y.toInt()
                }

                //设置水印位置
                GLES20.glViewport(
                    mWeaterX, mWeaterY,
                    bitmap.width,
                    bitmap.height
                )
            }
        }



        mFrameBuffers?.get(0)?.let { GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, it) }
        //使用着色器
        GLES20.glUseProgram(mGLProgramId)
        //传递坐标
        mGLVertexBuffer?.position(0)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer)
        GLES20.glEnableVertexAttribArray(vPosition)

        mGLTextureBuffer?.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        //因为这一层是摄像头后的第一层，所以需要使用扩展的  GL_TEXTURE_EXTERNAL_OES
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId!![0])
        GLES20.glUniform1i(vTexture, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

        //关闭
        GLES20.glDisable(GLES20.GL_BLEND)
    }

    override fun release() {
        super.release()
        mBitmap!!.recycle()
    }

    fun setWatermark(watermark: Watermark?) {
        this.mWatermark = watermark
        watermark?.let {
            it.bitmap?.let {
                mBitmap = it
            }
        }


    }
}
