package com.devyk.aveditor.entity

import android.graphics.Bitmap

/**
 * <pre>
 *     author  : devyk on 2020-08-09 18:08
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Watermark
 * </pre>
 */

data class Watermark(var bitmap: Bitmap, var x: Float=0.0f, var y: Float=0.0f)