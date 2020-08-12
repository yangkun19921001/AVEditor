//画肉体 base_vertex就可以了
//还要画灵魂
attribute vec4 vPosition;
attribute vec2 vCoord;
varying vec2 aCoord;

uniform mat4 vMatrix;

void main(){
     gl_Position = vMatrix * vPosition;
       // 进过测试 和设备有关(有些设备直接就采集不到图像，有些呢则会镜像)
     aCoord =  vCoord;
}