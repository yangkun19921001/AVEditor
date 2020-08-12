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
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER

/**
 * Converts the image to a single-color version, based on the luminance of each pixel
 * intensity: The degree to which the specific color replaces the normal image color (0.0 - 1.0, with 1.0 as the default)
 * color: The color to use as the basis for the effect, with (0.6, 0.45, 0.3, 1.0) as the default.
 */
class GPUImageMonochromeFilter @JvmOverloads constructor(context: Context,
    private var intensity: Float = 1.0f,
    private var color: FloatArray? = floatArrayOf(
        0.6f,
        0.45f,
        0.3f,
        1.0f
    )
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, MONOCHROME_FRAGMENT_SHADER) {

    private var intensityLocation: Int = 0
    private var filterColorLocation: Int = 0

    override fun onInit() {
        super.onInit()
        intensityLocation = GLES20.glGetUniformLocation(program, "intensity")
        filterColorLocation = GLES20.glGetUniformLocation(program, "filterColor")
    }

    override fun onInitialized() {
        super.onInitialized()
        setIntensity(1.0f)
        setColor(floatArrayOf(0.6f, 0.45f, 0.3f, 1f))
    }

    fun setIntensity(intensity: Float) {
        this.intensity = intensity
        setFloat(intensityLocation, this.intensity)
    }

    fun setColor(color: FloatArray) {
        this.color = color
        setColor(this.color!![0], this.color!![1], this.color!![2])

    }

    fun setColor(red: Float, green: Float, blue: Float) {
        setFloatVec3(filterColorLocation, floatArrayOf(red, green, blue))
    }

    companion object {
        val MONOCHROME_FRAGMENT_SHADER = "" +
                " precision lowp float;\n" +
                "  \n" +
                "  varying highp vec2 textureCoordinate;\n" +
                "  \n" +
                "  uniform sampler2D inputImageTexture;\n" +
                "  uniform float intensity;\n" +
                "  uniform vec3 filterColor;\n" +
                "  \n" +
                "  const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                "  \n" +
                "  void main()\n" +
                "  {\n" +
                " 	//desat, then apply overlay blend\n" +
                " 	lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                " 	float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
                " 	\n" +
                " 	lowp vec4 desat = vec4(vec3(luminance), 1.0);\n" +
                " 	\n" +
                " 	//overlay\n" +
                " 	lowp vec4 outputColor = vec4(\n" +
                "                                  (desat.r < 0.5 ? (2.0 * desat.r * filterColor.r) : (1.0 - 2.0 * (1.0 - desat.r) * (1.0 - filterColor.r))),\n" +
                "                                  (desat.g < 0.5 ? (2.0 * desat.g * filterColor.g) : (1.0 - 2.0 * (1.0 - desat.g) * (1.0 - filterColor.g))),\n" +
                "                                  (desat.b < 0.5 ? (2.0 * desat.b * filterColor.b) : (1.0 - 2.0 * (1.0 - desat.b) * (1.0 - filterColor.b))),\n" +
                "                                  1.0\n" +
                "                                  );\n" +
                " 	\n" +
                " 	//which is better, or are they equal?\n" +
                " 	gl_FragColor = vec4( mix(textureColor.rgb, outputColor.rgb, intensity), textureColor.a);\n" +
                "  }"
    }
}
