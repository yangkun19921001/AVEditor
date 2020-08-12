precision mediump float;

varying vec2 aCoord;
//byte[] -》 分离出yuv ？

uniform sampler2D sampler_y; //yuv
uniform sampler2D sampler_u;
uniform sampler2D sampler_v;

//透明度
uniform float alpha;

void main(){
    //4个float数据 y、u、v保存在向量中的第一个
    float y = texture2D(sampler_y,aCoord).r;
    float u = texture2D(sampler_u,aCoord).r - 0.5;
    float v = texture2D(sampler_v,aCoord).r - 0.5;
    // yuv转rgb的公式
    //R = Y + 1.402 (v-128)
    //G = Y - 0.34414 (u - 128) - 0.71414 (v-128)
    //B = Y + 1.772 (u- 128)
    vec3 rgb;
    //u - 128
    //1、glsl中 不能直接将int与float进行计算
    //2、rgba取值都是：0-1 （128是0-255 归一化为0-1 128就是0.5）
    rgb.r = y + 1.402 * v;
    rgb.g = y - 0.34414 * u - 0.71414* v;
    rgb.b = y + 1.772 * u;
    //rgba
    gl_FragColor = vec4(rgb,alpha);
}