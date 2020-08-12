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

class GPUImageGlassSphereFilter @JvmOverloads constructor(context: Context,
    private var center: PointF? = PointF(0.5f, 0.5f),
    private var radius: Float = 0.25f,
    private var refractiveIndex: Float = 0.71f
) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, SPHERE_FRAGMENT_SHADER) {
    private var centerLocation: Int = 0
    private var radiusLocation: Int = 0
    private var aspectRatio: Float = 0.toFloat()
    private var aspectRatioLocation: Int = 0
    private var refractiveIndexLocation: Int = 0

    override fun onInit() {
        super.onInit()
        centerLocation = GLES20.glGetUniformLocation(program, "center")
        radiusLocation = GLES20.glGetUniformLocation(program, "radius")
        aspectRatioLocation = GLES20.glGetUniformLocation(program, "aspectRatio")
        refractiveIndexLocation = GLES20.glGetUniformLocation(program, "refractiveIndex")
    }

    override fun onInitialized() {
        super.onInitialized()
        setAspectRatio(aspectRatio)
        setRadius(radius)
        setCenter(center)
        setRefractiveIndex(refractiveIndex)
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

    fun setRefractiveIndex(refractiveIndex: Float) {
        this.refractiveIndex = refractiveIndex
        setFloat(refractiveIndexLocation, refractiveIndex)
    }

    fun setCenter(center: PointF?) {
        this.center = center
        center?.let { setPoint(centerLocation, it) }
    }

    fun setRadius(radius: Float) {
        this.radius = radius
        setFloat(radiusLocation, radius)
    }

    companion object {
        val SPHERE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform highp vec2 center;\n" +
                "uniform highp float radius;\n" +
                "uniform highp float aspectRatio;\n" +
                "uniform highp float refractiveIndex;\n" +
                "// uniform vec3 lightPosition;\n" +
                "const highp vec3 lightPosition = vec3(-0.5, 0.5, 1.0);\n" +
                "const highp vec3 ambientLightPosition = vec3(0.0, 0.0, 1.0);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "highp float distanceFromCenter = distance(center, textureCoordinateToUse);\n" +
                "lowp float checkForPresenceWithinSphere = step(distanceFromCenter, radius);\n" +
                "\n" +
                "distanceFromCenter = distanceFromCenter / radius;\n" +
                "\n" +
                "highp float normalizedDepth = radius * sqrt(1.0 - distanceFromCenter * distanceFromCenter);\n" +
                "highp vec3 sphereNormal = normalize(vec3(textureCoordinateToUse - center, normalizedDepth));\n" +
                "\n" +
                "highp vec3 refractedVector = 2.0 * refract(vec3(0.0, 0.0, -1.0), sphereNormal, refractiveIndex);\n" +
                "refractedVector.xy = -refractedVector.xy;\n" +
                "\n" +
                "highp vec3 finalSphereColor = texture2D(inputImageTexture, (refractedVector.xy + 1.0) * 0.5).rgb;\n" +
                "\n" +
                "// Grazing angle lighting\n" +
                "highp float lightingIntensity = 2.5 * (1.0 - pow(clamp(dot(ambientLightPosition, sphereNormal), 0.0, 1.0), 0.25));\n" +
                "finalSphereColor += lightingIntensity;\n" +
                "\n" +
                "// Specular lighting\n" +
                "lightingIntensity  = clamp(dot(normalize(lightPosition), sphereNormal), 0.0, 1.0);\n" +
                "lightingIntensity  = pow(lightingIntensity, 15.0);\n" +
                "finalSphereColor += vec3(0.8, 0.8, 0.8) * lightingIntensity;\n" +
                "\n" +
                "gl_FragColor = vec4(finalSphereColor, 1.0) * checkForPresenceWithinSphere;\n" +
                "}\n"
    }
}
