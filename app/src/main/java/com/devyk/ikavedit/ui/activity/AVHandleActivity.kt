package com.devyk.ikavedit.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.devyk.aveditor.callback.OnSelectFilterListener
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.stream.packer.mp4.MP4Packer
import com.devyk.aveditor.utils.BitmapUtils
import com.devyk.aveditor.utils.TimerUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.ikavedit.R
import com.devyk.ikavedit.base.BaseActivity
import com.devyk.ikavedit.callback.OnFilterItemClickListener
import com.devyk.ikavedit.entity.FilterEntity
import com.devyk.ikavedit.entity.TabEntity
import com.devyk.ikavedit.utils.FileUtils
import com.devyk.ikavedit.utils.intent.OnResultListener
import com.devyk.ikavedit.utils.intent.OpenFileUtil
import com.devyk.ikavedit.utils.intent.StartActivityForResultManager
import com.devyk.ikavedit.widget.AnimTextView
import com.devyk.ikavedit.widget.RecordButton
import com.devyk.ikavedit.widget.dialog.CommonDialog
import com.devyk.ikavedit.widget.dialog.SelectFilterDialog
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import kotlinx.android.synthetic.main.activity_av_handle.*
import java.io.File
import java.util.*

/**
 * <pre>
 *     author  : devyk on 2020-08-06 14:16
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVHandleActivity 音视频处理页面
 * </pre>
 */
public class AVHandleActivity : BaseActivity<Int>(), TimerUtils.OnTimerUtilsListener, AnimTextView.OnClickListener,
    OnResultListener<Intent>, OnFilterItemClickListener {


    private var mCurrentRecordProgress = 0
    private var mCurrentRecordDuration = 0

    private var mOutVideoFilePath =
        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AVEditor/${System.currentTimeMillis()}.mp4"


    private var mSelectFilterDialog:SelectFilterDialog?=null


    private val mFunTitles = arrayOf("拍照", "录像", "拍30秒", "拍60秒", "开直播")
    private val mRecordSpeedTitles = arrayOf("极慢", "慢", "标准", "快", "极快")

    private val mCurTab = 2

    private val mCustomTabEntity = ArrayList<CustomTabEntity>()

    private val mSelectTextSize = 16f
    private val mUnSelectTextSize = 12f

    // 计时器
    private var mTimer = TimerUtils(this, 20)

    override fun onContentViewBefore() {
        super.onContentViewBefore()
        setFullScreen()
    }

    override fun initListener() {
        douyin_button_fun_tab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                douyin_button_fun_tab.setTextsize(mSelectTextSize, mUnSelectTextSize)
            }

            override fun onTabReselect(position: Int) {
            }
        })

        douyin_button_speed_tab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                douyin_button_speed_tab.setTextsize(mSelectTextSize, mUnSelectTextSize)
            }

            override fun onTabReselect(position: Int) {
            }
        })

        camera_filter.addOnClickListener(300, this)
        exit.addOnClickListener(300, this)
        camera_beauty.addOnClickListener(300, this)
        ic_camera_tools.addOnClickListener(300, this)
        switch_camera.addOnClickListener(300, this)
        select_music.addOnClickListener(300, this)
    }

    override fun initData() {

    }

    override fun init() {
        //初始化 底部 功能按钮
        initFunTab()
        //初始化 拍摄 功能按钮
        initRecordSpeedTab()
        //初始化录制进度监听
        initRecordProgress()
        //TODO 初始化水印,一定要在 onResume 生命周期之前初始化，不然会有异常
        initWatermark()
    }

    private fun initWatermark() {
        videoView.addWatermark(Watermark(BitmapUtils.messageToBitmap("DevYK", applicationContext)))
    }

    private fun initRecordProgress() {
        record_button.setOnGestureListener(object : RecordButton.OnGestureListener {
            override fun onDown() {
                mTimer.start(60 * 1000)
                FileUtils.createFileByDeleteOldFile(mOutVideoFilePath)
                videoView.setPaker(MP4Packer(mOutVideoFilePath))
                videoView.startRecord()

            }

            override fun onUp() {
                mTimer.stop()
                line_progress_view.addProgress(mCurrentRecordProgress * 1.0F / mCurrentRecordDuration)
                videoView.stopRecord()
            }

            override fun onClick() {
            }
        })
    }

    private fun initRecordSpeedTab() {
        mCustomTabEntity.clear()
        for (title in mRecordSpeedTitles)
            mCustomTabEntity.add(TabEntity(title))
        douyin_button_speed_tab.setTabData(mCustomTabEntity)
        douyin_button_speed_tab.currentTab = mCurTab
        douyin_button_speed_tab.setTextsize(mSelectTextSize, mUnSelectTextSize)
    }

    private fun initFunTab() {
        mCustomTabEntity.clear()
        for (title in mFunTitles)
            mCustomTabEntity.add(TabEntity(title))
        douyin_button_fun_tab.setTabData(mCustomTabEntity)
        douyin_button_fun_tab.currentTab = mCurTab
        douyin_button_fun_tab.setTextsize(mSelectTextSize, mUnSelectTextSize)
    }


    override fun getLayoutId(): Int = R.layout.activity_av_handle


    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, R.anim.pp_bottom_out)
    }

    override fun update(timer: TimerUtils, currentTime: Int) {
        mCurrentRecordProgress = currentTime
        mCurrentRecordDuration = timer.duration
        line_progress_view.setLoadingProgress(currentTime * 1.0f / timer.duration)
    }

    override fun end(timer: TimerUtils) {
    }


    /**
     * 功能按钮点击事件
     */
    override fun onClick(v: View) {
        when (v) {
            exit -> {
                videoView.stopRecord()
                finish()
            }
            camera_beauty, ic_camera_tools -> {
                var commonDialog = CommonDialog()
                commonDialog.show(supportFragmentManager, this.javaClass.simpleName)
            }
            camera_filter -> {
                mSelectFilterDialog = SelectFilterDialog()
                mSelectFilterDialog?.show(supportFragmentManager, this.javaClass.simpleName)
                mSelectFilterDialog?.setOnFilterItemClickListener(this)
            }
            switch_camera -> {
                videoView.switchCamera()
            }
            select_music -> {
                var forResultManager = StartActivityForResultManager(this)
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = OpenFileUtil.DATA_TYPE_AUDIO
                forResultManager.startActivityForResult(intent, this)
            }
        }
    }

    /**
     * OnResultListener 回调的文件
     */
    override fun onResult(data: Intent?) {
        val filePathByUri = FileUtils.getFilePathByUri(applicationContext, data?.data!!)
        Toast.makeText(this, filePathByUri, Toast.LENGTH_LONG).show()

    }

    /**
     * 点击滤镜回调
     */
    override fun onFilterItemClick(position: Int, item: FilterEntity) {
        videoView?.setGPUImageFilter(item.avFilterType, object : OnSelectFilterListener {
            override fun onSelectFilter(gpuImageFilter: GPUImageFilter?) {
                gpuImageFilter?.let { mSelectFilterDialog?.setSelectFilter(it) }
            }
        })
    }
}