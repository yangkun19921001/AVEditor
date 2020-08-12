package com.devyk.aveditor.video.filter.magic


import android.content.Context
import com.devyk.aveditor.video.filter.gpuimage.*
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import java.util.ArrayList

class AVToolsImageAdjustFilter(context: Context) : AVToolsBaseGroupFilter(context, initFilters()) {


    init {
        mContext = context.applicationContext
    }

    companion object {
        private var mContext: Context? = null
        public fun initFilters(): List<GPUImageFilter> {
            val filters = ArrayList<GPUImageFilter>()
            mContext?.let { context ->
                filters.add(GPUImageContrastFilter(context))
                filters.add(GPUImageBrightnessFilter(context))
                filters.add(GPUImageExposureFilter(context))
                filters.add(GPUImageHueFilter(context))
                filters.add(GPUImageSaturationFilter(context))
                filters.add(GPUImageSharpenFilter(context))
            }
            return filters
        }
    }


    fun setSharpness(range: Float) {
        (filters.get(5) as GPUImageSharpenFilter).setSharpness(range)
    }





    fun setHue(range: Float) {
        (filters.get(3) as GPUImageHueFilter).setHue(range)
    }

    fun setBrightness(range: Float) {
        (filters.get(1) as GPUImageBrightnessFilter).setBrightness(range)
    }

    fun setContrast(range: Float) {
        (filters.get(0) as GPUImageContrastFilter).setContrast(range)
    }

    fun setSaturation(range: Float) {
        (filters.get(4) as GPUImageSaturationFilter).setSaturation(range)
    }

    fun setExposure(range: Float) {
        (filters.get(2) as GPUImageExposureFilter).setExposure(range)
    }
}
