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
import android.graphics.Point
import android.graphics.PointF
import android.opengl.GLES20
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter

import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator

class GPUImageToneCurveFilter(context: Context) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, TONE_CURVE_FRAGMENT_SHADER) {

    private val toneCurveTexture = intArrayOf(OpenGLUtils.NO_TEXTURE)
    private var toneCurveTextureUniformLocation: Int = 0

    private var rgbCompositeControlPoints: Array<PointF?> ?=null
    private var redControlPoints: Array<PointF?>?=null
    private var greenControlPoints: Array<PointF?>?=null
    private var blueControlPoints: Array<PointF?>?=null
    private var rgbCompositeCurve: ArrayList<Float>? = null
    private var redCurve: ArrayList<Float>? = null
    private var greenCurve: ArrayList<Float>? = null
    private var blueCurve: ArrayList<Float>? = null

    init {

//        val defaultCurvePoints = arrayOf(PointF(0.0f, 0.0f), PointF(0.5f, 0.5f), PointF(1.0f, 1.0f))
        val defaultCurvePoints = getDeflautPoints()
        rgbCompositeControlPoints = defaultCurvePoints
        redControlPoints = defaultCurvePoints
        greenControlPoints = defaultCurvePoints
        blueControlPoints = defaultCurvePoints
    }

    public fun getDeflautPoints():Array<PointF?> ?{
        val arrayOfNulls = arrayOfNulls<PointF>(3)
        arrayOfNulls[0] = PointF(0.0f, 0.0f)
        arrayOfNulls[1] = PointF(0.5f, 0.5f)
        arrayOfNulls[2] = PointF(1.0f, 1.0f)
        return arrayOfNulls
    }

    override fun onInit() {
        super.onInit()
        toneCurveTextureUniformLocation = GLES20.glGetUniformLocation(program, "toneCurveTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glGenTextures(1, toneCurveTexture, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toneCurveTexture[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
    }

    override fun onInitialized() {
        super.onInitialized()
        setRgbCompositeControlPoints(rgbCompositeControlPoints)
        setRedControlPoints(redControlPoints)
        setGreenControlPoints(greenControlPoints)
        setBlueControlPoints(blueControlPoints)
    }

    protected override fun onDrawArraysPre() {
        if (toneCurveTexture[0] != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toneCurveTexture[0])
            GLES20.glUniform1i(toneCurveTextureUniformLocation, 3)
        }
    }

