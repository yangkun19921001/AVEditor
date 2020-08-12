precision mediump float;

varying vec2 aCoord;

uniform sampler2D vTexture;

uniform vec2 left_eye; //左眼
uniform vec2 right_eye; //右眼

//实现 公式 ： 得出需要采集的改变后的点距离眼睛中心点的位置
// r:原来的点距离眼睛中心点的位置
//rmax: 放大区域
float fs(float r,float rmax){
    //放大系数
    float a = 0.4;
    return (1.0 - pow((r/rmax -1.0),2.0) *a);
}

//根据需要采集的点 aCoord 计算新的点(可能是需要改变为眼睛内部的点，完成放大的效果)
vec2 newCoord(vec2 coord,vec2 eye,float rmax){
    vec2 p = coord;
    //获得当前需要采集的点与眼睛的距离
    float r = distance(coord,eye);
    //在范围内 才放大
    if(r < rmax){
        //想要方法需要采集的点 与 眼睛中心点的距离
        float fsr = fs(r,rmax);
        // 新点-眼睛 / 老点-眼睛 = 新距离/老距离
        //(newCoord  - eye) / (coord-eye) = fsr/r;
        //(newCoord  - eye) = fsr/r * (coord-eye)
        p = fsr * (coord - eye) +eye;
    }
    return p;
}

void main(){
    //最大作用半径 rmax
    //计算两个点的距离
    float rmax = distance(left_eye,right_eye)/2.0;
    // 如果属于 左眼 放大区域的点 得到的就是 左眼里面的某一个点（完成放大效果）
    // 如果属于 右眼放大区域的点 或者都不属于 ，那么 newCoord还是 aCoord
    vec2 p = newCoord(aCoord,left_eye,rmax);

    p = newCoord(p,right_eye,rmax);
   //  采集到 RGBA 值
    gl_FragColor = texture2D(vTexture,p);
}