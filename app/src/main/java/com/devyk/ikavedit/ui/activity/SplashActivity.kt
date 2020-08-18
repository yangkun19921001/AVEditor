package com.devyk.ikavedit.ui.activity

import android.content.Intent
import android.os.Handler
import com.devyk.aveditor.utils.TimerUtils
import com.devyk.ikavedit.R
import com.devyk.ikavedit.base.BaseActivity

/**
 * <pre>
 *     author  : devyk on 2020-08-09 23:54
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is SplashActivity
 * </pre>
 */
public class SplashActivity : BaseActivity<Int>() {

    private val mDelayMillis = 2 * 1000L

    override fun initListener() {
    }

    override fun initData() {
    }

    override fun init() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            startActivity(Intent(this@SplashActivity, PlayActivity::class.java))
            finish()
        }, mDelayMillis)
    }

    override fun onContentViewBefore() {
        super.onContentViewBefore()
        setFullScreen()
    }

    override fun getLayoutId(): Int = R.layout.activity_splash


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}