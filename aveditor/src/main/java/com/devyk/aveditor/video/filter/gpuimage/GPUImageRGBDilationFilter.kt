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

/**
 * For each pixel, this sets it to the maximum value of each color channel in a rectangular neighborhood extending
 * out dilationRadius pixels from the center.
 * This extends out brighter colors, and can be used for abstraction of color images.
 */
class GPUImageRGBDilationFilter private constructor(context: Context,vertexShader: String, fragmentShader: String) :
    GPUImageTwoPassTextureSamplingFilter(context,vertexShader, fragmentShader, vertexShader, fragmentShader) {

    /**
     * Acceptable values for dilationRadius, which sets the distance in pixels to sample out
     * from the center, are 1, 2, 3, and 4.
     *
     * @param radius 1, 2, 3 or 4
     */
    @JvmOverloads
    constructor(context: Context,radius: Int = 1) : this(context,getVertexShader(radius), getFragmentShader(radius)) {
    }

    companion object {
        val VERTEX_SHADER_1 = "attribute vec4 position;\n" +
                "attribute vec2 inputTextureCoordinate;\n" +
                "\n" +
                "uniform float texelWidthOffset; \n" +
                "uniform float texelHeightOffset; \n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "gl_Position = position;\n" +
                "\n" +
                "vec2 offset = vec2(texelWidthOffset, texelHeightOffset);\n" +
                "\n" +
                "centerTextureCoordinate = inputTextureCoordinate;\n" +
                "oneStepNegativeTextureCoordinate = inputTextureCoordinate - offset;\n" +
                "oneStepPositiveTextureCoordinate = inputTextureCoordinate + offset;\n" +
                "}\n"

        val VERTEX_SHADER_2 = "attribute vec4 position;\n" +
                "attribute vec2 inputTextureCoordinate;\n" +
                "\n" +
                "uniform float texelWidthOffset;\n" +
                "uniform float texelHeightOffset;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "gl_Position = position;\n" +
                "\n" +
                "vec2 offset = vec2(texelWidthOffset, texelHeightOffset);\n" +
                "\n" +
                "centerTextureCoordinate = inputTextureCoordinate;\n" +
                "oneStepNegativeTextureCoordinate = inputTextureCoordinate - offset;\n" +
                "oneStepPositiveTextureCoordinate = inputTextureCoordinate + offset;\n" +
                "twoStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 2.0);\n" +
                "twoStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 2.0);\n" +
                "}\n"

        val VERTEX_SHADER_3 = "attribute vec4 position;\n" +
                "attribute vec2 inputTextureCoordinate;\n" +
                "\n" +
                "uniform float texelWidthOffset;\n" +
                "uniform float texelHeightOffset;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
                "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "gl_Position = position;\n" +
                "\n" +
                "vec2 offset = vec2(texelWidthOffset, texelHeightOffset);\n" +
                "\n" +
                "centerTextureCoordinate = inputTextureCoordinate;\n" +
                "oneStepNegativeTextureCoordinate = inputTextureCoordinate - offset;\n" +
                "oneStepPositiveTextureCoordinate = inputTextureCoordinate + offset;\n" +
                "twoStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 2.0);\n" +
                "twoStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 2.0);\n" +
                "threeStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 3.0);\n" +
                "threeStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 3.0);\n" +
                "}\n"

        val VERTEX_SHADER_4 = "attribute vec4 position;\n" +
                "attribute vec2 inputTextureCoordinate;\n" +
                "\n" +
                "uniform float texelWidthOffset;\n" +
                "uniform float texelHeightOffset;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
                "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
                "varying vec2 fourStepsPositiveTextureCoordinate;\n" +
                "varying vec2 fourStepsNegativeTextureCoordinate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "gl_Position = position;\n" +
                "\n" +
                "vec2 offset = vec2(texelWidthOffset, texelHeightOffset);\n" +
                "\n" +
                "centerTextureCoordinate = inputTextureCoordinate;\n" +
                "oneStepNegativeTextureCoordinate = inputTextureCoordinate - offset;\n" +
                "oneStepPositiveTextureCoordinate = inputTextureCoordinate + offset;\n" +
                "twoStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 2.0);\n" +
                "twoStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 2.0);\n" +
                "threeStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 3.0);\n" +
                "threeStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 3.0);\n" +
                "fourStepsNegativeTextureCoordinate = inputTextureCoordinate - (offset * 4.0);\n" +
                "fourStepsPositiveTextureCoordinate = inputTextureCoordinate + (offset * 4.0);\n" +
                "}\n"


        val FRAGMENT_SHADER_1 = "precision highp float;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "lowp vec4 centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate);\n" +
                "lowp vec4 oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate);\n" +
                "lowp vec4 oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate);\n" +
                "\n" +
                "lowp vec4 maxValue = max(centerIntensity, oneStepPositiveIntensity);\n" +
                "\n" +
                "gl_FragColor = max(maxValue, oneStepNegativeIntensity);\n" +
                "}\n"

        val FRAGMENT_SHADER_2 = "precision highp float;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "lowp vec4 centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate);\n" +
                "lowp vec4 oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate);\n" +
                "lowp vec4 oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate);\n" +
                "lowp vec4 twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate);\n" +
                "lowp vec4 twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate);\n" +
                "\n" +
                "lowp vec4 maxValue = max(centerIntensity, oneStepPositiveIntensity);\n" +
                "maxValue = max(maxValue, oneStepNegativeIntensity);\n" +
                "maxValue = max(maxValue, twoStepsPositiveIntensity);\n" +
                "maxValue = max(maxValue, twoStepsNegativeIntensity);\n" +
                "\n" +
                "gl_FragColor = max(maxValue, twoStepsNegativeIntensity);\n" +
                "}\n"

        val FRAGMENT_SHADER_3 = "precision highp float;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
                "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "lowp vec4 centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate);\n" +
                "lowp vec4 oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate);\n" +
                "lowp vec4 oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate);\n" +
                "lowp vec4 twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate);\n" +
                "lowp vec4 twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate);\n" +
                "lowp vec4 threeStepsPositiveIntensity = texture2D(inputImageTexture, threeStepsPositiveTextureCoordinate);\n" +
                "lowp vec4 threeStepsNegativeIntensity = texture2D(inputImageTexture, threeStepsNegativeTextureCoordinate);\n" +
                "\n" +
                "lowp vec4 maxValue = max(centerIntensity, oneStepPositiveIntensity);\n" +
                "maxValue = max(maxValue, oneStepNegativeIntensity);\n" +
                "maxValue = max(maxValue, twoStepsPositiveIntensity);\n" +
                "maxValue = max(maxValue, twoStepsNegativeIntensity);\n" +
                "maxValue = max(maxValue, threeStepsPositiveIntensity);\n" +
                "\n" +
                "gl_FragColor = max(maxValue, threeStepsNegativeIntensity);\n" +
                "}\n"

        val FRAGMENT_SHADER_4 = "precision highp float;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepPositiveTextureCoordinate;\n" +
                "varying vec2 oneStepNegativeTextureCoordinate;\n" +
                "varying vec2 twoStepsPositiveTextureCoordinate;\n" +
                "varying vec2 twoStepsNegativeTextureCoordinate;\n" +
                "varying vec2 threeStepsPositiveTextureCoordinate;\n" +
                "varying vec2 threeStepsNegativeTextureCoordinate;\n" +
                "varying vec2 fourStepsPositiveTextureCoordinate;\n" +
                "varying vec2 fourStepsNegativeTextureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "lowp vec4 centerIntensity = texture2D(inputImageTexture, centerTextureCoordinate);\n" +
                "lowp vec4 oneStepPositiveIntensity = texture2D(inputImageTexture, oneStepPositiveTextureCoordinate);\n" +
                "lowp vec4 oneStepNegativeIntensity = texture2D(inputImageTexture, oneStepNegativeTextureCoordinate);\n" +
                "lowp vec4 twoStepsPositiveIntensity = texture2D(inputImageTexture, twoStepsPositiveTextureCoordinate);\n" +
                "lowp vec4 twoStepsNegativeIntensity = texture2D(inputImageTexture, twoStepsNegativeTextureCoordinate);\n" +
                "lowp vec4 threeStepsPositiveIntensity = texture2D(inputImageTexture, threeStepsPositiveTextureCoordinate);\n" +
                "lowp vec4 threeStepsNegativeIntensity = texture2D(inputImageTexture, threeStepsNegativeTextureCoordinate);\n" +
                "lowp vec4 fourStepsPositiveIntensity = texture2D(inputImageTexture, fourStepsPositiveTextureCoordinate);\n" +
                "lowp vec4 fourStepsNegativeIntensity = texture2D(inputImageTexture, fourStepsNegativeTextureCoordinate);\n" +
                "\n" +
                "lowp vec4 maxValue = max(centerIntensity, oneStepPositiveIntensity);\n" +
                "maxValue = max(maxValue, oneStepNegativeIntensity);\n" +
                "maxValue = max(maxValue, twoStepsPositiveIntensity);\n" +
                "maxValue = max(maxValue, twoStepsNegativeIntensity);\n" +
                "maxValue = max(maxValue, threeStepsPositiveIntensity);\n" +
                "maxValue = max(maxValue, threeStepsNegativeIntensity);\n" +
                "maxValue = max(maxValue, fourStepsPositiveIntensity);\n" +
                "\n" +
                "gl_FragColor = max(maxValue, fourStepsNegativeIntensity);\n" +
                "}\n"

        private fun getVertexShader(radius: Int): String {
            when (radius) {
                0, 1 -> return VERTEX_SHADER_1
                2 -> return VERTEX_SHADER_2
                3 -> return VERTEX_SHADER_3
                else -> return VERTEX_SHADER_4
            }
        }

        private fun getFragmentShader(radius: Int): String {
            when (radius) {
                0, 1 -> return FRAGMENT_SHADER_1
                2 -> return FRAGMENT_SHADER_2
                3 -> return FRAGMENT_SHADER_3
                else -> return FRAGMENT_SHADER_4
            }
        }
    }
}
