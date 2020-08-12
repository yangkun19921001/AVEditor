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

class GPUImageHueFilter @JvmOverloads constructor(context: Context,private var hue: Float = 90.0f) :
    GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, HUE_FRAGMENT_SHADER) {
    private var hueLocation: Int = 0

    override fun onInit() {
        super.onInit()
        hueLocation = GLES20.glGetUniformLocation(program, "hueAdjust")
    }

    override fun onInitialized() {
        super.onInitialized()
        setHue(hue)
    }

    fun setHue(hue: Float) {
        this.hue = hue
        val hueAdjust = this.hue % 360.0f * Math.PI.toFloat() / 180.0f
        setFloat(hueLocation, hueAdjust)
    }

    companion object {
        val HUE_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform mediump float hueAdjust;\n" +
                "const highp vec4 kRGBToYPrime = vec4 (0.299, 0.587, 0.114, 0.0);\n" +
                "const highp vec4 kRGBToI = vec4 (0.595716, -0.274453, -0.321263, 0.0);\n" +
                "const highp vec4 kRGBToQ = vec4 (0.211456, -0.522591, 0.31135, 0.0);\n" +
                "\n" +
                "const highp vec4 kYIQToR = vec4 (1.0, 0.9563, 0.6210, 0.0);\n" +
                "const highp vec4 kYIQToG = vec4 (1.0, -0.2721, -0.6474, 0.0);\n" +
                "const highp vec4 kYIQToB = vec4 (1.0, -1.1070, 1.7046, 0.0);\n" +
                "\n" +
                "void main ()\n" +
                "{\n" +
                "    // Sample the input pixel\n" +
                "    highp vec4 color = texture2D(inputImageTexture, textureCoordinate);\n" +
                "\n" +
                "    // Convert to YIQ\n" +
                "    highp float YPrime = dot (color, kRGBToYPrime);\n" +
                "    highp float I = dot (color, kRGBToI);\n" +
                "    highp float Q = dot (color, kRGBToQ);\n" +
                "\n" +
                "    // Calculate the hue and chroma\n" +
                "    highp float hue = atan (Q, I);\n" +
                "    highp float chroma = sqrt (I * I + Q * Q);\n" +
                "\n" +
                "    // Make the user's adjustments\n" +
                "    hue += (-hueAdjust); //why negative rotation?\n" +
                "\n" +
                "    // Convert back to YIQ\n" +
                "    Q = chroma * sin (hue);\n" +
                "    I = chroma * cos (hue);\n" +
                "\n" +
                "    // Convert back to RGB\n" +
                "    highp vec4 yIQ = vec4 (YPrime, I, Q, 0.0);\n" +
                "    color.r = dot (yIQ, kYIQToR);\n" +
                "    color.g = dot (yIQ, kYIQToG);\n" +
                "    color.b = dot (yIQ, kYIQToB);\n" +
                "\n" +
                "    // Save the result\n" +
                "    gl_FragColor = color;\n" +
                "}\n"
    }
}
