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
import android.graphics.Bitmap
import android.opengl.GLES20
import com.devyk.aveditor.utils.OpenGLUtils
import com.devyk.aveditor.utils.Rotation
import com.devyk.aveditor.utils.TextureRotationUtil
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter


import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class GPUImageTwoInputFilter(context: Context,vertexShader: String, fragmentShader: String) :
    GPUImageFilter(context,vertexShader, fragmentShader) {

    private var filterSecondTextureCoordinateAttribute: Int = 0
    private var filterInputTextureUniform2: Int = 0
    private var filterSourceTexture2 = OpenGLUtils.NO_TEXTURE
    private var texture2CoordinatesBuffer: ByteBuffer? = null
    private var bitmap: Bitmap? = null

    constructor(context: Context,fragmentShader: String) : this(context,VERTEX_SHADER, fragmentShader) {}

    init {
        setRotation(Rotation.NORMAL, false, false)
    }

    override fun onInit() {
        super.onInit()

        filterSecondTextureCoordinateAttribute = GLES20.glGetAttribLocation(program, "inputTextureCoordinate2")
        filterInputTextureUniform2 = GLES20.glGetUniformLocation(
            program,
            "inputImageTexture2"
        ) // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute)
    }

    override fun onInitialized() {
        super.onInitialized()
        if (bitmap != null && !bitmap!!.isRecycled) {
            setBitmap(bitmap)
        }
    }

    fun setBitmap(bitmap: Bitmap?) {
        if (bitmap != null && bitmap.isRecycled) {
            return
        }
        this.bitmap = bitmap
        if (this.bitmap == null) {
            return
        }
        runOnDraw(Runnable {
            if (filterSourceTexture2 == OpenGLUtils.NO_TEXTURE) {
                if (bitmap == null || bitmap.isRecycled) {
                    return@Runnable
                }
                GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
                filterSourceTexture2 = OpenGLUtils.loadTexture(bitmap, OpenGLUtils.NO_TEXTURE, false)
            }
        })
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    fun recycleBitmap() {
        if (bitmap != null && !bitmap!!.isRecycled) {
            bitmap!!.recycle()
            bitmap = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GLES20.glDeleteTextures(1, intArrayOf(filterSourceTexture2), 0)
        filterSourceTexture2 = OpenGLUtils.NO_TEXTURE
    }

    protected override fun onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterSourceTexture2)
        GLES20.glUniform1i(filterInputTextureUniform2, 3)

        texture2CoordinatesBuffer!!.position(0)
        GLES20.glVertexAttribPointer(
            filterSecondTextureCoordinateAttribute,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            texture2CoordinatesBuffer
        )
    }

    fun setRotation(rotation: Rotation, flipHorizontal: Boolean, flipVertical: Boolean) {
        val buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical)

        val bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder())
        val fBuffer = bBuffer.asFloatBuffer()
        fBuffer.put(buffer)
        fBuffer.flip()

        texture2CoordinatesBuffer = bBuffer
    }

    companion object {
        private val VERTEX_SHADER = "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                "attribute vec4 inputTextureCoordinate2;\n" +
                " \n" +
                "varying vec2 textureCoordinate;\n" +
                "varying vec2 textureCoordinate2;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = position;\n" +
                "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                "    textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
                "}"
    }
}
