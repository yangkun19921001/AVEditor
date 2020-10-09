package com.devyk.ffmpeglib.entity

/**
 * <pre>
 *     author  : devyk on 2020-10-02 20:28
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is OutputOption
 * </pre>
 */

/**
 * 输出选项设置
 */
class OutputOption(
    internal var outPath: String//输出路径
) {
    var frameRate = 0//帧率
    var bitRate = 0//比特率(一般设置10M)
    var outFormat = ""//输出格式(目前暂时只支持mp4,x264,mp3,gif)
    var width_ = 0//输出宽度
    var height_ = 0//输出高度
    private var sar = 6//输出宽高比

    /**
     * 获取输出信息
     *
     * @return 1
     */
    internal val outputInfo: String
        get() {
            val res = StringBuilder()
            if (frameRate != 0) {
                res.append(" -r ").append(frameRate)
            }
            if (bitRate != 0) {
                res.append(" -b ").append(bitRate).append("M")
            }
            if (!outFormat.isEmpty()) {
                res.append(" -f ").append(outFormat)
            }
            return res.toString()
        }

    /**
     * 获取宽高比
     *
     * @return 1
     */
    fun getSar(): String {
        val res: String
        when (sar) {
            ONE_TO_ONE -> res = "1/1"
            FOUR_TO_THREE -> res = "4/3"
            THREE_TO_FOUR -> res = "3/4"
            SIXTEEN_TO_NINE -> res = "16/9"
            NINE_TO_SIXTEEN -> res = "9/16"
            else -> res = "$width_/$height_"
        }
        return res
    }

    fun setSar(sar: Int) {
        this.sar = sar
    }

    /**
     * 设置宽度
     *
     * @param width 宽
     */
    fun setWidth(width: Int) {
        var width = width
        if (width % 2 != 0) width -= 1
        this.width_ = width
    }

    /**
     * 设置高度
     *
     * @param height 高
     */
    fun setHeight(height: Int) {
        var height = height
        if (height % 2 != 0) height -= 1
        this.height_ = height
    }

    companion object {
        internal val ONE_TO_ONE = 1// 1:1
        internal val FOUR_TO_THREE = 2// 4:3
        internal val SIXTEEN_TO_NINE = 3// 16:9
        internal val NINE_TO_SIXTEEN = 4// 9:16
        internal val THREE_TO_FOUR = 5// 3:4
    }
}
