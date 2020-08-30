

## KAVEdit

这是一款短视频编辑 SDK，仿 DouYin 音视频处理。功能包含有美颜、滤镜、贴纸、特效、录制、分段录制、速率录制、变声、配乐、rtmp 直播推流、图片转视频、剪辑等功能。



### 使用方式

**1、添加远程依赖**

```groovy

```

**2、API 文档**

**JNI-播放模块-PlayerEngine**

待编辑...

**JNI-音视频格式封装模块-AVMuxerEngine**

待编辑...

**JNI-速率模块-AVSpeedEngine**

待编辑...

**JNI-音视频编辑模块-AVEditorEngine**

待编辑...




### 版本:

#### v1.0.1
- 基于 FFmpeg 、OpenGL/SL 自研万能音视频播放器（已完成)
- 导入 **MP3** 格式 , 为录制的音频源(已完成)
- 音频视频录制合成 mp4（已完成)
- 滤镜 (已完成)
- 水印 (已完成)
- 倍速播放(已完成)
- RTMP 直播推流
- 分段录制(完成，待分段总合并)
- 美颜
- 特效
- 贴纸
- 软硬编解码
- 导入 **MP3** 格式 , 为视频的背景音，将录制的音频和导入的音频混音
- 速率录制(完成快，极快模式)
- 音频，视频剪辑合成

### 效果

**音视频录制编辑**

片段加载失败[点击下载 Gif](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200820232848.gif)

![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200820232848.gif)



![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200830192244.jpg) ![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200830192505.jpg)![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200830192337.jpg)

![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200830192558.jpg)







**播放器**

如果播放器效果加载失败[点击下载 Gif 文件](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200524193715.gif)

![](https://devyk.oss-cn-qingdao.aliyuncs.com/blog/20200524193715.gif)

### 感谢

- [FFmpeg](https://ffmpeg.org/)
- [google/grafika](https://github.com/google/grafika)
- [android-gpuimage](https://github.com/cats-oss/android-gpuimage)
- [magic-camera](https://github.com/wuhaoyu1990/MagicCamera)









