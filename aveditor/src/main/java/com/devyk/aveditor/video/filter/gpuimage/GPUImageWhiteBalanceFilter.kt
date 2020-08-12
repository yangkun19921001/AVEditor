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
 * Adjusts the white balance of incoming image. <br></br>
 * <br></br>
 * temperature:
 * tint:
 */
class GPUImageWhiteBalanceFilter @JvmOverloads constructor(context: Context,
    private var temperature: Float = 5000.0f,
    private var tint: Float = 0.0f
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, WHITE_BALANCE_FRAGMENT_SHADER) {

    private var temperatureLocation: Int = 0
    private var tintLocation: Int = 0

    override fun onInit() {
        super.onInit()
        temperatureLocation = GLES20.glGetUniformLocation(program, "temperature")
        tintLocation = GLES20.glGetUniformLocation(program, "tint")
    }

    override fun onInitialized() {
        super.onInitialized()
        setTemperature(temperature)
        setTint(tint)
    }

    fun setTemperature(temperature: Float) {
        this.temperature = temperature
        setFloat(
            temperatureLocation,
            if (this.temperature < 5000) (0.0004 * (this.temperature - 5000.0)).toFloat() else (0.00006 * (this.temperature - 5000.0)).toFloat()
        )
    }

    fun setTint(tint: Float) {
        this.tint = tint
        setFloat(tintLocation, (this.tint / 100.0).toFloat())
    }

    companion object {
        val WHITE_BALANCE_FRAGMENT_SHADER = "" +
                "uniform sampler2D inputImageTexture;\n" +
                "varying highp vec2 textureCoordinate;\n" +
                " \n" +
                "uniform lowp float temperature;\n" +
                "uniform lowp float tint;\n" +
                "\n" +
                "const lowp vec3 warmFilter = vec3(0.93, 0.54, 0.0);\n" +
                "\n" +
                "const mediump mat3 RGBtoYIQ = mat3(0.299, 0.587, 0.114, 0.596, -0.274, -0.322, 0.212, -0.523, 0.311);\n" +
                "const mediump mat3 YIQtoRGB = mat3(1.0, 0.956, 0.621, 1.0, -0.272, -0.647, 1.0, -1.105, 1.702);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "	lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
                "	\n" +
                "	mediump vec3 yiq = RGBtoYIQ * source.rgb; //adjusting tint\n" +
                "	yiq.b = clamp(yiq.b + tint*0.5226*0.1, -0.5226, 0.5226);\n" +
                "	lowp vec3 rgb = YIQtoRGB * yiq;\n" +
                "\n" +
                "	lowp vec3 processed = vec3(\n" +
                "		(rgb.r < 0.5 ? (2.0 * rgb.r * warmFilter.r) : (1.0 - 2.0 * (1.0 - rgb.r) * (1.0 - warmFilter.r))), //adjusting temperature\n" +
                "		(rgb.g < 0.5 ? (2.0 * rgb.g * warmFilter.g) : (1.0 - 2.0 * (1.0 - rgb.g) * (1.0 - warmFilter.g))), \n" +
                "		(rgb.b < 0.5 ? (2.0 * rgb.b * warmFilter.b) : (1.0 - 2.0 * (1.0 - rgb.b) * (1.0 - warmFilter.b))));\n" +
                "\n" +
                "	gl_FragColor = vec4(mix(rgb, processed, temperature), source.a);\n" +
                "}"
    }
}
