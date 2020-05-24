package com.devyk.ikavedit

import android.app.Application
import com.devyk.avedit.LogHelper

/**
 * <pre>
 *     author  : devyk on 2020-05-21 16:24
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is App
 * </pre>
 */
public class App : Application() {
    private var TAG = this.javaClass.simpleName;
    override fun onCreate() {
        super.onCreate()
        LogHelper.initLog();
        LogHelper.e(TAG, "xlog init success！");
    }
}