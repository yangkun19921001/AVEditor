package com.devyk.aveditor.video.filter.magic

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.devyk.aveditor.R
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter

import java.nio.FloatBuffer

class AVToolsCameraInputFilter(context: Context) : GPUImageFilter(context,
    OpenGLUtils.readRawTextFile(context, R.raw.default_vertex),
    OpenGLUtils.readRawTextFile(context,R.raw.default_fragment)
) {

    private var mTextureTransformMatrix: FloatArray? = null
    private var mTextureTransformMatrixLocation: Int = 0
    private var mSingleStepOffsetLocation: Int = 0
    private var mParamsLocation: Int = 0
    private var mFrameWidth = -1
    private var mFrameHeight = -1

    protected override fun onInit() {
        super.onInit()
        mTextureTransformMatrixLocation = GLES20.glGetUniformLocation(program, "textureTransform")
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(program, "singleStepOffset")
        mParamsLocation = GLES20.glGetUniformLocation(program, "params")
        configBeautyLevel(beautyLevel)
    }

    fun setTextureTransformMatrix(mtx: FloatArray) {
        mTextureTransformMatrix = mtx
    }

    override fun onDrawFrame(textureId: Int): Int {
        GLES20.glUseProgram(program)
        runPendingOnDrawTasks()
        if (!isInitialized) {
            return OpenGLUtils.NOT_INIT
        }
        mGLCubeBuffer.position(0)
        GLES20.glVertexAttribPointer(attribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer)
        GLES20.glEnableVertexAttribArray(attribPosition)
        mGLTextureBuffer.position(0)
        GLES20.glVertexAttribPointer(attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer)
        GLES20.glEnableVertexAttribArray(attribTextureCoordinate)
        GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, mTextureTransformMatrix, 0)

        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
            GLES20.glUniform1i(uniformTexture, 0)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(attribPosition)
        GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        return OpenGLUtils.ON_DRAWN
    }

    override fun onDrawFrame(textureId: Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer): Int {
        GLES20.glUseProgram(program)
        runPendingOnDrawTasks()
        if (!isInitialized) {
            return OpenGLUtils.NOT_INIT
        }
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(attribPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(attribPosition)
        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(attribTextureCoordinate)
        GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, mTextureTransformMatrix, 0)

        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
            GLES20.glUniform1i(uniformTexture, 0)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(attribPosition)
        GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        return OpenGLUtils.ON_DRAWN
    }

    fun onDrawToTexture(textureId: Int): Int {
        if (mFrameBuffers == null)
            return OpenGLUtils.NO_TEXTURE
        runPendingOnDrawTasks()
        GLES20.glViewport(0, 0, mFrameWidth, mFrameHeight)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers!![0])
        GLES20.glUseProgram(program)
        if (!isInitialized) {
            return OpenGLUtils.NOT_INIT
        }
        mGLCubeBuffer.position(0)
        GLES20.glVertexAttribPointer(attribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer)
        GLES20.glEnableVertexAttribArray(attribPosition)
        mGLTextureBuffer.position(0)
        GLES20.glVertexAttribPointer(attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer)
        GLES20.glEnableVertexAttribArray(attribTextureCoordinate)
        GLES20.glUniformMatrix4fv(mTextureTransformMatrixLocation, 1, false, mTextureTransformMatrix, 0)

        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
            GLES20.glUniform1i(uniformTexture, 0)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(attribPosition)
        GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight)
        return mFrameBufferTextures!![0]
    }

    fun initCameraFrameBuffer(width: Int, height: Int) {
        if (mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height))
            destroyFramebuffers()
        if (mFrameBuffers == null) {
            mFrameWidth = width
            mFrameHeight = height
            mFrameBuffers = IntArray(1)
            mFrameBufferTextures = IntArray(1)

            GLES20.glGenFramebuffers(1, mFrameBuffers, 0)
            GLES20.glGenTextures(1, mFrameBufferTextures, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures!![0])
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers!![0])
            GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures!![0], 0
            )
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        }
    }

    fun destroyFramebuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0)
            mFrameBufferTextures = null
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0)
            mFrameBuffers = null
        }
        mFrameWidth = -1
        mFrameHeight = -1
    }

    private fun setTexelSize(w: Float, h: Float) {
        setFloatVec2(mSingleStepOffsetLocation, floatArrayOf(2.0f / w, 2.0f / h))
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        setTexelSize(width.toFloat(), height.toFloat())
    }

    fun configBeautyLevel(level: Float) {
        setFloat(mParamsLocation, level)

        /*        when (level) {
            0 -> setFloat(mParamsLocation, 0.0f)
            1 -> setFloat(mParamsLocation, 1.0f)
            2 -> setFloat(mParamsLocation, 0.8f)
            3 -> setFloat(mParamsLocation, 0.6f)
            4 -> setFloat(mParamsLocation, 0.4f)
            5 -> setFloat(mParamsLocation, 0.33f)
            else -> {
            }
        }*/
    }

    protected override fun onDestroy() {
        super.onDestroy()
        destroyFramebuffers()
    }

    fun onBeautyLevelChanged() {
        configBeautyLevel(beautyLevel)
    }

}