    fun setFromCurveFileInputStream(input: InputStream) {
        try {
            val version = readShort(input).toInt()
            val totalCurves = readShort(input).toInt()

            val curves = ArrayList<Array<PointF?>>(totalCurves)
            val pointRate = 1.0f / 255

            for (i in 0 until totalCurves) {
                // 2 bytes, Count of points in the curve (short integer from 2...19)
                val pointCount = readShort(input)

                val points = arrayOfNulls<PointF>(pointCount.toInt())

                // point count * 4
                // Curve points. Each curve point is a pair of short integers where
                // the first number is the output value (vertical coordinate on the
                // Curves dialog graph) and the second is the input value. All coordinates have range 0 to 255.
                for (j in 0 until pointCount) {
                    val y = readShort(input)
                    val x = readShort(input)

                    points[j] = PointF(x * pointRate, y * pointRate)
                }
                curves.add(points)
            }
            input.close()
            rgbCompositeControlPoints = curves[0]
            redControlPoints = curves[1]
            greenControlPoints = curves[2]
            blueControlPoints = curves[3]
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    private fun readShort(input: InputStream): Short {
        return (input.read() shl 8 or input.read()).toShort()
    }

    fun setRgbCompositeControlPoints(points: Array<PointF?>?) {
        if (points != null) {
            rgbCompositeControlPoints = points
        }
        rgbCompositeCurve = createSplineCurve(rgbCompositeControlPoints)
        updateToneCurveTexture()
    }

    fun setRedControlPoints(points: Array<PointF?>?) {
        if (points != null) {
            redControlPoints = points
        }
        redCurve = createSplineCurve(redControlPoints)
        updateToneCurveTexture()
    }

    fun setGreenControlPoints(points: Array<PointF?>?) {
        if (points != null) {
            greenControlPoints = points
        }
        greenCurve = createSplineCurve(greenControlPoints)
        updateToneCurveTexture()
    }

    fun setBlueControlPoints(points: Array<PointF?>?) {
        if (points != null) {
            blueControlPoints = points
        }
        blueCurve = createSplineCurve(blueControlPoints)
        updateToneCurveTexture()
    }

    private fun updateToneCurveTexture() {
        runOnDraw(Runnable {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toneCurveTexture[0])

            if (redCurve!!.size >= 256 && greenCurve!!.size >= 256 && blueCurve!!.size >= 256 && rgbCompositeCurve!!.size >= 256) {
                val toneCurveByteArray = ByteArray(256 * 4)
                for (currentCurveIndex in 0..255) {
                    // BGRA for upload to texture
                    toneCurveByteArray[currentCurveIndex * 4 + 2] = (Math.min(
                        Math.max(
                            currentCurveIndex.toFloat() + blueCurve!![currentCurveIndex] + rgbCompositeCurve!![currentCurveIndex],
                            0f
                        ), 255f
                    ).toInt() and 0xff).toByte()
                    toneCurveByteArray[currentCurveIndex * 4 + 1] = (Math.min(
                        Math.max(
                            currentCurveIndex.toFloat() + greenCurve!![currentCurveIndex] + rgbCompositeCurve!![currentCurveIndex],
                            0f
                        ), 255f
                    ).toInt() and 0xff).toByte()
                    toneCurveByteArray[currentCurveIndex * 4] = (Math.min(
                        Math.max(
                            currentCurveIndex.toFloat() + redCurve!![currentCurveIndex] + rgbCompositeCurve!![currentCurveIndex],
                            0f
                        ), 255f
                    ).toInt() and 0xff).toByte()
                    toneCurveByteArray[currentCurveIndex * 4 + 3] = 0xff.toByte()
                }

                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_RGBA,
                    256 /*width*/,
                    1 /*height*/,
                    0,
                    GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE,
                    ByteBuffer.wrap(toneCurveByteArray)
                )
            }
            //        Buffer pixels!
            //        GLES20.glTexImage2D(int target,
            //            int level,
            //            int internalformat,
            //            int width,
            //            int height,
            //            int border,
            //            int format,
            //            int type,
            //            java.nio.Buffer pixels);
        })
    }

    private fun createSplineCurve(points: Array<PointF?>?): ArrayList<Float>? {
        if (points == null || points.size <= 0) {
            return null
        }

        // Sort the array
        val pointsSorted = points.clone()
        Arrays.sort(pointsSorted) { point1, point2 ->
            if (point1?.x!! < point2?.x!!) {
                -1
            } else if (point1.x > point2.x) {
                1
            } else {
                0
            }
        }

        // Convert from (0, 1) to (0, 255).
//        val convertedPoints = arrayOf<Point>()
        val convertedPoints = arrayOfNulls<Point>(points.size)
        for (i in points.indices) {
            val point = pointsSorted[i]
            convertedPoints[i] = Point((point!!.x * 255).toInt(), (point.y * 255).toInt())
        }

        val splinePoints = createSplineCurve2(convertedPoints)

        // If we have a first point like (0.3, 0) we'll be missing some points at the beginning
        // that should be 0.
        val firstSplinePoint = splinePoints!![0]
        if (firstSplinePoint.x > 0) {
            for (i in firstSplinePoint.x downTo 0) {
                splinePoints.add(0, Point(i, 0))
            }
        }

        // Insert points similarly at the end, if necessary.
        val lastSplinePoint = splinePoints[splinePoints.size - 1]
        if (lastSplinePoint.x < 255) {
            for (i in lastSplinePoint.x + 1..255) {
                splinePoints.add(Point(i, 255))
            }
        }

        // Prepare the spline points.
        val preparedSplinePoints = ArrayList<Float>(splinePoints.size)
        for (newPoint in splinePoints) {
            val origPoint = Point(newPoint.x, newPoint.x)

            var distance = Math.sqrt(
                Math.pow(
                    (origPoint.x - newPoint.x).toDouble(),
                    2.0
                ) + Math.pow((origPoint.y - newPoint.y).toDouble(), 2.0)
            ).toFloat()

            if (origPoint.y > newPoint.y) {
                distance = -distance
            }

            preparedSplinePoints.add(distance)
        }

        return preparedSplinePoints
    }

