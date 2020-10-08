package com.devyk.ikavedit.utils;

import com.devyk.aveditor.video.filter.helper.AVFilterType;
import com.devyk.ikavedit.R;



public class FilterTypeHelper {


    public static int FilterType2Thumb(AVFilterType filterType) {
        switch (filterType) {
            case NONE:
                return R.drawable.filter_thumb_original;
            case WHITECAT:
                return R.drawable.filter_thumb_whitecat;
            case BLACKCAT:
                return R.drawable.filter_thumb_blackcat;
            case ROMANCE:
                return R.drawable.filter_thumb_romance;
            case SAKURA:
                return R.drawable.filter_thumb_sakura;
            case AMARO:
                return R.drawable.filter_thumb_amoro;
            case BRANNAN:
                return R.drawable.filter_thumb_brannan;
            case BROOKLYN:
                return R.drawable.filter_thumb_brooklyn;
            case EARLYBIRD:
                return R.drawable.filter_thumb_earlybird;
            case FREUD:
                return R.drawable.filter_thumb_freud;
            case HEFE:
                return R.drawable.filter_thumb_hefe;
            case HUDSON:
                return R.drawable.filter_thumb_hudson;
            case INKWELL:
                return R.drawable.filter_thumb_inkwell;
            case KEVIN:
                return R.drawable.filter_thumb_kevin;
            case LOMO:
                return R.drawable.filter_thumb_lomo;
            case N1977:
                return R.drawable.filter_thumb_1977;
            case NASHVILLE:
                return R.drawable.filter_thumb_nashville;
            case PIXAR:
                return R.drawable.filter_thumb_piaxr;
            case RISE:
                return R.drawable.filter_thumb_rise;
            case SIERRA:
                return R.drawable.filter_thumb_sierra;
            case SUTRO:
                return R.drawable.filter_thumb_sutro;
            case TOASTER2:
                return R.drawable.filter_thumb_toastero;
            case VALENCIA:
                return R.drawable.filter_thumb_valencia;
            case WALDEN:
                return R.drawable.filter_thumb_walden;
            case XPROII:
                return R.drawable.filter_thumb_xpro;
            case ANTIQUE:
                return R.drawable.filter_thumb_antique;
            case SKINWHITEN:
                return R.drawable.filter_thumb_beauty;
            case BEAUTY:
            case BUFFING:
                return R.drawable.filter_thumb_beauty;
            case CALM:
                return R.drawable.filter_thumb_calm;
            case COOL:
                return R.drawable.filter_thumb_cool;
            case EMERALD:
                return R.drawable.filter_thumb_emerald;
            case EVERGREEN:
                return R.drawable.filter_thumb_evergreen;
            case FAIRYTALE:
                return R.drawable.filter_thumb_fairytale;
            case HEALTHY:
                return R.drawable.filter_thumb_healthy;
            case NOSTALGIA:
                return R.drawable.filter_thumb_nostalgia;
            case TENDER:
                return R.drawable.filter_thumb_tender;
            case SWEETS:
                return R.drawable.filter_thumb_sweets;
            case LATTE:
                return R.drawable.filter_thumb_latte;
            case WARM:
                return R.drawable.filter_thumb_warm;
            case SUNRISE:
                return R.drawable.filter_thumb_sunrise;
            case SUNSET:
                return R.drawable.filter_thumb_sunset;
            case CRAYON:
                return R.drawable.filter_thumb_crayon;
            case SKETCH:
                return R.drawable.filter_thumb_sketch;
            default:
                return R.drawable.filter_thumb_original;
        }
    }

