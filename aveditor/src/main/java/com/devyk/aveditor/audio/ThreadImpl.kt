package com.devyk.aveditor.audio

import com.devyk.aveditor.utils.LogHelper


/**
 * <pre>
 *     author  : devyk on 2020-07-15 20:48
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ThreadImpl
 * </pre>
 */
public open class ThreadImpl : IThread {

    private var isPause = false

    private var isRuning = false

    private var TAG = javaClass.simpleName


    override fun start(main: () -> Unit) {
        if (isRuning())return
        isRuning = true
        isPause = false
        Thread {
            main()
            LogHelper.d(TAG, "thread start!")
        }.start()
    }

    /**
     * 线程停止
     */
    override fun stop() {
        isRuning = false
        isPause = true
        LogHelper.d(TAG, "thread stop!")
    }

    /**
     * 设置停止
     */
    override fun setPause(pause: Boolean) {
        this.isPause = pause
        LogHelper.d(TAG, "thread pause:${pause}!")
    }

    /**
     * 是否停止
     */
    override fun isPause(): Boolean = isPause


    /**
     * 是否执行
     */
    override fun isRuning(): Boolean = isRuning

}