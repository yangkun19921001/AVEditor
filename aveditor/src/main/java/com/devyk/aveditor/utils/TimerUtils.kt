package com.devyk.aveditor.utils

import android.annotation.SuppressLint
import android.os.*
import java.util.concurrent.locks.ReentrantLock

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

    private var mHandler: Handler? = null
    private var mStartTime: Long = 0
    var duration: Int = 0
    private var TAG = "TimerUtils"



    interface OnTimerUtilsListener {
        // called for interval update
        fun update(timer: TimerUtils, elapsedTime: Int)

        // called when the timer ends
        fun end(timer: TimerUtils)
    }

    init {
        mHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                if (mStartTime == 0L) {
                    mStartTime = SystemClock.elapsedRealtime()
                }
                val time = SystemClock.elapsedRealtime() - mStartTime
                if (time >= duration) {
                    mListener.update(this@TimerUtils, duration)
                    mListener.end(this@TimerUtils)
                    return
                } else {
                    mListener.update(this@TimerUtils, time.toInt())

                }
                mHandler?.sendEmptyMessageDelayed(0, mUpdateInterval.toLong())
            }
        }

    }

    fun setUpdateInterval(updateInterval: Int) {
        mUpdateInterval = updateInterval

    }

    fun start(duration: Int) {
        synchronized(this) {
            stop()
            this.duration = duration
            mStartTime = 0
            mHandler?.sendEmptyMessageDelayed(0, 0)

        }
    }

    fun stop() {
        synchronized(this) {
            mStartTime = 0
            LogHelper.e(TAG, "stop")
            mHandler?.removeMessages(0)
        }
    }

    fun release() {
        synchronized(this) {
            mHandler?.removeMessages(0)
            mHandler = null
        }
    }
}
