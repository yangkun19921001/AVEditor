package com.devyk.ikavedit.utils.intent

import android.content.Intent

/**
 * <pre>
 *     author  : devyk on 2020-08-10 11:40
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is OnResultListener
 * </pre>
 */
public interface OnResultListener<T:Intent> {
    fun onResult(data: T?)
}