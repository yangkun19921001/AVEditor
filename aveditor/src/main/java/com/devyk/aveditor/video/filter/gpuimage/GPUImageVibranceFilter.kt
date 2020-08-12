package com.devyk.aveditor.video.filter.gpuimage

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER

class GPUImageVibranceFilter @JvmOverloads constructor(context: Context, private var vibrance: Float = 0f) :
    GPUImageFilter(context, NO_FILTER_VERTEX_SHADER, VIBRANCE_FRAGMENT_SHADER) {

    private var vibranceLocation: Int = 0

    override fun onInit() {
        super.onInit()
        vibranceLocation = GLES20.glGetUniformLocation(program, "vibrance")
    }

    override fun onInitialized() {
        super.onInitialized()
        setVibrance(vibrance)
    }

    fun setVibrance(vibrance: Float) {
        this.vibrance = vibrance
        if (isInitialized) {
            setFloat(vibranceLocation, vibrance)
        }
    }

    companion object {
        val VIBRANCE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform lowp float vibrance;\n" +
                "\n" +
                "void main() {\n" +
                "    lowp vec4 color = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    lowp float average = (color.r + color.g + color.b) / 3.0;\n" +
                "    lowp float mx = max(color.r, max(color.g, color.b));\n" +
                "    lowp float amt = (mx - average) * (-vibrance * 3.0);\n" +
                "    color.rgb = mix(color.rgb, vec3(mx), amt);\n" +
                "    gl_FragColor = color;\n" +
                "}"
    }
}

