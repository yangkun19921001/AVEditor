package com.devyk.avedit

import android.os.Environment
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import java.io.File

/**
 * <pre>
 *     author  : devyk on 2020-04-25 17:17
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioUtils
 * </pre>
 */
object LogHelper {

    private val TAG = this.javaClass.simpleName

    /**
     * init log
     */
    public fun initLog(
        isLogOpen: Boolean = true,
        logPath: String = Environment.getExternalStorageDirectory().absolutePath + File.separator + "DevYK/avedit",
        cachePath: String = Environment.getExternalStorageDirectory().absolutePath + File.separator + "DevYK/Cache",
        nameprefix: String = "DevYK",
        cacheDay: Int = 3
    ) {
        if (!File(logPath).exists())
            File(logPath).mkdirs()
        if (!File(cachePath).exists())
            File(cachePath).mkdirs()
        Xlog.open(isLogOpen, Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, nameprefix, cacheDay, "")
        Log.setLogImp(Xlog())
        Log.e(TAG, "LogInit ---> ");
    }


    public fun i(tag: String = javaClass.simpleName, obj: String) {
        Log.i(tag, obj);
    }

    public fun e(tag: String = javaClass.simpleName, obj: String) {
        Log.e(tag, obj);
    }

    public fun d(tag: String = javaClass.simpleName, obj: String) {
        Log.d(tag, obj);
    }

    public fun w(tag: String = javaClass.simpleName, obj: String) {
        Log.w(tag, obj);
    }

}