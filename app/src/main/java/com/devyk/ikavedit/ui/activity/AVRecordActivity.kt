package com.devyk.ikavedit.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import com.devyk.aveditor.callback.OnSelectFilterListener
import com.devyk.aveditor.entity.MediaEntity
import com.devyk.aveditor.entity.Speed
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.jni.JNIManager
import com.devyk.aveditor.stream.packer.PackerType
import com.devyk.aveditor.utils.BitmapUtils
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.utils.ThreadUtils
import com.devyk.aveditor.utils.TimerUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.ikavedit.R
import com.devyk.ikavedit.base.BaseActivity
import com.devyk.ikavedit.callback.OnFilterItemClickListener
import com.devyk.ikavedit.entity.FilterEntity
import com.devyk.ikavedit.entity.TabEntity
import com.devyk.aveditor.utils.FileUtils
import com.devyk.aveditor.utils.intent.OnResultListener
import com.devyk.aveditor.utils.intent.OpenFileUtil
import com.devyk.aveditor.utils.intent.StartActivityForResultManager
import com.devyk.ffmpeglib.AVEditor
import com.devyk.ffmpeglib.callback.ExecuteCallback
import com.devyk.ffmpeglib.entity.AVVideo
import com.devyk.ffmpeglib.entity.LogMessage
import com.devyk.ffmpeglib.entity.OutputOption
import com.devyk.ikavedit.widget.AnimImageView
import com.devyk.ikavedit.widget.AnimTextView
import com.devyk.ikavedit.widget.CommonTabLayout
import com.devyk.ikavedit.widget.RecordButton
import com.devyk.ikavedit.widget.dialog.CommonDialog
import com.devyk.ikavedit.widget.dialog.SelectFilterDialog
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.tencent.mars.xlog.Log
import kotlinx.android.synthetic.main.activity_av_handle.*
import java.io.File
import java.util.*


/**
 * <pre>
 *     author  : devyk on 2020-08-06 14:16
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVRecordActivity 音视频处理页面
 * </pre>
 */
