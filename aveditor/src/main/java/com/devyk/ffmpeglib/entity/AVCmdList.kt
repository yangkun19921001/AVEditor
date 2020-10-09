package com.devyk.ffmpeglib.entity
import java.util.ArrayList


/**
 * <pre>
 *     author  : devyk on 2020-09-28 20:17
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVCmdList 命令集合
 * </pre>
 */
class AVCmdList : ArrayList<String>() {

    fun append(s: String): AVCmdList {
        this.add(s)
        return this
    }

    fun append(i: Int): AVCmdList {
        this.add(i.toString() + "")
        return this
    }

    fun append(f: Float): AVCmdList {
        this.add(f.toString() + "")
        return this
    }

    fun append(sb: StringBuilder): AVCmdList {
        this.add(sb.toString())
        return this
    }

    fun append(ss: Array<String>): AVCmdList {
        for (s in ss) {
            if (s.replace(" ", "") != "") {
                this.add(s)
            }
        }
        return this
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (s in this) {
            sb.append(" ").append(s)
        }
        return sb.toString()
    }
}
