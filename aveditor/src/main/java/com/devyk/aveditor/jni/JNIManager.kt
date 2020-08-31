package com.devyk.aveditor.jni

/**
 * <pre>
 *     author  : devyk on 2020-08-12 16:46
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is JNIManager
 * </pre>
 */
object JNIManager {


    /**
     * 播放模块
     */
    private var mPlayerEngine: IPlayer? = null

    /**
     * 音视频编辑模块
     */
    private var mAVEditor: IAVEditor? = null

    /**
     * 编解码边播放模块
     */
    private var mAVFileDecodeEngine: IMusicDecode? = null

    /**
     * FFmpeg 打包
     */
    private var mAVMuxer: INativeMuxer? = null

    /**
     * 录制速率控制
     */
    private var mSpeedController: ISpeedController? = null


    init {
        System.loadLibrary("avtools")
        mPlayerEngine = AVPlayerEngine()
        mAVFileDecodeEngine = AVFileDecodeEngine()
        mAVEditor = AVEditorEngine()
        mAVMuxer = AVMuxerEngine()
        mSpeedController = AVSpeedEngine();
    }


    /**
     * 动态替换播放拨快
     */
    public fun <T : IPlayer> setPlayerEngine(t: T) {
        mPlayerEngine = t
    }

    /**
     * 动态替音频解码模块
     */
    public fun <T : IMusicDecode> setAVFileDecodeEngine(t: T) {
        mAVFileDecodeEngine = t
    }

    /**
     * 动态替换媒体编辑模块
     */
    public fun <T : IAVEditor> setAVFileDecodeEngine(t: T) {
        mAVEditor = t
    }

    /**
     * 动态替换复用器
     */
    public fun <T : INativeMuxer> setAVMuxerEngine(t: T) {
        mAVMuxer = t
    }

    /**
     * 拿到播放的模块
     */
    public fun getAVPlayEngine(): IPlayer? = mPlayerEngine

    /**
     * 拿到媒体解码模块
     */
    public fun getAVDecodeEngine(): IMusicDecode? = mAVFileDecodeEngine


    /**
     * 拿到媒体编辑模块
     */
    public fun getAVEditorEngine(): IAVEditor? = mAVEditor


    /**
     * 拿到媒体编辑模块
     */
    public fun getAVMuxerEngine(): INativeMuxer? = mAVMuxer

    /**
     * 拿到速率控制
     */
    public fun getAVSpeedEngine(): ISpeedController? = mSpeedController
}