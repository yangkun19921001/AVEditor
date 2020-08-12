precision mediump float;

//当前要采集像素的点
varying mediump vec2 aCoord;
//纹理
uniform sampler2D vTexture;

//输出的宽与高
uniform int width;
uniform int height;

vec2 blurCoordinates[20];
void main(){
    //1、 模糊 ： 平滑处理 降噪
    //singleStepOffset：步长
    vec2 singleStepOffset = vec2(1.0/float(width),1.0/float(height));
    blurCoordinates[0] = aCoord.xy + singleStepOffset * vec2(0.0, -10.0);
    blurCoordinates[1] = aCoord.xy + singleStepOffset * vec2(0.0, 10.0);
    blurCoordinates[2] = aCoord.xy + singleStepOffset * vec2(-10.0, 0.0);
    blurCoordinates[3] = aCoord.xy + singleStepOffset * vec2(10.0, 0.0);
    blurCoordinates[4] = aCoord.xy + singleStepOffset * vec2(5.0, -8.0);
    blurCoordinates[5] = aCoord.xy + singleStepOffset * vec2(5.0, 8.0);
    blurCoordinates[6] = aCoord.xy + singleStepOffset * vec2(-5.0, 8.0);
    blurCoordinates[7] = aCoord.xy + singleStepOffset * vec2(-5.0, -8.0);
    blurCoordinates[8] = aCoord.xy + singleStepOffset * vec2(8.0, -5.0);
    blurCoordinates[9] = aCoord.xy + singleStepOffset * vec2(8.0, 5.0);
    blurCoordinates[10] = aCoord.xy + singleStepOffset * vec2(-8.0, 5.0);
    blurCoordinates[11] = aCoord.xy + singleStepOffset * vec2(-8.0, -5.0);
    blurCoordinates[12] = aCoord.xy + singleStepOffset * vec2(0.0, -6.0);
    blurCoordinates[13] = aCoord.xy + singleStepOffset * vec2(0.0, 6.0);
    blurCoordinates[14] = aCoord.xy + singleStepOffset * vec2(6.0, 0.0);
    blurCoordinates[15] = aCoord.xy + singleStepOffset * vec2(-6.0, 0.0);
    blurCoordinates[16] = aCoord.xy + singleStepOffset * vec2(-4.0, -4.0);
    blurCoordinates[17] = aCoord.xy + singleStepOffset * vec2(-4.0, 4.0);
    blurCoordinates[18] = aCoord.xy + singleStepOffset * vec2(4.0, -4.0);
    blurCoordinates[19] = aCoord.xy + singleStepOffset * vec2(4.0, 4.0);
    //计算平均值
     //本身的点的像素值
     vec4 currentColor = texture2D(vTexture, aCoord);
     vec3 rgb = currentColor.rgb;
     // 计算偏移坐标的颜色值总和
        for (int i = 0; i < 20; i++) {
            //采集20个点 的像素值 相加 得到总和
            rgb += texture2D(vTexture, blurCoordinates[i].xy).rgb;
        }
     // rgb：21个点的像素和
     //平均值 模糊效果
     // rgba
    vec4 blur = vec4(rgb * 1.0 / 21.0, currentColor.a);
    //gl_FragColor = blur;

        //高反差
        //https://www.jianshu.com/p/bb702124d2ad
        //https://blog.csdn.net/matrix_space/article/details/22426633
        // 强光处理: color = 2 * color1 * color2
        //  24.0 强光程度
        // clamp:获得三个参数中大小处在中间的那个值
        vec4 highPassColor = currentColor - blur ;
        highPassColor.r = clamp(2.0 * highPassColor.r * highPassColor.r * 24.0, 0.0, 1.0);
        highPassColor.g = clamp(2.0 * highPassColor.g * highPassColor.g * 24.0, 0.0, 1.0);
        highPassColor.b = clamp(2.0 * highPassColor.b * highPassColor.b * 24.0, 0.0, 1.0);
        // 过滤疤痕
        vec4 highPassBlur = vec4(highPassColor.rgb, 1.0);

        //3、融合 -> 磨皮
            //蓝色通道值
        float b = min(currentColor.b, blur.b);
        float value = clamp((b - 0.2) * 5.0, 0.0, 1.0);
            // RGB的最大值
        float maxChannelColor = max(max(highPassBlur.r, highPassBlur.g), highPassBlur.b);
            // 磨皮程度
        float intensity = 1.0; // 0.0 - 1.0f 再大会很模糊
        float currentIntensity = (1.0 - maxChannelColor / (maxChannelColor + 0.2)) * value * intensity;
        //gl_FragColor = highPassBlur;
        // 一个滤镜
        //opengl 内置函数 线性融合
        //混合 x*(1−a)+y⋅a
        // 第三个值越大，在这里融合的图像 越模糊
        vec3 r = mix(currentColor.rgb,blur.rgb,currentIntensity);
        //
        gl_FragColor = vec4(r,1.0);

}