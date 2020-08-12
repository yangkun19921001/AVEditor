// 把顶点坐标给这个变量， 确定要画画的形状
attribute vec4 vPosition;
//接收纹理坐标，接收采样器采样图片的坐标
attribute vec4 vCoord;
//变换矩阵， 需要将原本的vCoord（01,11,00,10） 与矩阵相乘 才能够得到 surfacetexure(特殊)的正确的采样坐标
uniform mat4 vMatrix;
//传给片元着色器 像素点
varying vec2 aCoord;
void main(){
    //内置变量 gl_Position ,我们把顶点数据赋值给这个变量 opengl就知道它要画什么形状了
    gl_Position = vPosition;
    // 进过测试 和设备有关(有些设备直接就采集不到图像，有些呢则会镜像)
    aCoord = (vMatrix * vCoord).xy;
    //aCoord =  vec2((vCoord*vMatrix).x,(vCoord*vMatrix).y);
}