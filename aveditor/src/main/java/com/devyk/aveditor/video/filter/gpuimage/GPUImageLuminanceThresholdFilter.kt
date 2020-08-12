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

class GPUImageLuminanceThresholdFilter @JvmOverloads constructor(context: Context,private var threshold: Float = 0.5f) :
    GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, LUMINANCE_THRESHOLD_FRAGMENT_SHADER) {

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

        val LUMINANCE_THRESHOLD_FRAGMENT_SHADER = "" +
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
                "    highp float thresholdResult = step(threshold, luminance);\n" +
                "    \n" +
                "    gl_FragColor = vec4(vec3(thresholdResult), textureColor.w);\n" +
                "}"
    }
}
