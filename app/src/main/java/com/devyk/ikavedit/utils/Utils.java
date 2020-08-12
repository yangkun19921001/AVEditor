package com.devyk.ikavedit.utils;

import android.app.Application;
import android.content.Context;

/**
 * <pre>
 *     author  : devyk on 2020-07-16 22:32
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Utils
 * </pre>
 */
public class Utils {
    private static Application sApp;

    public static Context getApp() {
        return sApp;
    }

    public static void init(Application application) {
        sApp = application;
    }

}
