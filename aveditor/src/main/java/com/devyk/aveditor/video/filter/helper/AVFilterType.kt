package com.devyk.aveditor.video.filter.helper

/**
 * <pre>
 *     author  : devyk on 2020-08-10 23:44
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVFilterType
 * </pre>
 */
public enum class AVFilterType(name: String = "test") {
    //MagicCamera
    NONE("原图"),
    FAIRYTALE("童话"),
    SUNRISE("日出"),
    SUNSET("SUNSET"),
    WHITECAT("白猫"),
    BLACKCAT("黑猫"),
    SKINWHITEN("美白"),
    BEAUTY("美颜"),
    HEALTHY("健康"),
    SWEETS("甜品"),
    ROMANCE("浪漫"),
    SAKURA("樱花"),
    WARM("温暖"),
    ANTIQUE("复古"),
    NOSTALGIA("怀旧"),
    CALM("平静"),
    LATTE("拿铁"),
    TENDER("温柔"),
    COOL("冰冷"),
    EMERALD("祖母绿"),
    EVERGREEN("常青"),
    CRAYON("蜡笔"),
    SKETCH("素描"),
    AMARO("AMARO"),
    BRANNAN("BRANNAN"),
    BROOKLYN("BROOKLYN"),
    EARLYBIRD("EARLYBIRD"),
    FREUD("FREUD"),
    HEFE("HEFE"),
    HUDSON("HUDSON"),
    INKWELL("INKWELL"),
    KEVIN("KEVIN"),
    LOMO("LOMO"),
    N1977("N1977"),
    NASHVILLE("NASHVILLE"),
    PIXAR("PIXAR"),
    RISE("RISE"),
    SIERRA("SIERRA"),
    SUTRO("SUTRO"),
    TOASTER2("TOASTER2"),
    VALENCIA("VALENCIA"),
    WALDEN("WALDEN"),
    XPROII("XPROII"),


    //GPUImageFilter
    CONTRAST("对比度"),
    BRIGHTNESS("亮度"),
    EXPOSURE("曝光"),
    HUE("色调"),
    SATURATION("饱和度"),
    SHARPEN("SHARPEN"),
    LEVELS("Levels"),
    FILTER_GROUP("FILTER_GROUP"),

    GAMMA("GAMMA"),

    INVERT("反转"),

    GRAYSCALE("灰度"),

    SEPIA("棕黑色"),

    SOBEL_EDGE_DETECTION("边缘"),

    THRESHOLD_EDGE_DETECTION("THRESHOLD_EDGE_DETECTION"),

    THREE_X_THREE_CONVOLUTION("THREE_X_THREE_CONVOLUTION"),

    EMBOSS("凹凸"),

    POSTERIZE("多色"),

    HIGHLIGHT_SHADOW("HIGHLIGHT_SHADOW"),

    MONOCHROME("单色"),

    OPACITY("OPACITY"),

    RGB("RGB"),

    WHITE_BALANCE("WHITE_BALANCE"),

    VIGNETTE("VIGNETTE"),

    TONE_CURVE("TONE_CURVE"),

    LUMINANCE("LUMINANCE"),

    LUMINANCE_THRESHSOLD("LUMINANCE_THRESHSOLD"),

    GAUSSIAN_BLUR("GAUSSIAN_BLUR"),

    CROSSHATCH("CROSSHATCH"),

    BOX_BLUR("BOX_BLUR"),

    CGA_COLORSPACE("CGA_COLORSPACE"),

    DILATION("DILATION"),

    KUWAHARA("KUWAHARA"),

    RGB_DILATION("RGB_DILATION"),

    TOON("TOON"),

    SMOOTH_TOON("SMOOTH_TOON"),

    BULGE_DISTORTION("BULGE_DISTORTION"),

    GLASS_SPHERE("GLASS_SPHERE"),

    HAZE("HAZE"),

    LAPLACIAN("LAPLACIAN"),

    NON_MAXIMUM_SUPPRESSION("NON_MAXIMUM_SUPPRESSION"),

    SPHERE_REFRACTION("SPHERE_REFRACTION"),

    SWIRL("SWIRL"),

    WEAK_PIXEL_INCLUSION("WEAK_PIXEL_INCLUSION"),

    FALSE_COLOR("FALSE_COLOR"),

    COLOR_BALANCE("COLOR_BALANCE"),

    LEVELS_FILTER_MIN("LEVELS_FILTER_MIN"),

    HALFTONE("HALFTONE"),

    BILATERAL_BLUR("v"),

    ZOOM_BLUR("ZOOM_BLUR"),

    TRANSFORM2D("TRANSFORM2D"),

    SOLARIZE("SOLARIZE"),

    VIBRANCE("VIBRANCE");


    //TODO 会导致 Crash
//    IMAGE_ADJUST("IMAGE_ADJUST");


    var filterNmae = name
}