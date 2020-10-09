package com.devyk.ffmpeglib.entity

import java.util.ArrayList

/**
 * <pre>
 *     author  : devyk on 2020-09-28 20:28
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVVideo 视频处理类
 * </pre>
 */
class AVVideo(
    /**
     * 获取视频路径
     *
     * @return
     */
    val videoPath: String  //视频地址
) {

    //剪辑
    /**
     * 获取剪辑信息
     *
     * @return
     */
    var videoClip = false
        private set//是否剪辑
    /**
     * 获取剪辑起始时间
     *
     * @return
     */
    var clipStart: Float = 0.toFloat()
        private set//剪辑开始时间
    /**
     * 获取剪辑持续时间
     *
     * @return
     */
    var clipDuration: Float = 0.toFloat()
        private set//剪辑时间

    //滤镜
    /**
     * 获取滤镜效果
     *
     * @return
     */
    var mFilter: StringBuilder? = null
    

    //特效
    /**
     * 获取添加的图片类
     *
     * @return
     */
    val epDraws: ArrayList<AVDraw>

    //裁剪
    /**
     * 获取裁剪信息
     * @return
     */
    var crop: Crop? = null
        private set

    private fun getFilters(): StringBuilder? {
        if (mFilter == null || mFilter.toString() == "") {
            mFilter = StringBuilder()
        } else {
            mFilter?.append(",")
        }
        return mFilter
    }


    init {
        epDraws = ArrayList()
    }

    /**
     * 设置视频剪辑
     *
     * @param start    起始时间，单位秒
     * @param duration 持续时间，单位秒
     * @return
     */
    fun clip(start: Float, duration: Float): AVVideo {
        videoClip = true
        this.clipStart = start
        this.clipDuration = duration
        return this
    }


    /**
     * 设置旋转和镜像
     *
     * @param rotation 旋转角度(仅支持90,180,270度旋转)
     * @param isFlip   是否镜像
     * @return
     */
    fun rotation(rotation: Int, isFlip: Boolean): AVVideo {
        mFilter = getFilters()
        if (isFlip) {
            when (rotation) {
                0 -> mFilter!!.append("hflip")
                90 -> mFilter!!.append("transpose=3")
                180 -> mFilter!!.append("vflip")
                270 -> mFilter!!.append("transpose=0")
            }
        } else {
            when (rotation) {
                90 -> mFilter!!.append("transpose=2")
                180 -> mFilter!!.append("vflip,hflip")
                270 -> mFilter!!.append("transpose=1")
            }
        }
        return this
    }

    /**
     * 设置裁剪
     *
     * @param width  裁剪宽度
     * @param height 裁剪高度
     * @param x      起始位置X
     * @param y      起始位置Y
     * @return
     */
    fun crop(width: Float, height: Float, x: Float, y: Float): AVVideo {
        mFilter = getFilters()
        crop = Crop(width, height, x, y)
        mFilter!!.append("crop=$width:$height:$x:$y")
        return this
    }

    /**
     * 为视频添加文字
     *
     * @param size  文字大小
     * @param color 文字颜色(white,black,blue,red...)
     * @param x     文字的x坐标
     * @param y     文字的y坐标
     * @param ttf   文字字体的路径
     * @param text  添加的文字
     */
    @Deprecated("废弃，采用EpText参数")
    fun addText(x: Int, y: Int, size: Float, color: String, ttf: String, text: String): AVVideo {
        mFilter = getFilters()
        mFilter!!.append("drawtext=fontfile=$ttf:fontsize=$size:fontcolor=$color:x=$x:y=$y:text='$text'")
        return this
    }


    fun addText(avText: AVText){
        mFilter = getFilters()
        mFilter?.append(avText.textFitler);
    }

    /**
     * 为视频添加时间
     *
     * @param size  文字大小
     * @param color 文字颜色(white,black,blue,red...)
     * @param x     文字的x坐标
     * @param y     文字的y坐标
     * @param ttf   文字字体的路径
     * @param type  时间类型(1==>hh:mm:ss,2==>yyyy-MM-dd hh:mm:ss,3==>yyyy年MM月dd日 hh时mm分ss秒)
     */
    fun addTime(x: Int, y: Int, size: Float, color: String, ttf: String, type: Int): AVVideo {
        val time = System.currentTimeMillis() / 1000
        val str = time.toString()
        mFilter =getFilters()
        var ts = ""
        when (type) {
            1 -> ts = "%{pts\\:localtime\\:$str\\:%H\\\\\\:%M\\\\\\:%S}"
            2 -> ts = "%{pts\\:localtime\\:$str}"
            3 -> ts = "%{pts\\:localtime\\:$str\\:%Y\\\\年%m\\\\月%d\\\\日\n%H\\\\\\时%M\\\\\\分%S秒}"
        }
        mFilter!!.append("drawtext=fontfile=$ttf:fontsize=$size:fontcolor=$color:x=$x:y=$y:text='$ts'")
        return this
    }

    /**
     * 添加自定义滤镜效果
     *
     * @param ofi 命令符
     * @return
     */
    fun addFilter(ofi: String): AVVideo {
        mFilter =getFilters()
        mFilter!!.append(ofi)
        return this
    }

    /**
     * 为视频添加图片
     *
     * @param epDraw 添加的图片类
     * @return
     */
    fun addDraw(epDraw: AVDraw): AVVideo {
        epDraws.add(epDraw)

        return this
    }

    fun addLogo(){

    }

    /**
     * 裁剪信息类
     */
    inner class Crop(width: Float, height: Float, x: Float, y: Float) {
        var width: Float = 0.toFloat()
            internal set
        var height: Float = 0.toFloat()
            internal set
        var x: Float = 0.toFloat()
            internal set
        var y: Float = 0.toFloat()
            internal set

        init {
            this.width = width
            this.height = height
            this.x = x
            this.y = y
        }
    }
}
