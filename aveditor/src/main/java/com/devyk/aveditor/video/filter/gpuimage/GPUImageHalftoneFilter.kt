package com.devyk.aveditor.video.filter.gpuimage

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER

class GPUImageHalftoneFilter @JvmOverloads constructor(context: Context,private var fractionalWidthOfAPixel: Float = 0.01f) :
    GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, HALFTONE_FRAGMENT_SHADER) {

    private var fractionalWidthOfPixelLocation: Int = 0
    private var aspectRatioLocation: Int = 0
    private var aspectRatio: Float = 0.toFloat()

    override fun onInit() {
        super.onInit()
        fractionalWidthOfPixelLocation = GLES20.glGetUniformLocation(program, "fractionalWidthOfPixel")
        aspectRatioLocation = GLES20.glGetUniformLocation(program, "aspectRatio")
    }

    override fun onInitialized() {
        super.onInitialized()
        setFractionalWidthOfAPixel(fractionalWidthOfAPixel)
        setAspectRatio(aspectRatio)
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        setAspectRatio(height.toFloat() / width.toFloat())
        
    }


    fun setFractionalWidthOfAPixel(fractionalWidthOfAPixel: Float) {
        this.fractionalWidthOfAPixel = fractionalWidthOfAPixel
        setFloat(fractionalWidthOfPixelLocation, this.fractionalWidthOfAPixel)
    }

    fun setAspectRatio(aspectRatio: Float) {
        this.aspectRatio = aspectRatio
        setFloat(aspectRatioLocation, this.aspectRatio)
    }

    companion object {
        val HALFTONE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +

                "uniform sampler2D inputImageTexture;\n" +

                "uniform highp float fractionalWidthOfPixel;\n" +
                "uniform highp float aspectRatio;\n" +

                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +

                "void main()\n" +
                "{\n" +
                "  highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
                "  highp vec2 samplePos = textureCoordinate - mod(textureCoordinate, sampleDivisor) + 0.5 * sampleDivisor;\n" +
                "  highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "  highp vec2 adjustedSamplePos = vec2(samplePos.x, (samplePos.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "  highp float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);\n" +
                "  lowp vec3 sampledColor = texture2D(inputImageTexture, samplePos).rgb;\n" +
                "  highp float dotScaling = 1.0 - dot(sampledColor, W);\n" +
                "  lowp float checkForPresenceWithinDot = 1.0 - step(distanceFromSamplePoint, (fractionalWidthOfPixel * 0.5) * dotScaling);\n" +
                "  gl_FragColor = vec4(vec3(checkForPresenceWithinDot), 1.0);\n" +
                "}"
    }
}
