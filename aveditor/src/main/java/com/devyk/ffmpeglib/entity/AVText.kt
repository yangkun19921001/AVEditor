package com.devyk.ffmpeglib.entity

/**
 * <pre>
 *     author  : devyk on 2020-10-02 13:04
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVText
 * </pre>
 */
class AVText
/**
 * @param x     文字起始位置X
 * @param y     文字起始位置Y
 * @param size  文字的大小
 * @param color 文字的颜色
 * @param ttf   文字的字体文件路径
 * @param text  添加文字的内容
 * @param time  起始结束时间(传null的时候为一直显示)
 */
    (x: Int, y: Int, size: Float, color: Color, ttf: String, text: String, time: Time?) {

    val textFitler: String

    init {
        this.textFitler =
            "drawtext=fontfile=" + ttf + ":fontsize=" + size + ":fontcolor=" + color.color + ":x=" + x + ":y=" + y + ":text='" + text + "'" + (time?.time
                ?: "")
    }

    /**
     * 起始结束时间的类
     */
    class Time(start: Int, end: Int) {
        val time: String

        init {
            this.time = ":enable=between(t\\,$start\\,$end)"
        }
    }

    /**
     * 颜色
     */
    enum class Color private constructor(val color: String) {
        Red("Red"), Blue("Blue"), Yellow("Yellow"), Black("Black"), DarkBlue("DarkBlue"),
        Green("Green"), SkyBlue("SkyBlue"), Orange("Orange"), White("White"), Cyan("Cyan")
    }
}