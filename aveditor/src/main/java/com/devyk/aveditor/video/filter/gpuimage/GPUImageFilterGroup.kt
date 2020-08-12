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

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.utils.Rotation
import com.devyk.aveditor.utils.TextureRotationUtil
import com.devyk.aveditor.utils.TextureRotationUtil.CUBE
import com.devyk.aveditor.utils.TextureRotationUtil.TEXTURE_NO_ROTATION
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.ArrayList

/**
 * Resembles a filter that consists of multiple filters applied after each
 * other.
 */
open class GPUImageFilterGroup
/**
 * Instantiates a new GPUImageFilterGroup with the given filters.
 *
 * @param filters the filters which represent this filter
 */
@JvmOverloads constructor(context: Context, filters: ArrayList<GPUImageFilter>? = null) : GPUImageFilter(context) {

    protected var filters: ArrayList<GPUImageFilter>? = null
    private var mergedFilters: MutableList<GPUImageFilter>? = null
    private var frameBuffers: IntArray? = null
    private var frameBufferTextures: IntArray? = null

    private val glCubeBuffer: FloatBuffer
    private val glTextureBuffer: FloatBuffer
    private val glTextureFlipBuffer: FloatBuffer

    init {
        this.filters = filters
        if (this.filters == null) {
            this.filters = ArrayList<GPUImageFilter>()
        } else {
            updateMergedFilters()
        }
        glCubeBuffer = ByteBuffer.allocateDirect(CUBE.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        glCubeBuffer.put(CUBE).position(0)

        glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        glTextureBuffer.put(TEXTURE_NO_ROTATION).position(0)

        val flipTexture = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)
        glTextureFlipBuffer = ByteBuffer.allocateDirect(flipTexture.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        glTextureFlipBuffer.put(flipTexture).position(0)
    }

    fun addFilter(aFilter: GPUImageFilter?) {
        if (aFilter == null) {
            return
        }
        filters!!.add(aFilter)
        updateMergedFilters()
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter#onInit()
     */
    override fun onInit() {
        super.onInit()
        for (filter in filters!!) {
            filter.isNeedInit()
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter#onDestroy()
     */
    override fun onDestroy() {
        destroyFramebuffers()
        for (filter in filters!!) {
            filter.destroy()
        }
        super.onDestroy()
    }

    private fun destroyFramebuffers() {
        if (frameBufferTextures != null) {
            GLES20.glDeleteTextures(frameBufferTextures!!.size, frameBufferTextures, 0)
            frameBufferTextures = null
        }
        if (frameBuffers != null) {
            GLES20.glDeleteFramebuffers(frameBuffers!!.size, frameBuffers, 0)
            frameBuffers = null
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter#onOutputSizeChanged(int,
     * int)
     */

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        if (frameBuffers != null) {
            destroyFramebuffers()
        }

        var size = filters!!.size
        for (i in 0 until size) {
            filters!![i].onInputSizeChanged(width, height)
        }

        if (mergedFilters != null && mergedFilters!!.size > 0) {
            size = mergedFilters!!.size
            frameBuffers = IntArray(size - 1)
            frameBufferTextures = IntArray(size - 1)

            for (i in 0 until size - 1) {
                GLES20.glGenFramebuffers(1, frameBuffers, i)
                GLES20.glGenTextures(1, frameBufferTextures, i)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTextures!![i])
                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
                )
                GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
                )
                GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat()
                )
                GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
                )
                GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
                )

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers!![i])
                GLES20.glFramebufferTexture2D(
                    GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, frameBufferTextures!![i], 0
                )

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            }
        }

    }


    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter#onDraw(int,
     * java.nio.FloatBuffer, java.nio.FloatBuffer)
     */
    @SuppressLint("WrongCall")
    fun onDraw(
        textureId: Int, cubeBuffer: FloatBuffer,
        textureBuffer: FloatBuffer
    ) {
        runPendingOnDrawTasks()
        if (!isInitialized || frameBuffers == null || frameBufferTextures == null) {
            return
        }
        if (mergedFilters != null) {
            val size = mergedFilters!!.size
            var previousTexture = textureId
            for (i in 0 until size) {
                val filter = mergedFilters!![i]
                val isNotLast = i < size - 1
                if (isNotLast) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers!![i])
                    GLES20.glClearColor(0f, 0f, 0f, 0f)
                }

                if (i == 0) {
                    filter.onDrawFrame(previousTexture, cubeBuffer, textureBuffer)
                } else if (i == size - 1) {
                    filter.onDrawFrame(
                        previousTexture,
                        glCubeBuffer,
                        if (size % 2 == 0) glTextureFlipBuffer else glTextureBuffer
                    )
                } else {
                    filter.onDrawFrame(previousTexture, glCubeBuffer, glTextureBuffer)
                }

                if (isNotLast) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                    previousTexture = frameBufferTextures!![i]
                }
            }
        }
    }



    fun getMergedFilters(): List<GPUImageFilter>? {
        return mergedFilters
    }

    fun updateMergedFilters() {
        if (filters == null) {
            return
        }

        if (mergedFilters == null) {
            mergedFilters = ArrayList<GPUImageFilter>()
        } else {
            mergedFilters!!.clear()
        }

        var filters: List<GPUImageFilter>?
        for (filter in this.filters!!) {
            if (filter is GPUImageFilterGroup) {
                (filter as GPUImageFilterGroup).updateMergedFilters()
                filters = (filter as GPUImageFilterGroup).getMergedFilters()
                if (filters == null || filters.isEmpty())
                    continue
                mergedFilters!!.addAll(filters)
                continue
            }
            mergedFilters!!.add(filter)
        }
    }
}
/**
 * Instantiates a new GPUImageFilterGroup with no filters.
 */
