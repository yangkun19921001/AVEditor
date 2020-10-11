package com.devyk.aveditor.callback

/**
 * <pre>
 *     author  : devyk on 2020-10-11 13:50
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IYUVDataListener
 * </pre>
 */
public interface IYUVDataListener {
    fun onYUV420pData(
        width: Int,
        height: Int,
        y: ByteArray,
        u: ByteArray,
        v: ByteArray
    )
}