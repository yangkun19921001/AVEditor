package com.devyk.aveditor.video.filter.magic

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.R
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter


class AVToolsEarlyBirdFilter(context: Context) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, OpenGLUtils.readRawTextFile(context,
    R.raw.earlybird)) {
    private val inputTextureHandles = intArrayOf(-1, -1, -1, -1, -1)
    private val inputTextureUniformLocations = intArrayOf(-1, -1, -1, -1, -1)
    protected var mGLStrengthLocation: Int = 0

    protected override fun onDestroy() {
        super.onDestroy()
        GLES20.glDeleteTextures(inputTextureHandles.size, inputTextureHandles, 0)
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
        for (i in inputTextureUniformLocations.indices)
            inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(program, "inputImageTexture" + (2 + i))
        mGLStrengthLocation = GLES20.glGetUniformLocation(
           program,
            "strength"
        )
    }

    protected override fun onInitialized() {
        super.onInitialized()
        setFloat(mGLStrengthLocation, 1.0f)
        runOnDraw(Runnable {
            inputTextureHandles[0] = OpenGLUtils.loadTexture(context, "filter/earlybirdcurves.png")
            inputTextureHandles[1] = OpenGLUtils.loadTexture(context, "filter/earlybirdoverlaymap_new.png")
            inputTextureHandles[2] = OpenGLUtils.loadTexture(context, "filter/vignettemap_new.png")
            inputTextureHandles[3] = OpenGLUtils.loadTexture(context, "filter/earlybirdblowout.png")
            inputTextureHandles[4] = OpenGLUtils.loadTexture(context, "filter/earlybirdmap.png")
        })
    }
}
