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

/**
 * This uses Sobel edge detection to place a black border around objects,
 * and then it quantizes the colors present in the image to give a cartoon-like quality to the image.
 */
class GPUImageToonFilter @JvmOverloads constructor(context: Context,
    private var threshold: Float = 0.2f,
    private var quantizationLevels: Float = 10.0f
) : GPUImage3x3TextureSamplingFilter(context,TOON_FRAGMENT_SHADER) {
    private var thresholdLocation: Int = 0
    private var quantizationLevelsLocation: Int = 0

    override fun onInit() {
        super.onInit()
        thresholdLocation = GLES20.glGetUniformLocation(program, "threshold")
        quantizationLevelsLocation = GLES20.glGetUniformLocation(program, "quantizationLevels")
    }

    override fun onInitialized() {
        super.onInitialized()
        setThreshold(threshold)
        setQuantizationLevels(quantizationLevels)
    }

    /**
     * The threshold at which to apply the edges, default of 0.2.
     *
     * @param threshold default 0.2
     */
    fun setThreshold(threshold: Float) {
        this.threshold = threshold
        setFloat(thresholdLocation, threshold)
    }

    /**
     * The levels of quantization for the posterization of colors within the scene, with a default of 10.0.
     *
     * @param quantizationLevels default 10.0
     */
    fun setQuantizationLevels(quantizationLevels: Float) {
        this.quantizationLevels = quantizationLevels
        setFloat(quantizationLevelsLocation, quantizationLevels)
    }

    companion object {
        val TOON_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +
                "\n" +
                "varying vec2 textureCoordinate;\n" +
                "varying vec2 leftTextureCoordinate;\n" +
                "varying vec2 rightTextureCoordinate;\n" +
                "\n" +
                "varying vec2 topTextureCoordinate;\n" +
                "varying vec2 topLeftTextureCoordinate;\n" +
                "varying vec2 topRightTextureCoordinate;\n" +
                "\n" +
                "varying vec2 bottomTextureCoordinate;\n" +
                "varying vec2 bottomLeftTextureCoordinate;\n" +
                "varying vec2 bottomRightTextureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform highp float intensity;\n" +
                "uniform highp float threshold;\n" +
                "uniform highp float quantizationLevels;\n" +
                "\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "\n" +
                "float bottomLeftIntensity = texture2D(inputImageTexture, bottomLeftTextureCoordinate).r;\n" +
                "float topRightIntensity = texture2D(inputImageTexture, topRightTextureCoordinate).r;\n" +
                "float topLeftIntensity = texture2D(inputImageTexture, topLeftTextureCoordinate).r;\n" +
                "float bottomRightIntensity = texture2D(inputImageTexture, bottomRightTextureCoordinate).r;\n" +
                "float leftIntensity = texture2D(inputImageTexture, leftTextureCoordinate).r;\n" +
                "float rightIntensity = texture2D(inputImageTexture, rightTextureCoordinate).r;\n" +
                "float bottomIntensity = texture2D(inputImageTexture, bottomTextureCoordinate).r;\n" +
                "float topIntensity = texture2D(inputImageTexture, topTextureCoordinate).r;\n" +
                "float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
                "float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
                "\n" +
                "float mag = length(vec2(h, v));\n" +
                "\n" +
                "vec3 posterizedImageColor = floor((textureColor.rgb * quantizationLevels) + 0.5) / quantizationLevels;\n" +
                "\n" +
                "float thresholdTest = 1.0 - step(threshold, mag);\n" +
                "\n" +
                "gl_FragColor = vec4(posterizedImageColor * thresholdTest, textureColor.a);\n" +
                "}\n"
    }
}
