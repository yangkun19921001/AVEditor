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
 * saturation: The degree of saturation or desaturation to apply to the image (0.0 - 2.0, with 1.0 as the default)
 */
class GPUImageSaturationFilter @JvmOverloads constructor(context: Context,private var saturation: Float = 1.0f) :
    GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, SATURATION_FRAGMENT_SHADER) {

    private var saturationLocation: Int = 0

    override fun onInit() {
        super.onInit()
        saturationLocation = GLES20.glGetUniformLocation(program, "saturation")
    }

    override fun onInitialized() {
        super.onInitialized()
        setSaturation(saturation)
    }

    fun setSaturation(saturation: Float) {
        this.saturation = saturation
        setFloat(saturationLocation, this.saturation)
    }

    companion object {
        val SATURATION_FRAGMENT_SHADER = "" +
                " varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform lowp float saturation;\n" +
                " \n" +
                " // Values from \"Graphics Shaders: Theory and Practice\" by Bailey and Cunningham\n" +
                " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
                "    lowp vec3 greyScaleColor = vec3(luminance);\n" +
                "    \n" +
                "    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);\n" +
                "     \n" +
                " }"
    }
}
