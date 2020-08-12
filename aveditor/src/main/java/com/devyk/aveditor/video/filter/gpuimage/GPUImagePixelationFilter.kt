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
 * Applies a grayscale effect to the image.
 */
class GPUImagePixelationFilter(context: Context) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, PIXELATION_FRAGMENT_SHADER) {

    private var imageWidthFactorLocation: Int = 0
    private var imageHeightFactorLocation: Int = 0
    private var pixel: Float = 0.toFloat()
    private var pixelLocation: Int = 0

    init {
        pixel = 1.0f
    }

    override fun onInit() {
        super.onInit()
        imageWidthFactorLocation = GLES20.glGetUniformLocation(program, "imageWidthFactor")
        imageHeightFactorLocation = GLES20.glGetUniformLocation(program, "imageHeightFactor")
        pixelLocation = GLES20.glGetUniformLocation(program, "pixel")
    }

    override fun onInitialized() {
        super.onInitialized()
        setPixel(pixel)
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        setFloat(imageWidthFactorLocation, 1.0f / width)
        setFloat(imageHeightFactorLocation, 1.0f / height)
    }


    fun setPixel(pixel: Float) {
        this.pixel = pixel
        setFloat(pixelLocation, this.pixel)
    }

    companion object {
        val PIXELATION_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +

                "varying vec2 textureCoordinate;\n" +

                "uniform float imageWidthFactor;\n" +
                "uniform float imageHeightFactor;\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform float pixel;\n" +

                "void main()\n" +
                "{\n" +
                "  vec2 uv  = textureCoordinate.xy;\n" +
                "  float dx = pixel * imageWidthFactor;\n" +
                "  float dy = pixel * imageHeightFactor;\n" +
                "  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
                "  vec3 tc = texture2D(inputImageTexture, coord).xyz;\n" +
                "  gl_FragColor = vec4(tc, 1.0);\n" +
                "}"
    }
}
