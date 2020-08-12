package com.devyk.ikavedit.entity

import com.devyk.aveditor.video.filter.helper.AVFilterType

/**
 * <pre>
 *     author  : devyk on 2020-08-10 16:49
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Filterentity
 * </pre>
 */
data class FilterEntity(val resId: Int, var isSelect: Boolean, var avFilterType: AVFilterType)