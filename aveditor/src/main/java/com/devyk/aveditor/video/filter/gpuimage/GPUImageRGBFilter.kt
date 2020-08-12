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
 * Adjusts the individual RGB channels of an image
 * red: Normalized values by which each color channel is multiplied. The range is from 0.0 up, with 1.0 as the default.
 * green:
 * blue:
 */
class GPUImageRGBFilter @JvmOverloads constructor(context: Context,
    private var red: Float = 1.0f,
    private var green: Float = 1.0f,
    private var blue: Float = 1.0f
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, RGB_FRAGMENT_SHADER) {

    private var redLocation: Int = 0
    private var greenLocation: Int = 0
    private var blueLocation: Int = 0

    override fun onInit() {
        super.onInit()
        redLocation = GLES20.glGetUniformLocation(program, "red")
        greenLocation = GLES20.glGetUniformLocation(program, "green")
        blueLocation = GLES20.glGetUniformLocation(program, "blue")
    }

    override fun onInitialized() {
        super.onInitialized()
        setRed(red)
        setGreen(green)
        setBlue(blue)
    }

    fun setRed(red: Float) {
        this.red = red
        setFloat(redLocation, this.red)
    }

    fun setGreen(green: Float) {
        this.green = green
        setFloat(greenLocation, this.green)
    }

    fun setBlue(blue: Float) {
        this.blue = blue
        setFloat(blueLocation, this.blue)
    }

    companion object {
        val RGB_FRAGMENT_SHADER = "" +
                "  varying highp vec2 textureCoordinate;\n" +
                "  \n" +
                "  uniform sampler2D inputImageTexture;\n" +
                "  uniform highp float red;\n" +
                "  uniform highp float green;\n" +
                "  uniform highp float blue;\n" +
                "  \n" +
                "  void main()\n" +
                "  {\n" +
                "      highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "      \n" +
                "      gl_FragColor = vec4(textureColor.r * red, textureColor.g * green, textureColor.b * blue, 1.0);\n" +
                "  }\n"
    }
}
