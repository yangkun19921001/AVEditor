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
import android.graphics.PointF
import android.opengl.GLES20
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter

class GPUImageBulgeDistortionFilter @JvmOverloads constructor(
    context: Context,
    private var radius: Float = 0.25f,
    private var scale: Float = 0.5f,
    private var center: PointF? = PointF(0.5f, 0.5f)
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, BULGE_FRAGMENT_SHADER) {
    private var scaleLocation: Int = 0
    private var radiusLocation: Int = 0
    private var centerLocation: Int = 0
    private var aspectRatio: Float = 0.toFloat()
    private var aspectRatioLocation: Int = 0

    override fun onInit() {
        super.onInit()
        scaleLocation = GLES20.glGetUniformLocation(program, "scale")
        radiusLocation = GLES20.glGetUniformLocation(program, "radius")
        centerLocation = GLES20.glGetUniformLocation(program, "center")
        aspectRatioLocation = GLES20.glGetUniformLocation(program, "aspectRatio")
    }

    override fun onInitialized() {
        super.onInitialized()
        setAspectRatio(aspectRatio)
        setRadius(radius)
        setScale(scale)
        setCenter(center)
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        aspectRatio = height.toFloat() / width
        setAspectRatio(aspectRatio)
        super.onInputSizeChanged(width, height)
    }


    private fun setAspectRatio(aspectRatio: Float) {
        this.aspectRatio = aspectRatio
        setFloat(aspectRatioLocation, aspectRatio)
    }

    /**
     * The radius of the distortion, ranging from 0.0 to 1.0, with a default of 0.25
     *
     * @param radius from 0.0 to 1.0, default 0.25
     */
    fun setRadius(radius: Float) {
        this.radius = radius
        setFloat(radiusLocation, radius)
    }

    /**
     * The amount of distortion to apply, from -1.0 to 1.0, with a default of 0.5
     *
     * @param scale from -1.0 to 1.0, default 0.5
     */
    fun setScale(scale: Float) {
        this.scale = scale
        setFloat(scaleLocation, scale)
    }

    /**
     * The center about which to apply the distortion, with a default of (0.5, 0.5)
     *
     * @param center default (0.5, 0.5)
     */
    fun setCenter(center: PointF?) {
        this.center = center
        center?.let { setPoint(centerLocation, it) }
    }

    companion object {
        val BULGE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform highp float aspectRatio;\n" +
                "uniform highp vec2 center;\n" +
                "uniform highp float radius;\n" +
                "uniform highp float scale;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "highp float dist = distance(center, textureCoordinateToUse);\n" +
                "textureCoordinateToUse = textureCoordinate;\n" +
                "\n" +
                "if (dist < radius)\n" +
                "{\n" +
                "textureCoordinateToUse -= center;\n" +
                "highp float percent = 1.0 - ((radius - dist) / radius) * scale;\n" +
                "percent = percent * percent;\n" +
                "\n" +
                "textureCoordinateToUse = textureCoordinateToUse * percent;\n" +
                "textureCoordinateToUse += center;\n" +
                "}\n" +
                "\n" +
                "gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse );    \n" +
                "}\n"
    }
}
