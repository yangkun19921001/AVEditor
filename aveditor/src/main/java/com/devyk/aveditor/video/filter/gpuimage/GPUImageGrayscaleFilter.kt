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
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER

/**
 * Applies a grayscale effect to the image.
 */
class GPUImageGrayscaleFilter(context: Context) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, GRAYSCALE_FRAGMENT_SHADER) {
    companion object {
        val GRAYSCALE_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +
                "\n" +
                "varying vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "  lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "  float luminance = dot(textureColor.rgb, W);\n" +
                "\n" +
                "  gl_FragColor = vec4(vec3(luminance), textureColor.a);\n" +
                "}"
    }
}
