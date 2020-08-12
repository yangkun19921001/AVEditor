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

open class GPUImageTwoPassTextureSamplingFilter(context: Context,
    firstVertexShader: String, firstFragmentShader: String,
    secondVertexShader: String, secondFragmentShader: String
) : GPUImageTwoPassFilter(context,firstVertexShader, firstFragmentShader, secondVertexShader, secondFragmentShader) {

    open var verticalTexelOffsetRatio: Float = 0.0f
        get() = 1f

    open val horizontalTexelOffsetRatio: Float
        get() = 1f

    override fun onInit() {
        super.onInit()
        initTexelOffsets()
    }

    protected fun initTexelOffsets() {
        var ratio = horizontalTexelOffsetRatio
        var filter = filters!![0]
        var texelWidthOffsetLocation = GLES20.glGetUniformLocation(filter.program, "texelWidthOffset")
        var texelHeightOffsetLocation = GLES20.glGetUniformLocation(filter.program, "texelHeightOffset")
        filter.setFloat(texelWidthOffsetLocation, ratio / mOutputWidth)
        filter.setFloat(texelHeightOffsetLocation, 0f)

        ratio = verticalTexelOffsetRatio
        filter = filters!![1]
        texelWidthOffsetLocation = GLES20.glGetUniformLocation(filter.program, "texelWidthOffset")
        texelHeightOffsetLocation = GLES20.glGetUniformLocation(filter.program, "texelHeightOffset")
        filter.setFloat(texelWidthOffsetLocation, 0f)
        filter.setFloat(texelHeightOffsetLocation, ratio / mOutputHeight)
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        initTexelOffsets()

    }
}
