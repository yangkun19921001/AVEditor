package com.devyk.aveditor.video.filter.magic

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.R
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter


class AVToolsSketchFilter(context: Context) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, OpenGLUtils.readRawTextFile(context,
    R.raw.sketch)) {

    private var mSingleStepOffsetLocation: Int = 0
    //0.0 - 1.0
    private var mStrengthLocation: Int = 0


    protected override fun onInit() {
        super.onInit()
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(program, "singleStepOffset")
        mStrengthLocation = GLES20.glGetUniformLocation(program, "strength")
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }

    private fun setTexelSize(w: Float, h: Float) {
        setFloatVec2(mSingleStepOffsetLocation, floatArrayOf(1.0f / w, 1.0f / h))
    }

    protected override fun onInitialized() {
        super.onInitialized()
        setFloat(mStrengthLocation, 0.5f)
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        setTexelSize(width.toFloat(), height.toFloat())
    }


}
