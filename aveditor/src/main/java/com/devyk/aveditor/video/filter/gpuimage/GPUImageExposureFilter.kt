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
 * exposure: The adjusted exposure (-10.0 - 10.0, with 0.0 as the default)
 */
class GPUImageExposureFilter @JvmOverloads constructor(context: Context,private var exposure: Float = 1.0f) :
    GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, EXPOSURE_FRAGMENT_SHADER) {

    private var exposureLocation: Int = 0

    override fun onInit() {
        super.onInit()
        exposureLocation = GLES20.glGetUniformLocation(program, "exposure")
    }

    override fun onInitialized() {
        super.onInitialized()
        setExposure(exposure)
    }

    fun setExposure(exposure: Float) {
        this.exposure = exposure
        setFloat(exposureLocation, this.exposure)
    }

    companion object {
        val EXPOSURE_FRAGMENT_SHADER = "" +
                " varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform highp float exposure;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "     \n" +
                "     gl_FragColor = vec4(textureColor.rgb * pow(2.0, exposure), textureColor.w);\n" +
                " } "
    }
}
