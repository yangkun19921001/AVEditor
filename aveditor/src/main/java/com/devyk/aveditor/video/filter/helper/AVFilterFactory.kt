package com.devyk.aveditor.video.filter.helper

import android.content.Context
import android.graphics.PointF
import com.devyk.aveditor.R
import com.devyk.aveditor.video.filter.gpuimage.*
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.magic.*

/**
 * <pre>
 *     author  : devyk on 2020-08-10 23:41
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVFilterFactory
 * </pre>
 */
public class AVFilterFactory<T : GPUImageFilter> {

    companion object {
        private var filterType = AVFilterType.NONE


        fun getFilters(
            context: Context,
            type: AVFilterType?

        ): GPUImageFilter? {
            type?.let { type ->
                filterType = type
            }
            val gpuImageFilter = when (filterType) {
                AVFilterType.WHITECAT -> return AVToolsWhiteCatFilter(context)
                AVFilterType.BLACKCAT -> return AVToolsBlackCatFilter(context)
                AVFilterType.SKINWHITEN -> return AVToolsSkinWhitenFilter(context)
                AVFilterType.BEAUTY -> return AVToolsBeautyFilter(context)
                AVFilterType.ROMANCE -> return AVToolsRomanceFilter(context)
                AVFilterType.SAKURA -> return AVToolsSakuraFilter(context)
                AVFilterType.AMARO -> return AVToolsAmaroFilter(context)
                AVFilterType.WALDEN -> return AVToolsWaldenFilter(context)
                AVFilterType.ANTIQUE -> return AVToolsAntiqueFilter(context)
                AVFilterType.CALM -> return AVToolsCalmFilter(context)
                AVFilterType.BRANNAN -> return AVToolsBrannanFilter(context)
                AVFilterType.BROOKLYN -> return AVToolsBrooklynFilter(context)
                AVFilterType.EARLYBIRD -> return AVToolsEarlyBirdFilter(context)
                AVFilterType.FREUD -> return AVToolsFreudFilter(context)
                AVFilterType.HEFE -> return AVToolsHefeFilter(context)
                AVFilterType.HUDSON -> return AVToolsHudsonFilter(context)
                AVFilterType.INKWELL -> return AVToolsInkwellFilter(context)
                AVFilterType.KEVIN -> return AVToolsKevinFilter(context)
                AVFilterType.LOMO -> return AVToolsLomoFilter(context)
                AVFilterType.N1977 -> return AVToolsN1977Filter(context)
                AVFilterType.NASHVILLE -> return AVToolsNashvilleFilter(context)
                AVFilterType.PIXAR -> return AVToolsPixarFilter(context)
                AVFilterType.RISE -> return AVToolsRiseFilter(context)
                AVFilterType.SIERRA -> return AVToolsSierraFilter(context)
                AVFilterType.SUTRO -> return AVToolsSutroFilter(context)
                AVFilterType.TOASTER2 -> return AVToolsToasterFilter(context)
                AVFilterType.VALENCIA -> return AVToolsValenciaFilter(context)
                AVFilterType.XPROII -> return AVToolsXproIIFilter(context)
                AVFilterType.EVERGREEN -> return AVToolsEvergreenFilter(context)
                AVFilterType.HEALTHY -> return AVToolsHealthyFilter(context)
                AVFilterType.COOL -> return AVToolsCoolFilter(context)
                AVFilterType.EMERALD -> return AVToolsEmeraldFilter(context)
                AVFilterType.LATTE -> return AVToolsLatteFilter(context)
                AVFilterType.WARM -> return AVToolsWarmFilter(context)
                AVFilterType.TENDER -> return AVToolsTenderFilter(context)
                AVFilterType.SWEETS -> return AVToolsSweetsFilter(context)
                AVFilterType.NOSTALGIA -> return AVToolsNostalgiaFilter(context)
                AVFilterType.FAIRYTALE -> return AVToolsFairytaleFilter(context)
                AVFilterType.SUNRISE -> return AVToolsSunriseFilter(context)
                AVFilterType.SUNSET -> return AVToolsSunsetFilter(context)
                AVFilterType.CRAYON -> return AVToolsCrayonFilter(context)
                AVFilterType.SKETCH -> return AVToolsSketchFilter(context)
                //image adjust
                AVFilterType.BRIGHTNESS -> return GPUImageBrightnessFilter(context, 1.5f)
                AVFilterType.CONTRAST -> return GPUImageContrastFilter(context, 2.0f)
                AVFilterType.EXPOSURE -> return GPUImageExposureFilter(context)
                AVFilterType.HUE -> return GPUImageHueFilter(context)
                AVFilterType.SATURATION -> return GPUImageSaturationFilter(context, 1.0f)
                AVFilterType.SHARPEN -> return GPUImageSharpenFilter(context)
                AVFilterType.LEVELS -> return GPUImageLevelsFilter(context)


                AVFilterType.FILTER_GROUP -> return GPUImageFilterGroup(
                    context,
                    arrayListOf(
                        GPUImageContrastFilter(context),
                        GPUImageDirectionalSobelEdgeDetectionFilter(context),
                        GPUImageGrayscaleFilter(context)
                    )
                )

                AVFilterType.GAMMA -> return GPUImageGammaFilter(context, 2.0f)
                AVFilterType.INVERT -> return GPUImageColorInvertFilter(context)
                AVFilterType.GRAYSCALE -> return GPUImageGrayscaleFilter(context)
                AVFilterType.SEPIA -> return GPUImageSepiaToneFilter(context)
                AVFilterType.SOBEL_EDGE_DETECTION -> return GPUImageSobelEdgeDetectionFilter(context)
                AVFilterType.THRESHOLD_EDGE_DETECTION -> return GPUImageThresholdEdgeDetectionFilter(context)
                AVFilterType.THREE_X_THREE_CONVOLUTION -> return GPUImage3x3ConvolutionFilter(context)
                AVFilterType.EMBOSS -> return GPUImageEmbossFilter(context)
                AVFilterType.POSTERIZE -> return GPUImagePosterizeFilter(context)

                AVFilterType.HIGHLIGHT_SHADOW -> return GPUImageHighlightShadowFilter(
                    context,
                    0.0f,
                    1.0f
                )
                AVFilterType.MONOCHROME -> return GPUImageMonochromeFilter(
                    context,
                    1.0f, floatArrayOf(0.6f, 0.45f, 0.3f, 1.0f)
                )
                AVFilterType.OPACITY -> GPUImageOpacityFilter(context, 1.0f)
                AVFilterType.RGB -> GPUImageRGBFilter(context, 1.0f, 1.0f, 1.0f)
                AVFilterType.WHITE_BALANCE -> return GPUImageWhiteBalanceFilter(
                    context,
                    5000.0f,
                    0.0f
                )
                AVFilterType.VIGNETTE -> return GPUImageVignetteFilter(
                    context,
                    PointF(0.5f, 0.5f),
                    floatArrayOf(0.0f, 0.0f, 0.0f),
                    0.3f,
                    0.75f
                )
                AVFilterType.TONE_CURVE -> return GPUImageToneCurveFilter(context).apply {
                    setFromCurveFileInputStream(context.resources.openRawResource(R.raw.tone_cuver_sample))
                }
                AVFilterType.LUMINANCE -> return GPUImageLuminanceFilter(context)
                AVFilterType.LUMINANCE_THRESHSOLD -> return GPUImageLuminanceThresholdFilter(context, 0.5f)
                AVFilterType.GAUSSIAN_BLUR -> return GPUImageGaussianBlurFilter(context)
                AVFilterType.CROSSHATCH -> return GPUImageCrosshatchFilter(context)
                AVFilterType.BOX_BLUR -> return GPUImageBoxBlurFilter(context)
                AVFilterType.CGA_COLORSPACE -> return GPUImageCGAColorspaceFilter(context)
                AVFilterType.DILATION -> return GPUImageDilationFilter(context)
                AVFilterType.KUWAHARA -> return GPUImageKuwaharaFilter(context)
                AVFilterType.RGB_DILATION -> return GPUImageRGBDilationFilter(context)
                AVFilterType.TOON -> return GPUImageToonFilter(context)
                AVFilterType.SMOOTH_TOON -> return GPUImageSmoothToonFilter(context)
                AVFilterType.BULGE_DISTORTION -> return GPUImageBulgeDistortionFilter(context)
                AVFilterType.GLASS_SPHERE -> return GPUImageGlassSphereFilter(context)
                AVFilterType.HAZE -> return GPUImageHazeFilter(context)
                AVFilterType.LAPLACIAN -> return GPUImageLaplacianFilter(context)
                AVFilterType.NON_MAXIMUM_SUPPRESSION -> return GPUImageNonMaximumSuppressionFilter(context)
                AVFilterType.SPHERE_REFRACTION -> return GPUImageSphereRefractionFilter(context)
                AVFilterType.SWIRL -> return GPUImageSwirlFilter(context)
                AVFilterType.WEAK_PIXEL_INCLUSION -> return GPUImageWeakPixelInclusionFilter(context)
                AVFilterType.FALSE_COLOR -> return GPUImageFalseColorFilter(context)
                AVFilterType.COLOR_BALANCE -> return GPUImageColorBalanceFilter(context)
                AVFilterType.LEVELS_FILTER_MIN -> return GPUImageLevelsFilter(context)
                AVFilterType.HALFTONE -> return GPUImageHalftoneFilter(context)
                AVFilterType.BILATERAL_BLUR -> return GPUImageBilateralBlurFilter(context)
                AVFilterType.ZOOM_BLUR -> return GPUImageZoomBlurFilter(context)
                AVFilterType.TRANSFORM2D -> return GPUImageTransformFilter(context)
                AVFilterType.SOLARIZE -> return GPUImageSolarizeFilter(context)
                AVFilterType.VIBRANCE -> return GPUImageVibranceFilter(context)
                else -> return getExFilter()
                //TODO 会导致崩溃
//            AVFilterType.IMAGE_ADJUST -> return AVToolsImageAdjustFilter(context)
            }
            return null
        }

        fun <T : GPUImageFilter> getAdjustFilter(
            gpuimage: T?
        ): AVGPUImageFliterTools.FilterAdjuster? {
            gpuimage?.let {gpuimage->
                if (AVGPUImageFliterTools.FilterAdjuster(gpuimage).canAdjust()) {
                    return AVGPUImageFliterTools.FilterAdjuster(gpuimage)
                }
            }
           return null
        }

        public fun getExFilter(): GPUImageFilter? = null

        fun getCurrentFilterType(): AVFilterType {
            return filterType
        }
    }

}



