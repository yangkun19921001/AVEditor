package com.devyk.aveditor.video.filter.magic

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.R
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter


/**
 * Created by Administrator on 2016/5/22.
 */
class AVToolsBeautyFilter(context: Context) : GPUImageFilter(context,
    NO_FILTER_VERTEX_SHADER, OpenGLUtils.readRawTextFile(
        context,
        R.raw.beauty
    )
) {
    private var mSingleStepOffsetLocation: Int = 0
    private var mParamsLocation: Int = 0

    protected override fun onInit() {
        super.onInit()
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(program, "singleStepOffset")
        mParamsLocation = GLES20.glGetUniformLocation(program, "params")
        configBeautyLevel(beautyLevel)
    }

    private fun setTexelSize(w: Float, h: Float) {
        setFloatVec2(mSingleStepOffsetLocation, floatArrayOf(2.0f / w, 2.0f / h))
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        setTexelSize(width.toFloat(), height.toFloat())
    }

    fun onBeautyLevelChanged() {
        configBeautyLevel(beautyLevel)
    }

    public fun configBeautyLevel(level: Float) {
        setFloat(mParamsLocation, level)
//        when (level) {
//            1 -> setFloat(mParamsLocation, 1.0f)
//            2 -> setFloat(mParamsLocation, 0.8f)
//            3 -> setFloat(mParamsLocation, 0.6f)
//            4 -> setFloat(mParamsLocation, 0.4f)
//            5 -> setFloat(mParamsLocation, 0.33f)
//            else -> {
//            }
//        }

//        when (level) {
//            1 -> setFloat(mParamsLocation, 1.0f)
//            2 -> setFloat(mParamsLocation, 0.8f)
//            3 -> setFloat(mParamsLocation, 0.6f)
//            4 -> setFloat(mParamsLocation, 0.4f)
//            5 -> setFloat(mParamsLocation, 0.33f)
//            else -> {
//            }
//        }
    }
}
