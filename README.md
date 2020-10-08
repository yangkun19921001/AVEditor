

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

**JNI-播放模块-AVPlayerEngine**

1. 添加播放控件

    ```xml
        <com.devyk.aveditor.widget.AVPlayView
            android:id="@+id/player_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    ```

2. 播放功能控制

   ```kotlin
       /**
        * 设置播放源
        */
       public fun setDataSource(source: String?)
       /**
        * 播放
        */
       public fun start()
       /**
        * 暂停
        */
       public fun setPause(status: Boolean)
       /**
        * 指定跳转到某个时间点播放
        */
       public fun seekTo(seek: Double)
       /**
        * 停止
        */
       public fun stop()
       /**
        * 播放进度监听
        */
       public fun addProgressListener(progress: OnProgressListener)
   ```

   

**JNI-音视频格式封装模块-AVMuxerEngine**

```kotlin
    /**
     * @param outPath 输出的文件路径
     * @param videoWidth 视频宽
     * @param videoHeight 视频高
     * @param frame_rate 帧率
     * @param videoBitRate 视频码率
     * @param audioSampleRate 音频采样率
     * @param audioChannels 音频通道数量
     * @param audioBitRate 音频码流
     */
    fun initMuxer(
        outPath: String?,
        videoWidth: Int,
        videoHeight: Int,
        frame_rate: Int,
        videoBitRate: Int,
        audioSampleRate: Int,
        audioChannels: Int,
        audioBitRate: Int
    );

    /**
     * @param 送入队列的数据
     * @param isAudio 是否是音频数据
     * @param pts 时间戳
     */
    fun enqueue(byteArray: ByteArray?, isAudio: Boolean = false, pts: Long);

    /**
     * 关闭复用器
     */
    fun close()
```



**JNI-速率模块-AVSpeedEngine**

1. PCM 速率控制

   ```kotlin
       /**
        * @param track 操作音频速率控制的对象索引，不能重复
        * @param channels 通道
        * @param samplingRate 采样率
        * @param pitchSemi 变调率
        */
       public fun initSpeedController(
           track: Int,
           channels: Int,
           samplingRate: Int,
           tempo: Double,
           pitchSemi: Double
       )
   
       /**
        * 放入 PCM 数据
        */
       public fun putData(track: Int, input: ByteArray, length: Int): Int
   
       /**
        * 取出变速之后的 PCM 数据
        */
       public fun getData(track: Int, out: ShortArray, length: Int): Int
   
       /**
        * 关闭资源
        */
       public fun close(track: Int)
   ```



**JNI-音频解码模块 - AVAudioDecodeEngine**

1. 任意音频文件解码为 PCM API

   ```kotlin
    /**
        * C++ 实现
        * 初始化解码器
        */
       public fun addRecordMusic(musicPath: String?)
   
       /**
        * C++ 实现
        * 开始解码
        */
       public fun start()
   
       /**
        * C++ 实现
        * 暂停解码
        */
       public fun pause()
   
       /**
        * C++ 实现
        * 恢复解码
        */
       public fun resume()
       
       /**
        * C++ 实现
        * 停止解码
        */
       public fun stop()
       /**
       * C++ 调用
       *添加解码回调
       */
       public fun addOnDecodeListener(listener: OnDecodeListener)
   ```

**JNI-音视频编辑模块-AVEditorEngine**

待编辑...



### 版本:

####v_0.0.2 (研发中...)

- 分段录制(最后的音视频合并)
- RTMP 直播推流
- 美颜
- 特效
- 贴纸
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









