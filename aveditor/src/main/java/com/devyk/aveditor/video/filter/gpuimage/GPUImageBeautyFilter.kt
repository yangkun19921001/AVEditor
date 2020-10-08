package com.devyk.aveditor.video.filter.gpuimage

import android.content.Context
import android.opengl.GLES20
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter

class GPUImageBeautyFilter(context: Context) : GPUImageFilter(context,NO_FILTER_VERTEX_SHADER, BILATERAL_FRAGMENT_SHADER) {

    private var toneLevel: Float = 0.toFloat()
    private var gpu_beautyLevel: Float = 0.toFloat()
    private var brightLevel: Float = 0.toFloat()

    private var paramsLocation: Int = 0
    private var brightnessLocation: Int = 0
    private var singleStepOffsetLocation: Int = 0

    init {
        toneLevel = -0.5f
        gpu_beautyLevel = 0.8f
        brightLevel = 0.3f
    }

    public override fun onInit() {
        super.onInit()

        paramsLocation = GLES20.glGetUniformLocation(program, "params")
        brightnessLocation = GLES20.glGetUniformLocation(program, "brightness")
        singleStepOffsetLocation = GLES20.glGetUniformLocation(program, "singleStepOffset")

        setParams(gpu_beautyLevel, toneLevel)
        setBrightLevel(brightLevel)
    }



    //磨皮
    fun setBeautyLevel_(gpu_beautyLevel: Float) {
        this.gpu_beautyLevel = gpu_beautyLevel
        setParams(gpu_beautyLevel, toneLevel)
    }

    //美白
    fun setBrightLevel(brightLevel: Float) {
        this.brightLevel = brightLevel
        setFloat(brightnessLocation, 0.6f * (-0.5f + brightLevel))
    }

    //红润
    fun setToneLevel(toneLevel: Float) {
        this.toneLevel = toneLevel
        setParams(gpu_beautyLevel, toneLevel)
    }

    fun setAllBeautyParams(beauty: Float, bright: Float, tone: Float) {
        setBeautyLevel_(beauty)
        setBrightLevel(bright)
        setToneLevel(tone)
    }


    fun setParams(beauty: Float, tone: Float) {
        val vector = FloatArray(4)
        vector[0] = 1.0f - 0.6f * beauty
        vector[1] = 1.0f - 0.3f * beauty
        vector[2] = 0.1f + 0.3f * tone
        vector[3] = 0.1f + 0.3f * tone
        setFloatVec4(paramsLocation, vector)
    }

    private fun setTexelSize(w: Float, h: Float) {
        setFloatVec2(singleStepOffsetLocation, floatArrayOf(2.0f / w, 2.0f / h))
    }

    override fun onInputSizeChanged(width: Int, height: Int) {
        super.onInputSizeChanged(width, height)
        setTexelSize(width.toFloat(), height.toFloat())
    }

