package com.devyk.aveditor.decode

import com.devyk.aveditor.jni.IMusicDecode

/**
 * <pre>
 *     author  : devyk on 2020-08-12 22:49
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IAudioDecode
 * </pre>
 */
public interface IAudioDecode {
    /**
     * C++ 实现
     * 初始化解码器
     */
    public fun addRecordMusic(path: String?)

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


    /**
     * 解码回调
     */
    fun addOnDecodeListener(listener: IMusicDecode.OnDecodeListener);

    /**
     * C++ 实现
     * 停止解码
     */
    public fun stop()

}