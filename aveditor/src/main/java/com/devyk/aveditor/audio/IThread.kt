package com.devyk.aveditor.audio

/**
 * <pre>
 *     author  : devyk on 2020-07-15 20:42
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IThread
 * </pre>
 */
public interface IThread {

    /**
     * 开始执行线程
     */
    fun start(main:()->Unit)

    /**
     * 停止执行
     */
    fun stop()

    /**
     *设置是否暂停
     */
    fun setPause(pause: Boolean)

    /**
     * 停止
     */
    fun isPause(): Boolean

    /**
     * 是否运行
     */
    fun isRuning(): Boolean
}