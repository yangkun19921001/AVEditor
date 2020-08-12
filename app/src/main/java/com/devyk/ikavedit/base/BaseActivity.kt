package com.devyk.ikavedit.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.Choreographer
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devyk.ikavedit.R
import com.devyk.ikavedit.utils.SPUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import androidx.core.graphics.ColorUtils
import androidx.annotation.ColorInt
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.devyk.ikavedit.utils.QMUIDisplayHelper
import com.devyk.ikavedit.utils.QMUIStatusBarHelper


/**
 * <pre>
 *     author  : devyk on 2020-05-24 23:40
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseActivity
 * </pre>
 */

abstract class BaseActivity<T> : AppCompatActivity() {
    public var TAG = javaClass.simpleName;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onContentViewBefore()

        if (getLayoutId() is Int) {
            setContentView(getLayoutId() as Int)
        } else if (getLayoutId() is View) {
            setContentView(getLayoutId() as View)
        }
        checkPermission()
        init();
        initListener();
        initData();


    }

    /**
     * 在 setContentView 之前需要做的初始化
     */
    protected open fun onContentViewBefore() {

    }

    abstract fun initListener()

    abstract fun initData()

    abstract fun init()

    abstract fun getLayoutId(): T


    protected fun setNotTitleBar() {
        QMUIStatusBarHelper.translucent(this)

    }

    protected fun setFullScreen(){
        QMUIDisplayHelper.setFullScreen(this)
    }

    private fun isLightColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) >= 0.5
    }


    /**
     * 检查权限
     */
    @SuppressLint("CheckResult")
    protected fun checkPermission() {
        if (SPUtils.getInstance().getBoolean(getString(R.string.OPEN_PERMISSIONS))) return
        var rxPermissions = RxPermissions(this);
        rxPermissions.requestEach(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe {
            if (it.granted) {
                SPUtils.getInstance().put(getString(R.string.OPEN_PERMISSIONS), true)
                Toast.makeText(this, getString(R.string.GET_PERMISSION_ERROR), Toast.LENGTH_SHORT).show();
            } else if (it.shouldShowRequestPermissionRationale) {
                Toast.makeText(this, getString(R.string.GET_PERMISSION_ERROR), Toast.LENGTH_SHORT).show();
                SPUtils.getInstance().put(getString(R.string.OPEN_PERMISSIONS), false)
            }
        }
    }


    public fun startTime(timer: Chronometer) {
        var hour = ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60).toInt();
        timer.setFormat("0${hour}:%s");
        timer.start()
    }

    public fun cleanTime(timer: Chronometer) {
        timer?.setBase(SystemClock.elapsedRealtime());
        timer?.stop()
    }


}