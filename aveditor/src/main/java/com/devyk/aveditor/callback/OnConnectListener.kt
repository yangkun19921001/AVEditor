package com.devyk.aveditor.callback

/**
 * <pre>
 *     author  : devyk on 2020-07-16 23:10
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is OnConnectListener
 * </pre>
 */
public interface OnConnectListener {
    /**
     * 开始链接
     */
    fun onConnecting()

    /**
     * 连接成功
     */
    fun onConnected()

    /**
     * 推送失败
     */
    fun onFail(message:String)

    /**
     * 关闭
     */
    fun onClose()
}