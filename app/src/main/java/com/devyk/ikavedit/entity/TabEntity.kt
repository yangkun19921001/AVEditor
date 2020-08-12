package com.devyk.ikavedit.entity

import com.flyco.tablayout.listener.CustomTabEntity

/**
 * <pre>
 *     author  : devyk on 2020-08-06 11:11
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is TabEntity
 * </pre>
 */
public class TabEntity(title: String) : CustomTabEntity {

    private var mTitle: String? = null

    init {
        mTitle = title
    }


    override fun getTabUnselectedIcon(): Int = 0

    override fun getTabSelectedIcon(): Int = 0

    override fun getTabTitle(): String = mTitle!!
}