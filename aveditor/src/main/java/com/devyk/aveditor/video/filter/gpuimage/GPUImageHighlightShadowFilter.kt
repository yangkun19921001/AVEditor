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
 * Adjusts the shadows and highlights of an image
 * shadows: Increase to lighten shadows, from 0.0 to 1.0, with 0.0 as the default.
 * highlights: Decrease to darken highlights, from 0.0 to 1.0, with 1.0 as the default.
 */
class GPUImageHighlightShadowFilter @JvmOverloads constructor(context: Context,
    private var shadows: Float = 0.0f,
    private var highlights: Float = 1.0f
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, HIGHLIGHT_SHADOW_FRAGMENT_SHADER) {

    private var shadowsLocation: Int = 0
    private var highlightsLocation: Int = 0

    override fun onInit() {
        super.onInit()
        highlightsLocation = GLES20.glGetUniformLocation(program, "highlights")
        shadowsLocation = GLES20.glGetUniformLocation(program, "shadows")
    }

    override fun onInitialized() {
        super.onInitialized()
        setHighlights(highlights)
        setShadows(shadows)
    }

    fun setHighlights(highlights: Float) {
        this.highlights = highlights
        setFloat(highlightsLocation, this.highlights)
    }

    fun setShadows(shadows: Float) {
        this.shadows = shadows
        setFloat(shadowsLocation, this.shadows)
    }

    companion object {
        val HIGHLIGHT_SHADOW_FRAGMENT_SHADER = "" +
                " uniform sampler2D inputImageTexture;\n" +
                " varying highp vec2 textureCoordinate;\n" +
                "  \n" +
                " uniform lowp float shadows;\n" +
                " uniform lowp float highlights;\n" +
                " \n" +
                " const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                " 	lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
                " 	mediump float luminance = dot(source.rgb, luminanceWeighting);\n" +
                " \n" +
                " 	mediump float shadow = clamp((pow(luminance, 1.0/(shadows+1.0)) + (-0.76)*pow(luminance, 2.0/(shadows+1.0))) - luminance, 0.0, 1.0);\n" +
                " 	mediump float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-highlights)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-highlights)))) - luminance, -1.0, 0.0);\n" +
                " 	lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
                " \n" +
                " 	gl_FragColor = vec4(result.rgb, source.a);\n" +
                " }"
    }
}
