package com.devyk.aveditor.entity

/**
 * <pre>
 *     author  : devyk on 2020-10-11 14:11
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is YUVEntity
 * </pre>
 */
data class YUVEntity(val width:Int,val height :Int,val y:ByteArray,val u:ByteArray,val v:ByteArray)