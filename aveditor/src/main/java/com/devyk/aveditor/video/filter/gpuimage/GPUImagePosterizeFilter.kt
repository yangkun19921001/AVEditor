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
 * Reduces the color range of the image. <br></br>
 * <br></br>
 * colorLevels: ranges from 1 to 256, with a default of 10
 */
class GPUImagePosterizeFilter @JvmOverloads constructor(context: Context,private var colorLevels: Int = 10) :
    GPUImageFilter(context, GPUImageFilter.NO_FILTER_VERTEX_SHADER, POSTERIZE_FRAGMENT_SHADER) {

    private var glUniformColorLevels: Int = 0

    override fun onInit() {
        super.onInit()
        glUniformColorLevels = GLES20.glGetUniformLocation(program, "colorLevels")
    }

    override fun onInitialized() {
        super.onInitialized()
        setColorLevels(colorLevels)
    }

    fun setColorLevels(colorLevels: Int) {
        this.colorLevels = colorLevels
        setFloat(glUniformColorLevels, colorLevels.toFloat())
    }

    companion object {
        val POSTERIZE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform highp float colorLevels;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "   \n" +
                "   gl_FragColor = floor((textureColor * colorLevels) + vec4(0.5)) / colorLevels;\n" +
                "}"
    }
}