    companion object {
        val BILATERAL_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +
                "   varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "    uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "    uniform highp vec2 singleStepOffset;\n" +
                "    uniform highp vec4 params;\n" +
                "    uniform highp float brightness;\n" +
                "\n" +
                "    const highp vec3 W = vec3(0.299, 0.587, 0.114);\n" +
                "    const highp mat3 saturateMatrix = mat3(\n" +
                "        1.1102, -0.0598, -0.061,\n" +
                "        -0.0774, 1.0826, -0.1186,\n" +
                "        -0.0228, -0.0228, 1.1772);\n" +
                "    highp vec2 blurCoordinates[24];\n" +
                "\n" +
                "    highp float hardLight(highp float color) {\n" +
                "    if (color <= 0.5)\n" +
                "        color = color * color * 2.0;\n" +
                "    else\n" +
                "        color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n" +
                "    return color;\n" +
                "}\n" +
                "\n" +
                "    void main(){\n" +
                "    highp vec3 centralColor = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
                "    blurCoordinates[0] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -10.0);\n" +
                "    blurCoordinates[1] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 10.0);\n" +
                "    blurCoordinates[2] = textureCoordinate.xy + singleStepOffset * vec2(-10.0, 0.0);\n" +
                "    blurCoordinates[3] = textureCoordinate.xy + singleStepOffset * vec2(10.0, 0.0);\n" +
                "    blurCoordinates[4] = textureCoordinate.xy + singleStepOffset * vec2(5.0, -8.0);\n" +
                "    blurCoordinates[5] = textureCoordinate.xy + singleStepOffset * vec2(5.0, 8.0);\n" +
                "    blurCoordinates[6] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, 8.0);\n" +
                "    blurCoordinates[7] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, -8.0);\n" +
                "    blurCoordinates[8] = textureCoordinate.xy + singleStepOffset * vec2(8.0, -5.0);\n" +
                "    blurCoordinates[9] = textureCoordinate.xy + singleStepOffset * vec2(8.0, 5.0);\n" +
                "    blurCoordinates[10] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, 5.0);\n" +
                "    blurCoordinates[11] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, -5.0);\n" +
                "    blurCoordinates[12] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -6.0);\n" +
                "    blurCoordinates[13] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 6.0);\n" +
                "    blurCoordinates[14] = textureCoordinate.xy + singleStepOffset * vec2(6.0, 0.0);\n" +
                "    blurCoordinates[15] = textureCoordinate.xy + singleStepOffset * vec2(-6.0, 0.0);\n" +
                "    blurCoordinates[16] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, -4.0);\n" +
                "    blurCoordinates[17] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, 4.0);\n" +
                "    blurCoordinates[18] = textureCoordinate.xy + singleStepOffset * vec2(4.0, -4.0);\n" +
                "    blurCoordinates[19] = textureCoordinate.xy + singleStepOffset * vec2(4.0, 4.0);\n" +
                "    blurCoordinates[20] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, -2.0);\n" +
                "    blurCoordinates[21] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, 2.0);\n" +
                "    blurCoordinates[22] = textureCoordinate.xy + singleStepOffset * vec2(2.0, -2.0);\n" +
                "    blurCoordinates[23] = textureCoordinate.xy + singleStepOffset * vec2(2.0, 2.0);\n" +
                "\n" +
                "    highp float sampleColor = centralColor.g * 22.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[0]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[1]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[2]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[3]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[4]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[5]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[6]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[7]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[8]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[9]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[10]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[11]).g;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[12]).g * 2.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[13]).g * 2.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[14]).g * 2.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[15]).g * 2.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[16]).g * 2.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[17]).g * 2.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[18]).g * 2.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[19]).g * 2.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[20]).g * 3.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[21]).g * 3.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[22]).g * 3.0;\n" +
                "    sampleColor += texture2D(inputImageTexture, blurCoordinates[23]).g * 3.0;\n" +
                "\n" +
                "    sampleColor = sampleColor / 62.0;\n" +
                "\n" +
                "    highp float highPass = centralColor.g - sampleColor + 0.5;\n" +
                "\n" +
                "    for (int i = 0; i < 5; i++) {\n" +
                "        highPass = hardLight(highPass);\n" +
                "    }\n" +
                "    highp float lumance = dot(centralColor, W);\n" +
                "\n" +
                "    highp float alpha = pow(lumance, params.r);\n" +
                "\n" +
                "    highp vec3 smoothColor = centralColor + (centralColor-vec3(highPass))*alpha*0.1;\n" +
                "\n" +
                "    smoothColor.r = clamp(pow(smoothColor.r, params.g), 0.0, 1.0);\n" +
                "    smoothColor.g = clamp(pow(smoothColor.g, params.g), 0.0, 1.0);\n" +
                "    smoothColor.b = clamp(pow(smoothColor.b, params.g), 0.0, 1.0);\n" +
                "\n" +
                "    highp vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0)-centralColor);\n" +
                "    highp vec3 bianliang = max(smoothColor, centralColor);\n" +
                "    highp vec3 rouguang = 2.0*centralColor*smoothColor + centralColor*centralColor - 2.0*centralColor*centralColor*smoothColor;\n" +
                "\n" +
                "    gl_FragColor = vec4(mix(centralColor, lvse, alpha), 1.0);\n" +
                "    gl_FragColor.rgb = mix(gl_FragColor.rgb, bianliang, alpha);\n" +
                "    gl_FragColor.rgb = mix(gl_FragColor.rgb, rouguang, params.b);\n" +
                "\n" +
                "    highp vec3 satcolor = gl_FragColor.rgb * saturateMatrix;\n" +
                "    gl_FragColor.rgb = mix(gl_FragColor.rgb, satcolor, params.a);\n" +
                "    gl_FragColor.rgb = vec3(gl_FragColor.rgb + vec3(brightness));\n" +
                "}"
    }
}