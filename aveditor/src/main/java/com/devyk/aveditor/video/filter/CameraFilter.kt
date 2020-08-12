package com.devyk.aveditor.video.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.devyk.aveditor.R

/**
 * <pre>
 *     author  : devyk on 2020-08-08 13:33
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is CameraFilter 将 Camera 预览数据写入 FBO 缓存中去，等到视频处理完在于 @see {ScreenFilter} 来预览
 *
 * </pre>
 */
public class CameraFilter : BaseFBOFilter {

    protected var mMatrix: FloatArray? = null


    constructor(context: Context?) : super(
        context,
        R.raw.camera_vertex, R.raw.camera_frag
    )

    override fun changeCoordinate() {
        super.changeCoordinate(floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f))
    }

    override fun onDrawFrame(textureId: Int): Int {
        //设置显示窗口
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight)

        //不调用的话就是默认的操作glsurfaceview中的纹理了。显示到屏幕上了
        //这里我们还只是把它画到fbo中(缓存)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers!![0])

        //使用着色器
        GLES20.glUseProgram(mGLProgramId)

        //传递坐标
        mGLVertexBuffer?.position(0)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer)
        GLES20.glEnableVertexAttribArray(vPosition)

        mGLTextureBuffer?.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        //变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        //因为这一层是摄像头后的第一层，所以需要使用扩展的  GL_TEXTURE_EXTERNAL_OES
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES20.glUniform1i(vTexture, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        //返回fbo的纹理id
        return mFrameBufferTextures!![0]
    }

    fun setMatrix(matrix: FloatArray) {
        this.mMatrix = matrix
    }
}