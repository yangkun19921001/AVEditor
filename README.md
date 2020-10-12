

## AVTools

这是一款短视频编辑 SDK ，仿 DouYin 音视频处理。功能包含有美颜、滤镜、贴纸、特效、录制、分段录制、速率录制、变声、配乐、rtmp 直播推流、图片转视频、剪辑等功能。

### 效果

**音视频录制编辑**

片段加载失败[点击下载 Gif](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200820232848.gif)

![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200820232848.gif)



![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200830192244.jpg) ![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200830192505.jpg)![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200830192337.jpg)

![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200830192558.jpg)







**播放器**

如果播放器效果加载失败[点击下载 Gif 文件](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200524193715.gif)

![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200524193715.gif)


### 使用方式

####1、添加远程依赖

```groovy
implementation 'com.devyk.avtools:AVTools:0.0.1'
```

####2、API 文档

待编辑...



### 版本:

####v_0.0.2 (研发中...)

- 分段录制-最后的音视频合并(已完成)
- RTMP 直播推流
- 美颜
- 特效
- 贴纸
- 编辑页面基于 YUV 滤镜(已完成)
- 添加支持 FFmpeg 命令处理(已完成)
- 软硬编解码
- 导入 **MP3** 格式 , 为视频的背景音，将录制的音频和导入的音频混音
- 音频，视频剪辑合成

#### v_0.0.1
- 基于 FFmpeg 、OpenGL/SL 自研万能音视频播放器（已完成)
- 导入 **MP3** 格式 , 为录制的音频源(已完成)
- 音频视频录制合成 mp4（已完成)
- 滤镜 (已完成)
- 水印 (已完成)
- 倍速播放(已完成)
- 速率录制(已完成 极慢/慢/标准/快/极快)
- 分段录制(完成，待分段总合并)

  

### 感谢

- [FFmpeg](https://ffmpeg.org/)
- [google/grafika](https://github.com/google/grafika)
- [android-gpuimage](https://github.com/cats-oss/android-gpuimage)
- [magic-camera](https://github.com/wuhaoyu1990/MagicCamera)
- [欢迎来到OpenGL的世界](https://learnopengl-cn.github.io/)
- [音视频视频学习资料](https://space.bilibili.com/38154792/channel/index)









