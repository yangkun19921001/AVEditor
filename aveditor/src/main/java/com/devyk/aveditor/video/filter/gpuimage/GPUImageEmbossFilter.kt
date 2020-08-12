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
 * Applies an emboss effect to the image.<br></br>
 * <br></br>
 * Intensity ranges from 0.0 to 4.0, with 1.0 as the normal level
 */
class GPUImageEmbossFilter @JvmOverloads constructor(context: Context,private var intensity: Float = 1.0f) :
    GPUImage3x3ConvolutionFilter(context) {

    override fun onInit() {
        super.onInit()
    }

    override fun onInitialized() {
        super.onInitialized()
        setIntensity(intensity)
    }

    fun setIntensity(intensity: Float) {
        this.intensity = intensity
        setConvolutionKernel(
            floatArrayOf(
                intensity * -2.0f,
                -intensity,
                0.0f,
                -intensity,
                1.0f,
                intensity,
                0.0f,
                intensity,
                intensity * 2.0f
            )
        )
    }

    fun getIntensity(): Float {
        return intensity
    }
}