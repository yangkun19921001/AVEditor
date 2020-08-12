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
 * The haze filter can be used to add or remove haze.
 *
 *
 * This is similar to a UV filter.
 */
class GPUImageHazeFilter @JvmOverloads constructor(context: Context,
    private var distance: Float = 0.2f,
    private var slope: Float = 0.0f
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, HAZE_FRAGMENT_SHADER) {
    private var distanceLocation: Int = 0
    private var slopeLocation: Int = 0

    override fun onInit() {
        super.onInit()
        distanceLocation = GLES20.glGetUniformLocation(program, "distance")
        slopeLocation = GLES20.glGetUniformLocation(program, "slope")
    }

    override fun onInitialized() {
        super.onInitialized()
        setDistance(distance)
        setSlope(slope)
    }

    /**
     * Strength of the color applied. Default 0. Values between -.3 and .3 are best.
     *
     * @param distance -0.3 to 0.3 are best, default 0
     */
    fun setDistance(distance: Float) {
        this.distance = distance
        setFloat(distanceLocation, distance)
    }

    /**
     * Amount of color change. Default 0. Values between -.3 and .3 are best.
     *
     * @param slope -0.3 to 0.3 are best, default 0
     */
    fun setSlope(slope: Float) {
        this.slope = slope
        setFloat(slopeLocation, slope)
    }

    companion object {
        val HAZE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform lowp float distance;\n" +
                "uniform highp float slope;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "	//todo reconsider precision modifiers	 \n" +
                "	 highp vec4 color = vec4(1.0);//todo reimplement as a parameter\n" +
                "\n" +
                "	 highp float  d = textureCoordinate.y * slope  +  distance; \n" +
                "\n" +
                "	 highp vec4 c = texture2D(inputImageTexture, textureCoordinate) ; // consider using unpremultiply\n" +
                "\n" +
                "	 c = (c - d * color) / (1.0 -d);\n" +
                "\n" +
                "	 gl_FragColor = c; //consider using premultiply(c);\n" +
                "}\n"
    }
}
