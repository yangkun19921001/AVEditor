package com.devyk.ikavedit

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.devyk.avedit.KAVPlayView
import com.devyk.avedit.LogHelper
import com.devyk.ikavedit.widget.DYSeekBar
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.mars.xlog.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var TAG = javaClass.simpleName;


    private var isPause = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleBar()
        setContentView(R.layout.activity_main)
        checkPermission();
//        player_view.setDataSource("http://39.135.34.150:8080/000000001000/1000000001000009115/1.m3u8?channel-id=ystenlive&Contentid=1000000001000009115&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=bd8bb70bdb2b54bd84b587dffa024f7621vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000009115&owchid=ystenlive&owsid=1106497909461209970&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRJ1HPTMtmQf%2bVwcp8RojByB2Rhtn7waHVWUQ9gcJ0mHLEp3xuYtoWp3K%2bdNVn%2bMR4");
        player_view.setDataSource("http://devyk.top/video/%E5%90%8E%E6%B5%AA.mp4");
//        player_view.setDataSource("sdcard/1080.mp4");
//            )
        player_view.start()

        player_view.setOnClickListener {
            isPause = !isPause
            player_view.setPause(isPause)
            if (isPause) {
                dy_seekbar.setDrawThumbStatus(DYSeekBar.STATUS.DRAW)
            } else {
                dy_seekbar.setDrawThumbStatus(DYSeekBar.STATUS.NO_DRAW)
            }
        }

        dy_seekbar.isEnabled = true;
        dy_seekbar.max = 100;
        dy_seekbar.setOnChangeListener(object : DYSeekBar.OnChangeListener {
            override fun onProgressChanged(seekBar: DYSeekBar) {

            }

            override fun onTrackingTouchStart(seekBar: DYSeekBar) {
            }

            override fun onTrackingTouchFinish(seekBar: DYSeekBar) {
//                var seelV = seekBar.progress.toDouble() / dy_seekbar.max.toDouble()
                var isSeek = player_view.seekTo(seekBar.progress.toDouble())
            }
        })

        player_view.addProgressListener(object : KAVPlayView.OnProgressListener {
            override fun onProgressChanged(progress: Int) {
                dy_seekbar.progress = progress;
            }
        })
    }

    private fun setTitleBar() {
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(Color.TRANSPARENT)
        window.setNavigationBarColor(Color.TRANSPARENT)
        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    /**
     * 检查权限
     */
    @SuppressLint("CheckResult")
    private fun checkPermission() {
        var rxPermissions = RxPermissions(this);
        rxPermissions.requestEach(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe {
            if (it.granted) {
                LogHelper.d(TAG, "权限获取成功");
            } else if (it.shouldShowRequestPermissionRationale) {
                LogHelper.d(TAG, "权限获取失败");
            }
        }
    }

    public fun start(view: View) {
        player_view.start()
//        btn_play.visibility = View.GONE
    }
}
