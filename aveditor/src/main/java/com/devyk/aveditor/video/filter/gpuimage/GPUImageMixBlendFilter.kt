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

open class GPUImageMixBlendFilter @JvmOverloads constructor(context: Context,fragmentShader: String, private var mix: Float = 0.5f) :
    GPUImageTwoInputFilter(context,fragmentShader) {

    private var mixLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mixLocation = GLES20.glGetUniformLocation(program, "mixturePercent")
    }

    override fun onInitialized() {
        super.onInitialized()
        setMix(mix)
    }

    /**
     * @param mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
     */
    fun setMix(mix: Float) {
        this.mix = mix
        setFloat(mixLocation, this.mix)
    }
}
