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
    BUFFING("磨皮"),
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
    SHARPEN("锐化"),
    LEVELS("等级"),
    FILTER_GROUP("FILTER_GROUP"),

    GAMMA("伽玛"),

    INVERT("反转"),

    GRAYSCALE("灰度"),

    SEPIA("棕黑色"),

    SOBEL_EDGE_DETECTION("Sobel边缘检测算法"),

    THRESHOLD_EDGE_DETECTION("THRESHOLD_EDGE_DETECTION"),

    THREE_X_THREE_CONVOLUTION("THREE_X_THREE_CONVOLUTION"),

    EMBOSS("凹凸"),

    POSTERIZE("多色"),

    HIGHLIGHT_SHADOW("高光和阴影"),

    MONOCHROME("单色"),

    OPACITY("不透明度"),

    RGB("RGB"),

    WHITE_BALANCE("白平衡"),

    VIGNETTE("VIGNETTE"),

    TONE_CURVE("色调曲线"),

    LUMINANCE("亮度"),

    LUMINANCE_THRESHSOLD("亮度阈值"),

    GAUSSIAN_BLUR("高斯模糊"),

    CROSSHATCH("交叉影线"),

    BOX_BLUR("盒状模糊"),

    CGA_COLORSPACE("色彩空间滤镜"),

    DILATION("扩展边缘模糊，变黑白"),

    KUWAHARA("叠加"),

    RGB_DILATION("RGB扩展边缘模糊，有色彩"),

    TOON("香椿"),

    SMOOTH_TOON("SMOOTH_TOON"),

    BULGE_DISTORTION("凸起变形"),

    GLASS_SPHERE("玻璃球折射"),

    HAZE("阴霾"),

    LAPLACIAN("拉普拉斯式"),

    NON_MAXIMUM_SUPPRESSION("只显示亮度最高的像素"),

    SPHERE_REFRACTION("球面折射"),

    SWIRL("漩涡"),

    WEAK_PIXEL_INCLUSION("弱像素"),

    FALSE_COLOR("假色"),

    COLOR_BALANCE("颜色平衡"),

    LEVELS_FILTER_MIN("LEVELS_FILTER_MIN"),

    HALFTONE("半色调"),

    BILATERAL_BLUR("双边模糊"),

    ZOOM_BLUR("变焦模糊"),

    TRANSFORM2D("形状变化"),

    SOLARIZE("日晒"),

    VIBRANCE("活力");



    //TODO 会导致 Crash
//    IMAGE_ADJUST("IMAGE_ADJUST");


    var filterNmae = name
}