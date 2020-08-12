package com.devyk.aveditor.video.filter.gpuimage

import android.content.Context
import android.graphics.PointF
import android.opengl.GLES20
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER

class GPUImageZoomBlurFilter @JvmOverloads constructor(context: Context,
    private var blurCenter: PointF? = PointF(0.5f, 0.5f),
    private var blurSize: Float = 1.0f
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, ZOOM_BLUR_FRAGMENT_SHADER) {
    private var blurCenterLocation: Int = 0
    private var blurSizeLocation: Int = 0

    override fun onInit() {
        super.onInit()
        blurCenterLocation = GLES20.glGetUniformLocation(program, "blurCenter")
        blurSizeLocation = GLES20.glGetUniformLocation(program, "blurSize")
    }

    override fun onInitialized() {
        super.onInitialized()
        setBlurCenter(blurCenter)
        setBlurSize(blurSize)
    }

    fun setBlurCenter(blurCenter: PointF?) {
        this.blurCenter = blurCenter
        blurCenter?.let { setPoint(blurCenterLocation, it) }
    }

    fun setBlurSize(blurSize: Float) {
        this.blurSize = blurSize
        setFloat(blurSizeLocation, blurSize)
    }

    companion object {
        val ZOOM_BLUR_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform highp vec2 blurCenter;\n" +
                "uniform highp float blurSize;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    // TODO: Do a more intelligent scaling based on resolution here\n" +
                "    highp vec2 samplingOffset = 1.0/100.0 * (blurCenter - textureCoordinate) * blurSize;\n" +
                "    \n" +
                "    lowp vec4 fragmentColor = texture2D(inputImageTexture, textureCoordinate) * 0.18;\n" +
                "    fragmentColor += texture2D(inputImageTexture, textureCoordinate + samplingOffset) * 0.15;\n" +
                "    fragmentColor += texture2D(inputImageTexture, textureCoordinate + (2.0 * samplingOffset)) *  0.12;\n" +
                "    fragmentColor += texture2D(inputImageTexture, textureCoordinate + (3.0 * samplingOffset)) * 0.09;\n" +
                "    fragmentColor += texture2D(inputImageTexture, textureCoordinate + (4.0 * samplingOffset)) * 0.05;\n" +
                "    fragmentColor += texture2D(inputImageTexture, textureCoordinate - samplingOffset) * 0.15;\n" +
                "    fragmentColor += texture2D(inputImageTexture, textureCoordinate - (2.0 * samplingOffset)) *  0.12;\n" +
                "    fragmentColor += texture2D(inputImageTexture, textureCoordinate - (3.0 * samplingOffset)) * 0.09;\n" +
                "    fragmentColor += texture2D(inputImageTexture, textureCoordinate - (4.0 * samplingOffset)) * 0.05;\n" +
                "    \n" +
                "    gl_FragColor = fragmentColor;\n" +
                "}\n"
    }
}