    private fun createSplineCurve2(points: Array<Point?>): ArrayList<Point>? {
        val sdA = createSecondDerivative(points)

        // Is [points count] equal to [sdA count]?
        //    int n = [points count];
        val n = sdA!!.size
        if (n < 1) {
            return null
        }
        val sd = DoubleArray(n)

        // From NSMutableArray to sd[n];
        for (i in 0 until n) {
            sd[i] = sdA[i]
        }


        val output = ArrayList<Point>(n + 1)

        for (i in 0 until n - 1) {
            val cur = points[i]
            val next = points[i + 1]

            for (x in cur!!.x until next!!.x) {
                val t = (x - cur.x).toDouble() / (next.x - cur.x)

                val a = 1 - t
                val h = (next.x - cur.x).toDouble()

                var y = a * cur.y + t * next.y + h * h / 6 * ((a * a * a - a) * sd[i] + (t * t * t - t) * sd[i + 1])

                if (y > 255.0) {
                    y = 255.0
                } else if (y < 0.0) {
                    y = 0.0
                }

                output.add(Point(x, Math.round(y).toInt()))
            }
        }

        // If the last point is (255, 255) it doesn't get added.
        if (output.size == 255) {
            output.add(points[points.size - 1]!!)
        }
        return output
    }

    private fun createSecondDerivative(points: Array<Point?>): ArrayList<Double>? {
        val n = points.size
        if (n <= 1) {
            return null
        }

        val matrix = Array(n) { DoubleArray(3) }
        val result = DoubleArray(n)
        matrix[0][1] = 1.0
        // What about matrix[0][1] and matrix[0][0]? Assuming 0 for now (Brad L.)
        matrix[0][0] = 0.0
        matrix[0][2] = 0.0

        for (i in 1 until n - 1) {
            val P1 = points[i - 1]
            val P2 = points[i]
            val P3 = points[i + 1]

            matrix[i][0] = (P2!!.x - P1!!.x).toDouble() / 6
            matrix[i][1] = (P3!!.x - P1.x).toDouble() / 3
            matrix[i][2] = (P3.x - P2.x).toDouble() / 6
            result[i] = (P3.y - P2.y).toDouble() / (P3.x - P2.x) - (P2.y - P1.y).toDouble() / (P2.x - P1.x)
        }

        // What about result[0] and result[n-1]? Assuming 0 for now (Brad L.)
        result[0] = 0.0
        result[n - 1] = 0.0

        matrix[n - 1][1] = 1.0
        // What about matrix[n-1][0] and matrix[n-1][2]? For now, assuming they are 0 (Brad L.)
        matrix[n - 1][0] = 0.0
        matrix[n - 1][2] = 0.0

        // solving pass1 (up->down)
        for (i in 1 until n) {
            val k = matrix[i][0] / matrix[i - 1][1]
            matrix[i][1] -= k * matrix[i - 1][2]
            matrix[i][0] = 0.0
            result[i] -= k * result[i - 1]
        }
        // solving pass2 (down->up)
        for (i in n - 2 downTo 0) {
            val k = matrix[i][2] / matrix[i + 1][1]
            matrix[i][1] -= k * matrix[i + 1][0]
            matrix[i][2] = 0.0
            result[i] -= k * result[i + 1]
        }

        val output = ArrayList<Double>(n)
        for (i in 0 until n) output.add(result[i] / matrix[i][1])

        return output
    }

    companion object {
        val TONE_CURVE_FRAGMENT_SHADER = "" +
                " varying highp vec2 textureCoordinate;\n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D toneCurveTexture;\n" +
                "\n" +
                " void main()\n" +
                " {\n" +
                "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "     lowp float redCurveValue = texture2D(toneCurveTexture, vec2(textureColor.r, 0.0)).r;\n" +
                "     lowp float greenCurveValue = texture2D(toneCurveTexture, vec2(textureColor.g, 0.0)).g;\n" +
                "     lowp float blueCurveValue = texture2D(toneCurveTexture, vec2(textureColor.b, 0.0)).b;\n" +
                "\n" +
                "     gl_FragColor = vec4(redCurveValue, greenCurveValue, blueCurveValue, textureColor.a);\n" +
                " }"
    }
}
