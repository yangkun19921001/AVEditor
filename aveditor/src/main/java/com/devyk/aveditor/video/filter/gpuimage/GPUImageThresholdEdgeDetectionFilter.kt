package com.devyk.aveditor.video.filter.gpuimage

import android.content.Context

/**
 * Applies sobel edge detection on the image.
 */
class GPUImageThresholdEdgeDetectionFilter(context: Context) : GPUImageFilterGroup(context) {
    init {
        addFilter(GPUImageGrayscaleFilter(context))
        addFilter(GPUImageSobelThresholdFilter(context))
    }

    fun setLineSize(size: Float) {
        (filters!![1] as GPUImage3x3TextureSamplingFilter).setLineSize(size)
    }

    fun setThreshold(threshold: Float) {
        (filters!![1] as GPUImageSobelThresholdFilter).setThreshold(threshold)
    }
}
