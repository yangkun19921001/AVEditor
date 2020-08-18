package com.devyk.aveditor.jni

/**
 * <pre>
 *     author  : devyk on 2020-08-12 17:34
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IMusicDecode
 * </pre>
 */
public interface IMusicDecode {
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
     * 恢复解码
     */
    public fun resume()


    public interface OnDecodeListener {

        /**
         * 开始解码
         */
        public fun onDecodeStart(sampleRate: Int, channels: Int, sampleFormat: Int)

        /**
         * 回调编码完成的 PCM 数据
         */
        public fun onDecodeData(data: ByteArray)

        /**
         * 解码完成
         */
        public fun onDecodeStop()

    }

    public fun addOnDecodeListener(listener: OnDecodeListener)

    /**
     * C++ 实现
     * 停止解码
     */
    public fun stop()
}