public class AVRecordActivity : BaseActivity<Int>(), TimerUtils.OnTimerUtilsListener, AnimTextView.OnClickListener,
    View.OnClickListener,
    OnResultListener<Intent>, OnFilterItemClickListener {

    //当前录制的进度
    private var mCurrentRecordProgress = 0
    //当前录制的时间
    private var mCurrentRecordDuration = 0
    //最低录制时间
    private var mMinRecordDuration = 6 * 1000
    //最长录制时间
    private var mMaxRecordDuration = 60 * 1000
    //选择滤镜的弹窗
    private var mSelectFilterDialog: SelectFilterDialog? = null
    //装录制完成的媒体文件
    private val mMedias = ArrayList<MediaEntity>()
    //录制功能
    private val mFunTitles = arrayOf("拍照", "录像", "拍30秒", "拍60秒", "开直播")
    //录制速率
    private val mRecordSpeedTitles = arrayOf("极慢", "慢", "标准", "快", "极快")
    //当前速率 TAB
    private val mCurSpeedTab = 2
    //当前录制功能的 TAB
    private val mCurRecordFunTab = 2
    //用于装 TAB 对象的
    private val mCustomTabEntity = ArrayList<CustomTabEntity>()
    //TAB 选中的文字
    private val mSelectTextSize = 16f
    //TAB 未选中的文字
    private val mUnSelectTextSize = 12f
    //放入录制音频的文件，用于解码->编码 合成到新的文件中
    private var mRecordMusicFilePath: String? = null
    //当前录制的速率
    private var mSpeed = Speed.NORMAL
    //录制的计时器
    private var mTimer = TimerUtils(this, 20)
    //合并输出的视频
    private var mMergetOutPath = ""


    /**
     * setContentView() 之前可以做些什么事儿
     */
    override fun onContentViewBefore() {
        super.onContentViewBefore()
        setFullScreen()

    }


    /**
     * 当前 UI XML
     */
    override fun getLayoutId(): Int = R.layout.activity_av_handle

    /**
     * 初始化监听器
     */
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
                when (douyin_button_speed_tab.getTitleView(position).text) {
                    mRecordSpeedTitles[0] -> {
                        mSpeed = Speed.VERY_SLOW
                    }
                    mRecordSpeedTitles[1] -> {
                        mSpeed = Speed.SLOW
                    }
                    mRecordSpeedTitles[2] -> {
                        mSpeed = Speed.NORMAL
                    }
                    mRecordSpeedTitles[3] -> {
                        mSpeed = Speed.FAST
                    }
                    mRecordSpeedTitles[4] -> {
                        mSpeed = Speed.VERY_FAST
                    }
                    else -> {
                        mSpeed = Speed.NORMAL
                    }
                }
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
        record_delete.addOnClickListener(500, this)
        record_done.addOnClickListener(100, this)


    }

    /**
     * 初始化一些默认数据
     */
    override fun initData() {

    }

    /**
     * 初始化一些功能
     */
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

    /**
     * 初始化水印
     */
    private fun initWatermark() {
        videoView.addWatermark(Watermark(BitmapUtils.messageToBitmap("DevYK", applicationContext)))
    }


    /**
     * 设置录制过程中 UI 画面上需要隐藏或显示的 View
     */
    private fun setChildViewsStatus(isVisibility: Boolean, view: View) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val viewchild = view.getChildAt(i)
                if (viewchild is AnimTextView || viewchild is CommonTabLayout || viewchild is AnimImageView) {
                    changeViewStatus(isVisibility, viewchild)
                }
                setChildViewsStatus(isVisibility, viewchild)
            }
        }
    }

    /**
     * 改变 View 显示状态
     */
    private fun changeViewStatus(isVisibility: Boolean, viewchild: View) {
        ThreadUtils.runMainThread {
            if (isVisibility) {
                viewchild.visibility = View.VISIBLE
            } else {
                viewchild.visibility = View.INVISIBLE
            }
        }
    }


    /**
     * 初始化录制进度
     */
    private fun initRecordProgress() {

        record_button.setOnGestureListener(object : RecordButton.OnGestureListener {

            override fun onDown() {
                LogHelper.e(TAG, "开始录制")
                mTimer.start((mMaxRecordDuration / mSpeed.value).toInt())
                val outVideoFilePath = createRecordFilePath()
                FileUtils.createFileByDeleteOldFile(outVideoFilePath)
                videoView.setPaker(PackerType.MP4, outVideoFilePath)
                videoView.setRecordAudioSource(mRecordMusicFilePath)
                videoView.startRecord(mSpeed)
                mMedias.add(MediaEntity(outVideoFilePath, 0, 0))
                setChildViewsStatus(false, window.decorView)
                if (line_progress_view.isExist()) {
                    record_done.visibility = View.VISIBLE
                }
            }

            override fun onUp() {
                val name = Thread.currentThread().name
                videoView.stopRecord()
                mTimer.stop()
                mMedias.get(mMedias.size - 1).stopDuration = mCurrentRecordProgress.toLong()
                line_progress_view.addProgress(mCurrentRecordProgress * 1.0F / mCurrentRecordDuration)
                setChildViewsStatus(true, window.decorView)
                if (line_progress_view.isExist())
                    ic_camera_phone_album.visibility = View.GONE
                else
                    ic_camera_phone_album.visibility = View.VISIBLE
            }


            override fun onClick() {
            }
        })
    }

    /**
     * 初始化录制速率 TAB
     */
    private fun initRecordSpeedTab() {
        mCustomTabEntity.clear()
        for (title in mRecordSpeedTitles)
            mCustomTabEntity.add(TabEntity(title))
        douyin_button_speed_tab.setTabData(mCustomTabEntity)
        douyin_button_speed_tab.currentTab = mCurSpeedTab
        douyin_button_speed_tab.setTextsize(mSelectTextSize, mUnSelectTextSize)
    }

    /**
     * 初始化录制功能 TAB
     */
    private fun initFunTab() {
        mCustomTabEntity.clear()
        for (title in mFunTitles)
            mCustomTabEntity.add(TabEntity(title))
        douyin_button_fun_tab.setTabData(mCustomTabEntity)
        douyin_button_fun_tab.currentTab = mCurRecordFunTab
        douyin_button_fun_tab.setTextsize(mSelectTextSize, mUnSelectTextSize)
    }


    /**
     * 返回
     */
    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, R.anim.pp_bottom_out)
    }

    /**
     * 录制进度更新回调
     */
    override fun update(timer: TimerUtils, currentTime: Int) {
        mCurrentRecordProgress = currentTime
        mCurrentRecordDuration = timer.duration
        line_progress_view.setLoadingProgress(currentTime * 1.0f / timer.duration)
        if (currentTime > mMinRecordDuration) {
            record_done.visibility = View.VISIBLE;
        }
    }

    /**
     * 录制结束
     */
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
                intent.type = OpenFileUtil.DATA_TYPE_ALL
                forResultManager.startActivityForResult(intent, this)
            }
            record_done -> {
                //TODO 这里先不做删除，以防后续返回
//                line_progress_view.clear()
//                record_done.visibility = View.GONE
//                record_delete.visibility = View.GONE
//                ic_camera_phone_album.visibility = View.VISIBLE

                mRecordMusicFilePath = null
                select_music.setText(R.string.select_music)
                select_music.setMarqueeEnable(false)
                if (mMedias.isNotEmpty()) {
                    mergeVideo()
                }
            }
            record_delete -> {
                if (mMedias.isNotEmpty()) {
                    val mediaEntine = mMedias.removeAt(mMedias.size - 1)
                    mediaEntine?.let {
                        FileUtils.deleteFile(mediaEntine.path)
                    }
                    line_progress_view.deleteProgress()
                }

                if (File(mMergetOutPath).exists()){
                    File(mMergetOutPath).delete()
                }

                if (!line_progress_view.isExist()) {
                    record_done.visibility = View.GONE
                    record_delete.visibility = View.GONE
                    ic_camera_phone_album.visibility = View.VISIBLE
                    select_music.setText(R.string.select_music)
                    select_music.setMarqueeEnable(false)
                }

            }
        }
    }

    private fun mergeVideo() {
        if (mMedias.size == 1) {
            mMedias.get(0).path?.let { toPlayUI(it) }
            return
        }

        var mVideoEntity = java.util.ArrayList<AVVideo>()
        var mVidePath = java.util.ArrayList<String>()
        mMedias.forEach { mediaEntity ->
            mediaEntity.path?.let { path ->
                mVideoEntity.add(AVVideo(path))
                mVidePath.add(path)
            }
        }

        if (mVideoEntity.size > 0) {
            //如果已经存在该文件那么不处理
            if (!mMergetOutPath.isEmpty()) {
                if (File(mMergetOutPath).exists()) {
                    toPlayUI(mMergetOutPath)
                    return
                }
            }
            //判断这个 dir 是否存在，不存在 mkdir
            var filePath = File("sdcard/aveditor")
            if (!filePath.exists())
                filePath.mkdirs()

            //统一输出 720*1280
            mMergetOutPath = "${filePath.absolutePath}/outmerge_${System.currentTimeMillis()}.mp4"
            val outputOption = OutputOption(mMergetOutPath);
            outputOption.setWidth(720)
            outputOption.setHeight(1280)

            //这部分分辨率，码率都一样所以直接在 Java 端进行合并
            JNIManager.getAVJavaMuxer()?.javaMergeVieo(mVidePath,mMergetOutPath)

            toPlayUI(mMergetOutPath)
            //开始合并
//            AVEditor.merge(mVideoEntity, outputOption, object : ExecuteCallback {
//                override fun onSuccess(executionId: Long) {
//                    dismissProgressDialog()
//                    toPlayUI(mMergetOutPath)
//
//                }
//
//                override fun onFailure(executionId: Long, error: String?) {
//                    dismissProgressDialog()
//                    showMessage(error)
//
//                }
//
//                override fun onCancel(executionId: Long) {
//                    dismissProgressDialog()
//                }
//
//                override fun onProgress(v: Float) {
//                    updateProgress(v)
//                }
//
//                override fun onStart(executionId: Long?) {
//                    initProgressDialog()
//                    showProgressDialog()
//                }
//
//                override fun onFFmpegExecutionMessage(logMessage: LogMessage?) {
//                    Log.d(TAG, "onFFmpegExecutionMessage:${logMessage?.text}")
//                }
//            })
        }
    }

    private fun toPlayUI(outPath: String) {
        val intent = Intent(this, AVEditorActivity::class.java)
        intent.putExtra(AVEditorActivity.MEDIA, outPath)
        startActivity(intent)
    }

    /**
     * 封装 startActivityForResult 函数，接收传递过来的数据
     * @see 等同于 onActivityResult
     * OnResultListener 回调的文件
     */
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val selectMusicFilePath = data?.data?.let { FileUtils.getFilePathByUri(applicationContext, it) }
        selectMusicFilePath?.let { filePath ->
            select_music.setText(filePath)
            select_music.setMarqueeEnable(true)
            mRecordMusicFilePath = filePath
        }
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

    override fun onDestroy() {
        videoView.stopPreview()
        super.onDestroy()
        mRecordMusicFilePath = null
    }

    /**
     * 创建录制文件
     */
    private fun createRecordFilePath(): String =
        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AVEditor/${System.currentTimeMillis()}.mp4"

}