    public static final AVFilterType[] FILTER_DATA = new AVFilterType[]{
            AVFilterType.NONE,
            AVFilterType.FAIRYTALE,
            AVFilterType.SUNRISE,
            AVFilterType.SUNSET,
            AVFilterType.WHITECAT,
            AVFilterType.BLACKCAT,
            AVFilterType.SKINWHITEN,
            AVFilterType.BEAUTY,
            AVFilterType.BUFFING,
            AVFilterType.HEALTHY,
            AVFilterType.SWEETS,
            AVFilterType.ROMANCE,
            AVFilterType.SAKURA,
            AVFilterType.WARM,
            AVFilterType.ANTIQUE,
            AVFilterType.NOSTALGIA,
            AVFilterType.CALM,
            AVFilterType.LATTE,
            AVFilterType.TENDER,
            AVFilterType.COOL,
            AVFilterType.EMERALD,
            AVFilterType.EVERGREEN,
            AVFilterType.CRAYON,
            AVFilterType.SKETCH,
            AVFilterType.AMARO,
            AVFilterType.BRANNAN,
            AVFilterType.BROOKLYN,
            AVFilterType.EARLYBIRD,
            AVFilterType.FREUD,
            AVFilterType.HEFE,
            AVFilterType.HUDSON,
            AVFilterType.INKWELL,
            AVFilterType.KEVIN,
            AVFilterType.LOMO,
            AVFilterType.N1977,
            AVFilterType.NASHVILLE,
            AVFilterType.PIXAR,
            AVFilterType.RISE,
            AVFilterType.SIERRA,
            AVFilterType.SUTRO,
            AVFilterType.TOASTER2,
            AVFilterType.VALENCIA,
            AVFilterType.WALDEN,
            AVFilterType.XPROII,

            AVFilterType.CONTRAST,
            AVFilterType.EXPOSURE,
            AVFilterType.HUE,
            AVFilterType.SATURATION,
            AVFilterType.SHARPEN,

            AVFilterType.LEVELS,
            AVFilterType.FILTER_GROUP,

            AVFilterType.GAMMA,

            AVFilterType.INVERT,

            AVFilterType.GRAYSCALE,

            AVFilterType.SEPIA,

            AVFilterType.SOBEL_EDGE_DETECTION,

            AVFilterType.THRESHOLD_EDGE_DETECTION,

            AVFilterType.THREE_X_THREE_CONVOLUTION,

            AVFilterType.EMBOSS,

            AVFilterType.POSTERIZE,

            AVFilterType.HIGHLIGHT_SHADOW,

            AVFilterType.MONOCHROME,

            AVFilterType.OPACITY,

            AVFilterType.RGB,

            AVFilterType.WHITE_BALANCE,

            AVFilterType.VIGNETTE,

            AVFilterType.TONE_CURVE,

            AVFilterType.LUMINANCE,

            AVFilterType.LUMINANCE_THRESHSOLD,

            AVFilterType.GAUSSIAN_BLUR,

            AVFilterType.CROSSHATCH,

            AVFilterType.BOX_BLUR,

            AVFilterType.CGA_COLORSPACE,

            AVFilterType.DILATION,

            AVFilterType.KUWAHARA,

            AVFilterType.RGB_DILATION,

            AVFilterType.TOON,

            AVFilterType.SMOOTH_TOON,

            AVFilterType.BULGE_DISTORTION,

            AVFilterType.GLASS_SPHERE,

            AVFilterType.HAZE,

            AVFilterType.LAPLACIAN,

            AVFilterType.NON_MAXIMUM_SUPPRESSION,

            AVFilterType.SPHERE_REFRACTION,

            AVFilterType.SWIRL,

            AVFilterType.WEAK_PIXEL_INCLUSION,

            AVFilterType.FALSE_COLOR,

            AVFilterType.COLOR_BALANCE,

            AVFilterType.LEVELS_FILTER_MIN,

            AVFilterType.HALFTONE,

            AVFilterType.BILATERAL_BLUR,

            AVFilterType.ZOOM_BLUR,

            AVFilterType.TRANSFORM2D,

            AVFilterType.SOLARIZE,

            AVFilterType.VIBRANCE


//            AVFilterType.IMAGE_ADJUST


    };
}
