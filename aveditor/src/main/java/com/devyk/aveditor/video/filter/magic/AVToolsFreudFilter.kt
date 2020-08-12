package com.devyk.aveditor.video.filter.magic

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.R
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter


class AVToolsFreudFilter(context: Context) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, OpenGLUtils.readRawTextFile(context,
    R.raw.freud)) {
    private var mTexelHeightUniformLocation: Int = 0
    private var mTexelWidthUniformLocation: Int = 0
    private val inputTextureHandles = intArrayOf(-1)
    private val inputTextureUniformLocations = intArrayOf(-1)
    private var mGLStrengthLocation: Int = 0

    protected override fun onDestroy() {
        super.onDestroy()
        GLES20.glDeleteTextures(1, inputTextureHandles, 0)
        for (i in inputTextureHandles.indices)
            inputTextureHandles[i] = -1
    }

    protected override fun onDrawArraysAfter() {
        var i = 0
        while (i < inputTextureHandles.size && inputTextureHandles[i] != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i + 3))
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            i++
        }
    }

    protected override fun onDrawArraysPre() {
        var i = 0
        while (i < inputTextureHandles.size && inputTextureHandles[i] != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i + 3))
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i])
            GLES20.glUniform1i(inputTextureUniformLocations[i], i + 3)
            i++
        }
    }

    protected override fun onInit() {
        super.onInit()
        inputTextureUniformLocations[0] = GLES20.glGetUniformLocation(program, "inputImageTexture2")

        mTexelWidthUniformLocation = GLES20.glGetUniformLocation(program, "inputImageTextureWidth")
        mTexelHeightUniformLocation = GLES20.glGetUniformLocation(program, "inputImageTextureHeight")

        mGLStrengthLocation = GLES20.glGetUniformLocation(
            program,
            "strength"
        )
    }

    protected override fun onInitialized() {
        super.onInitialized()
        setFloat(mGLStrengthLocation, 1.0f)
        runOnDraw(Runnable {
            inputTextureHandles[0] = OpenGLUtils.loadTexture(context, "filter/freud_rand.png")
        })
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        GLES20.glUniform1f(mTexelWidthUniformLocation, width.toFloat())
        GLES20.glUniform1f(mTexelHeightUniformLocation, height.toFloat())
    }
}
