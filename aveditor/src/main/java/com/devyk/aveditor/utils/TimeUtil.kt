package com.devyk.aveditor.utils

/**
 * <pre>
 *     author  : devyk on 2020-09-29 15:49
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is TimeUtil
 * </pre>
 */

object TimeUtil {

    fun format(time: Long): String {
        var time = time
        var str = ""
        time = time / 1000
        val s = (time % 60).toInt()
        val m = (time / 60 % 60).toInt()
        val h = (time / 3600).toInt()
        if (h > 0) {
            str += "$h:"
        }
        if (m <= 9) {
            str = str + "0" + m + ":"
        } else {
            str += "$m:"
        }
        if (s <= 9) {
            str = str + "0" + s
        } else {
            str += s
        }
        return str
    }
}