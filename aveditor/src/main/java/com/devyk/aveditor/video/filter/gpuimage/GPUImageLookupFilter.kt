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

class GPUImageLookupFilter @JvmOverloads constructor(context: Context,private var intensity: Float = 1.0f) :
    GPUImageTwoInputFilter(context,LOOKUP_FRAGMENT_SHADER) {

    private var intensityLocation: Int = 0

    override fun onInit() {
        super.onInit()
        intensityLocation = GLES20.glGetUniformLocation(program, "intensity")
    }

    override fun onInitialized() {
        super.onInitialized()
        setIntensity(intensity)
    }

    fun setIntensity(intensity: Float) {
        this.intensity = intensity
        setFloat(intensityLocation, this.intensity)
    }

    companion object {

        val LOOKUP_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
                " varying highp vec2 textureCoordinate2; // TODO: This is not used\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2; // lookup texture\n" +
                " \n" +
                " uniform lowp float intensity;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "     \n" +
                "     highp float blueColor = textureColor.b * 63.0;\n" +
                "     \n" +
                "     highp vec2 quad1;\n" +
                "     quad1.y = floor(floor(blueColor) / 8.0);\n" +
                "     quad1.x = floor(blueColor) - (quad1.y * 8.0);\n" +
                "     \n" +
                "     highp vec2 quad2;\n" +
                "     quad2.y = floor(ceil(blueColor) / 8.0);\n" +
                "     quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n" +
                "     \n" +
                "     highp vec2 texPos1;\n" +
                "     texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                "     texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                "     \n" +
                "     highp vec2 texPos2;\n" +
                "     texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                "     texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                "     \n" +
                "     lowp vec4 newColor1 = texture2D(inputImageTexture2, texPos1);\n" +
                "     lowp vec4 newColor2 = texture2D(inputImageTexture2, texPos2);\n" +
                "     \n" +
                "     lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
                "     gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), intensity);\n" +
                " }"
    }
}
