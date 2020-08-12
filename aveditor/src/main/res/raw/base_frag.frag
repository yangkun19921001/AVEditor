//SurfaceTexture比较特殊
//float数据是什么精度的
precision mediump float;

//采样点的坐标
varying vec2 aCoord;

//采样器 不是从android的surfaceTexure中的纹理 采数据了，所以不再需要android的扩展纹理采样器了
//使用正常的 sampler2D
uniform sampler2D vTexture;

void main(){
    //变量 接收像素值
    // texture2D：采样器 采集 aCoord的像素 rgba
    gl_FragColor = texture2D(vTexture,aCoord);
    //赋值给 gl_FragColor 就可以了
//    vec4 rgba = texture2D(vTexture,aCoord);
//    gl_FragColor = vec4((rgba.r*0.3+rgba.g *0.59+rgba.b*0.11),(rgba.r*0.3+rgba.g *0.59+rgba.b*0.11),(rgba.r*0.3+rgba.g *0.59+rgba.b*0.11),rgba.a);
}