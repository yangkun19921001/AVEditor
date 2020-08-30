package com.devyk.ikavedit

import android.app.Application
import com.devyk.aveditor.jni.JNIManager
import com.devyk.aveditor.utils.FileUtils
import com.devyk.aveditor.utils.LogHelper
import com.devyk.crash_module.Crash
import com.devyk.crash_module.inter.JavaCrashUtils
import com.devyk.ikavedit.utils.Utils
import java.io.File

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


    private val JAVA_CRASH_PATH = "sdcard/aveditor/JavaCrash"
    private val NATIVE_CRASH_PATH = "sdcard/aveditor/NativeCrash"
    override fun onCreate() {
        super.onCreate()
        LogHelper.initLog();


        Utils.init(this);


        FileUtils.createOrExistsDir(File(JAVA_CRASH_PATH))
        FileUtils.createOrExistsDir(File(NATIVE_CRASH_PATH))
        Crash.CrashBuild(applicationContext).javaCrashPath(JAVA_CRASH_PATH, object : JavaCrashUtils.OnCrashListener {
            override fun onCrash(crashInfo: String?, e: Throwable?) {
                LogHelper.e(TAG, crashInfo);
            }
        }).nativeCrashPath(NATIVE_CRASH_PATH).build()
    }
}