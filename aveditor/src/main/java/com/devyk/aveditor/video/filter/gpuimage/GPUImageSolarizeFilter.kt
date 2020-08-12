package com.devyk.aveditor.video.filter.gpuimage

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER

class GPUImageSolarizeFilter @JvmOverloads constructor(context: Context,private var threshold: Float = 0.5f) :
    GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, SOLATIZE_FRAGMENT_SHADER) {

    private var uniformThresholdLocation: Int = 0

    override fun onInit() {
        super.onInit()
        uniformThresholdLocation = GLES20.glGetUniformLocation(program, "threshold")
    }

    override fun onInitialized() {
        super.onInitialized()
        setThreshold(threshold)
    }

    fun setThreshold(threshold: Float) {
        this.threshold = threshold
        setFloat(uniformThresholdLocation, threshold)
    }

    companion object {
        val SOLATIZE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform highp float threshold;\n" +
                "\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    highp float luminance = dot(textureColor.rgb, W);\n" +
                "    highp float thresholdResult = step(luminance, threshold);\n" +
                "    highp vec3 finalColor = abs(thresholdResult - textureColor.rgb);\n" +
                "    \n" +
                "    gl_FragColor = vec4(finalColor, textureColor.w);\n" +
                "}"
    }
}
