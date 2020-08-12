package com.devyk.aveditor.video.filter.magic


import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter


import java.nio.FloatBuffer


open class AVToolsBaseGroupFilter(context: Context,public var filters: List<GPUImageFilter>) : GPUImageFilter(context) {
    private var frameWidth = -1
    private var frameHeight = -1

    val size: Int
        get() = filters.size

    override fun onDestroy() {
        for (filter in filters) {
            filter.destroy()
        }
        destroyFramebuffers()
    }

    override fun init() {
        for (filter in filters) {
            filter.init()
        }
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        val size = filters.size
        for (i in 0 until size) {
            filters[i].onInputSizeChanged(width, height)
        }
        if (frameBuffers != null && (frameWidth != width || frameHeight != height || frameBuffers!!.size != size - 1)) {
            destroyFramebuffers()
            frameWidth = width
            frameHeight = height
        }
        if (frameBuffers == null) {
            frameBuffers = IntArray(size - 1)
            frameBufferTextures = IntArray(size - 1)

            for (i in 0 until size - 1) {
                GLES20.glGenFramebuffers(1, frameBuffers, i)

                GLES20.glGenTextures(1, frameBufferTextures, i)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTextures!![i])
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

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers!![i])
                GLES20.glFramebufferTexture2D(
                    GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, frameBufferTextures!![i], 0
                )

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            }
        }
    }

    override fun onDrawFrame(
        textureId: Int, cubeBuffer: FloatBuffer,
        textureBuffer: FloatBuffer
    ): Int {
        if (frameBuffers == null || frameBufferTextures == null) {
            return OpenGLUtils.NOT_INIT
        }
        val size = filters.size
        var previousTexture = textureId
        for (i in 0 until size) {
            val filter = filters[i]
            val isNotLast = i < size - 1
            if (isNotLast) {
                GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight)
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers!![i])
                GLES20.glClearColor(0f, 0f, 0f, 0f)
                filter.onDrawFrame(previousTexture, mGLCubeBuffer, mGLTextureBuffer)
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                previousTexture = frameBufferTextures!![i]
            } else {
                GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight)
                filter.onDrawFrame(previousTexture, cubeBuffer, textureBuffer)
            }
        }
        return frameBufferTextures!![0]
    }

    override fun onDrawFrame(textureId: Int): Int {
        if (frameBuffers == null || frameBufferTextures == null) {
            return OpenGLUtils.NOT_INIT
        }
        val size = filters.size
        var previousTexture = textureId
        for (i in 0 until size) {
            val filter = filters[i]
            val isNotLast = i < size - 1
            if (isNotLast) {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers!![i])
                GLES20.glClearColor(0f, 0f, 0f, 0f)
                filter.onDrawFrame(previousTexture, mGLCubeBuffer, mGLTextureBuffer)
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                previousTexture = frameBufferTextures!![i]
            } else {
                filter.onDrawFrame(previousTexture, mGLCubeBuffer, mGLTextureBuffer)
            }
        }
        return frameBufferTextures!![0]
    }

    private fun destroyFramebuffers() {
        if (frameBufferTextures != null) {
            GLES20.glDeleteTextures(frameBufferTextures!!.size, frameBufferTextures, 0)
            frameBufferTextures = null
        }
        if (frameBuffers != null) {
            GLES20.glDeleteFramebuffers(frameBuffers!!.size, frameBuffers, 0)
            frameBuffers = null
        }
    }

    companion object {


        protected var frameBuffers: IntArray? = null
        protected var frameBufferTextures: IntArray? = null
    }
}
