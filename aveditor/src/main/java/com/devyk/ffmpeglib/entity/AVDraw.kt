package com.devyk.ffmpeglib.entity

/**
 * <pre>
 *     author  : devyk on 2020-09-28 20:20
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVDraw 添加特效
 * </pre>
 */

class AVDraw {

    var picPath: String? = null
        private set//图片路径
    var picX: Int = 0
        private set//图片x的位置
    var picY: Int = 0
        private set//图片y的位置
    var picWidth: Float = 0.toFloat()
        private set//图片的宽
    var picHeight: Float = 0.toFloat()
        private set//图片的高
    var isAnimation: Boolean = false
        private set//是否是动图

    var time = ""//起始结束时间

    var picFilter: String? = null
        get() = if (field == null) "" else field!! + ","//图片滤镜

    constructor(picPath: String, picX: Int, picY: Int, picWidth: Float, picHeight: Float, isAnimation: Boolean) {
        this.picPath = picPath
        this.picX = picX
        this.picY = picY
        this.picWidth = picWidth
        this.picHeight = picHeight
        this.isAnimation = isAnimation
    }

    constructor(
        picPath: String,
        picX: Int,
        picY: Int,
        picWidth: Float,
        picHeight: Float,
        isAnimation: Boolean,
        start: Int,
        end: Int
    ) {
        this.picPath = picPath
        this.picX = picX
        this.picY = picY
        this.picWidth = picWidth
        this.picHeight = picHeight
        this.isAnimation = isAnimation
        time = ":enable=between(t\\,$start\\,$end)"
    }
}
