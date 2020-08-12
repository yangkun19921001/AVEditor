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
 * Adjusts the alpha channel of the incoming image
 * opacity: The value to multiply the incoming alpha channel for each pixel by (0.0 - 1.0, with 1.0 as the default)
 */
class GPUImageOpacityFilter @JvmOverloads constructor(context: Context,private var opacity: Float = 1.0f) :
    GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, OPACITY_FRAGMENT_SHADER) {

    private var opacityLocation: Int = 0

    override fun onInit() {
        super.onInit()
        opacityLocation = GLES20.glGetUniformLocation(program, "opacity")
    }

    override fun onInitialized() {
        super.onInitialized()
        setOpacity(opacity)
    }

    fun setOpacity(opacity: Float) {
        this.opacity = opacity
        setFloat(opacityLocation, this.opacity)
    }

    companion object {
        val OPACITY_FRAGMENT_SHADER = "" +
                "  varying highp vec2 textureCoordinate;\n" +
                "  \n" +
                "  uniform sampler2D inputImageTexture;\n" +
                "  uniform lowp float opacity;\n" +
                "  \n" +
                "  void main()\n" +
                "  {\n" +
                "      lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "      \n" +
                "      gl_FragColor = vec4(textureColor.rgb, textureColor.a * opacity);\n" +
                "  }\n"
    }
}
