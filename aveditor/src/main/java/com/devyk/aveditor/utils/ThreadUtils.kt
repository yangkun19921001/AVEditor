package com.devyk.aveditor.utils

import android.os.Handler
import android.os.Looper

/**
 * <pre>
 *     author  : devyk on 2020-08-07 11:10
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ThreadUtils
 * </pre>
 */
object ThreadUtils {
    private val mHandler = Handler(Looper.getMainLooper())

    private val mThreadPoolExecutor = ThreadPoolManager.getInstance()

    fun <T> runMainThread(body: () -> T) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            body()
        } else {
            mHandler.post {
                body()
            }
        }

        Thread {}.start()
    }

    fun <T> runChildThread(body: () -> T) {
        mThreadPoolExecutor.addTask(body().hashCode().toString(), Runnable { body })
    }
}