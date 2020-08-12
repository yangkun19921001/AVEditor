/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devyk.aveditor.video.filter.gpuimage

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter

/**
 * Applies a ColorMatrix to the image.
 */
open class GPUImageColorMatrixFilter @JvmOverloads constructor(context: Context,
    private var intensity: Float = 1.0f,
    private var colorMatrix: FloatArray? = floatArrayOf(
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f
    )
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, COLOR_MATRIX_FRAGMENT_SHADER) {
    private var colorMatrixLocation: Int = 0
    private var intensityLocation: Int = 0

    override fun onInit() {
        super.onInit()
        colorMatrixLocation = GLES20.glGetUniformLocation(program, "colorMatrix")
        intensityLocation = GLES20.glGetUniformLocation(program, "intensity")
    }

    override fun onInitialized() {
        super.onInitialized()
        setIntensity(intensity)
        setColorMatrix(colorMatrix)
    }

    fun setIntensity(intensity: Float) {
        this.intensity = intensity
        setFloat(intensityLocation, intensity)
    }

    fun setColorMatrix(colorMatrix: FloatArray?) {
        this.colorMatrix = colorMatrix
        colorMatrix?.let { setUniformMatrix4f(colorMatrixLocation, it) }
    }

    companion object {
        val COLOR_MATRIX_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform lowp mat4 colorMatrix;\n" +
                "uniform lowp float intensity;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    lowp vec4 outputColor = textureColor * colorMatrix;\n" +
                "    \n" +
                "    gl_FragColor = (intensity * outputColor) + ((1.0 - intensity) * textureColor);\n" +
                "}"
    }
}
