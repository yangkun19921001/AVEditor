package com.devyk.aveditor.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.os.SystemClock

/**
 * <pre>
 *     author  : devyk on 2020-08-07 11:08
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is TimerUtilsUtils
 * </pre>
 */
public class TimerUtils(private val mListener: OnTimerUtilsListener, private var mUpdateInterval: Int) {

    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private var mStartTime: Long = 0
    var duration: Int = 0

    private val mRunnable = object : Runnable {
        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            if (mStartTime == 0L) {
                mStartTime = SystemClock.elapsedRealtime()
            }
            val time = SystemClock.elapsedRealtime() - mStartTime
            if (time >= duration) {
                ThreadUtils.runChildThread {
                    mListener.update(this@TimerUtils, duration)
                    mListener.end(this@TimerUtils)
                }
                return
            } else {
                ThreadUtils.runChildThread {
                    mListener.update(this@TimerUtils, time.toInt())
                }
            }

            mHandler?.postDelayed(this, mUpdateInterval.toLong())
        }
    }

    interface OnTimerUtilsListener {
        // called for interval update
        fun update(timer: TimerUtils, elapsedTime: Int)

        // called when the timer ends
        fun end(timer: TimerUtils)
    }

    init {
        mHandlerThread = HandlerThread("time")
        mHandlerThread?.start()
        mHandler = Handler(mHandlerThread!!.looper)
    }

    fun setUpdateInterval(updateInterval: Int) {
        mUpdateInterval = updateInterval
    }

    fun start(duration: Int) {
        this.duration = duration
        mStartTime = 0
        stop()
        mHandler?.postDelayed(mRunnable, 0)
    }

    fun stop() {
        mStartTime = 0
        mHandler?.removeCallbacks(mRunnable)
    }

    fun release() {
        mHandlerThread?.looper?.quit()
        mHandlerThread?.quit()
        mHandlerThread = null
        mHandler?.removeCallbacks(mRunnable)
        mHandler = null
    }
}
