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
 * A hardware-accelerated 9-hit box blur of an image
 *
 *
 * scaling: for the size of the applied blur, default of 1.0
 */
class GPUImageBoxBlurFilter @JvmOverloads constructor(context: Context,blurSize: Float = 1f) :
    GPUImageTwoPassTextureSamplingFilter(context,VERTEX_SHADER, FRAGMENT_SHADER, VERTEX_SHADER, FRAGMENT_SHADER) {

    override var verticalTexelOffsetRatio: Float = 0.toFloat()
        public set(value: Float) {
            super.verticalTexelOffsetRatio = value
        }


    init {
        this.verticalTexelOffsetRatio = blurSize
    }

    override fun onInitialized() {
        super.onInitialized()
        setBlurSize(verticalTexelOffsetRatio)
    }

    /**
     * A scaling for the size of the applied blur, default of 1.0
     *
     * @param blurSize
     */
    fun setBlurSize(blurSize: Float) {
        this.verticalTexelOffsetRatio = blurSize
        runOnDraw(Runnable { initTexelOffsets() })
    }



    companion object {
        val VERTEX_SHADER = "attribute vec4 position;\n" +
                "attribute vec2 inputTextureCoordinate;\n" +
                "\n" +
                "uniform float texelWidthOffset; \n" +
                "uniform float texelHeightOffset; \n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepLeftTextureCoordinate;\n" +
                "varying vec2 twoStepsLeftTextureCoordinate;\n" +
                "varying vec2 oneStepRightTextureCoordinate;\n" +
                "varying vec2 twoStepsRightTextureCoordinate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "gl_Position = position;\n" +
                "\n" +
                "vec2 firstOffset = vec2(1.5 * texelWidthOffset, 1.5 * texelHeightOffset);\n" +
                "vec2 secondOffset = vec2(3.5 * texelWidthOffset, 3.5 * texelHeightOffset);\n" +
                "\n" +
                "centerTextureCoordinate = inputTextureCoordinate;\n" +
                "oneStepLeftTextureCoordinate = inputTextureCoordinate - firstOffset;\n" +
                "twoStepsLeftTextureCoordinate = inputTextureCoordinate - secondOffset;\n" +
                "oneStepRightTextureCoordinate = inputTextureCoordinate + firstOffset;\n" +
                "twoStepsRightTextureCoordinate = inputTextureCoordinate + secondOffset;\n" +
                "}\n"

        val FRAGMENT_SHADER = "precision highp float;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepLeftTextureCoordinate;\n" +
                "varying vec2 twoStepsLeftTextureCoordinate;\n" +
                "varying vec2 oneStepRightTextureCoordinate;\n" +
                "varying vec2 twoStepsRightTextureCoordinate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "lowp vec4 fragmentColor = texture2D(inputImageTexture, centerTextureCoordinate) * 0.2;\n" +
                "fragmentColor += texture2D(inputImageTexture, oneStepLeftTextureCoordinate) * 0.2;\n" +
                "fragmentColor += texture2D(inputImageTexture, oneStepRightTextureCoordinate) * 0.2;\n" +
                "fragmentColor += texture2D(inputImageTexture, twoStepsLeftTextureCoordinate) * 0.2;\n" +
                "fragmentColor += texture2D(inputImageTexture, twoStepsRightTextureCoordinate) * 0.2;\n" +
                "\n" +
                "gl_FragColor = fragmentColor;\n" +
                "}\n"
    }
}
/**
 * Construct new BoxBlurFilter with default blur size of 1.0.
 */
