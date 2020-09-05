package com.devyk.aveditor.mediacodec

import com.devyk.aveditor.entity.Speed

/**
 * <pre>
 *     author  : devyk on 2020-06-13 23:55
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ICodec
 * </pre>
 */
public interface IAudioCodec{


    /**
     * 准备编码
     */
    fun start(mSpeed: Speed)

    /**
     * 将数据送入编解码器
     */
     fun <T> enqueueCodec(input: T?)

    /**
     * 停止编码
     */
    fun stop();
}