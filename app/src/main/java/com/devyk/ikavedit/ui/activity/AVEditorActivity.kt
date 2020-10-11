package com.devyk.ikavedit.ui.activity

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Paint
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.devyk.aveditor.callback.OnSelectFilterListener
import com.devyk.aveditor.utils.LogHelper
import com.devyk.ikavedit.R
import com.devyk.ikavedit.base.BaseActivity
import com.devyk.aveditor.entity.MediaEntity
import com.devyk.aveditor.entity.Watermark
import com.devyk.aveditor.jni.JNIManager
import com.devyk.aveditor.utils.BitmapUtils
import com.devyk.aveditor.utils.FileUtils
import com.devyk.aveditor.utils.TimeUtil
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.widget.AVPlayView
import com.devyk.ffmpeglib.AVEditor
import com.devyk.ffmpeglib.callback.ExecuteCallback
import com.devyk.ffmpeglib.entity.AVVideo
import com.devyk.ffmpeglib.entity.LogMessage
import com.devyk.ffmpeglib.entity.VideoInfo
import com.devyk.ffmpeglib.util.VideoUitls
import com.devyk.ikavedit.callback.OnFilterItemClickListener
import com.devyk.ikavedit.entity.FilterEntity
import com.devyk.ikavedit.ui.adapter.ThumbnailAdapter
import com.devyk.ikavedit.widget.AnimTextView
import com.devyk.ikavedit.widget.RangeSlider
import com.devyk.ikavedit.widget.dialog.SelectFilterDialog
import kotlinx.android.synthetic.main.activity_av_handle.*
import kotlinx.android.synthetic.main.activity_aveditor.*
import kotlinx.android.synthetic.main.activity_aveditor.camera_filter
import java.io.File
import android.util.DisplayMetrics
import android.content.Context.WINDOW_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.view.WindowManager
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Context
import android.graphics.Rect
import android.view.Display
import android.graphics.Point


/**
 * <pre>
 *     author  : devyk on 2020-08-16 11:53
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVEditorActivity
 * </pre>
 */
public class AVEditorActivity : BaseActivity<Int>(), AnimTextView.OnClickListener, OnFilterItemClickListener,
    AVPlayView.OnProgressListener {


    //媒体文件时长
    private var mVideoDuration = 0L
    //媒体文件
    private var mAVFiles: ArrayList<MediaEntity>? = null
    //选择滤镜的弹窗
    private var mSelectFilterDialog: SelectFilterDialog? = null
    //视频缩略图集合
    private var mThumbnail: MutableList<File> = mutableListOf<File>()
    //视频信息
    private var mVideoInfo: VideoInfo? = null

    private var index = 0;

    companion object {
        //传递过来的媒体文件
        //多个文件
        public val MEDIAS = "medias"
        //单个文件
        public val MEDIA = "media"
    }

    override fun getLayoutId(): Int = R.layout.activity_aveditor

    /***
     * 拿到媒体文件集合
     */
    fun getAVEditPaths(): ArrayList<MediaEntity>? = intent.getParcelableArrayListExtra(MEDIAS)

    fun getAVEditPath(): String? = intent.getStringExtra(MEDIA)

    /**
     * 拿到媒体文件时长
     */
    fun getAVDuration(): Long {
        var totalDur = 0L
        mAVFiles?.forEach { item ->
            totalDur += item.stopDuration
        }
        return totalDur
    }

    /**
     * setContentView() 之前可以做些什么事儿
     */
    override fun onContentViewBefore() {
        super.onContentViewBefore()
        setFullScreen()
    }

    override fun init() {
        mAVFiles = getAVEditPaths()
        mVideoDuration = getAVDuration()


        var mVideoEntity = java.util.ArrayList<AVVideo>(10)

        mAVFiles?.forEach { mediaEntity ->
            mediaEntity.path?.let { path ->
                mVideoEntity.add(AVVideo(path))
            }
        }

        if (mAVFiles == null || mAVFiles?.size == 0) {
            getAVEditPath()?.let { path ->
                editor_view.setEditSource(path)
                play()
            }
        }
        //初始化裁剪的缩略图
        initThumbnail()
        initWatermark()


    }

    /**
     * 初始化水印
     */
    private fun initWatermark() {
        editor_view.addWatermark(Watermark(BitmapUtils.messageToBitmap("我是视频编辑页面的水印", applicationContext),
            100f  ,
            500f))
    }

    private fun initThumbnail() {
        //缩略图适配器
        mThumbnail.clear()

        //水平布局
        video_thumbnails.setLayoutManager(
            LinearLayoutManager(
                applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        var mThumbnailAdapter = ThumbnailAdapter(mThumbnail)
        video_thumbnails.setAdapter(mThumbnailAdapter)

        //1s 获取一次缩略图
        setThumbnailData(mThumbnailAdapter)


        var startTime = 0L
        var endTime = 0L
        //获取视频元数据
        mVideoInfo = VideoUitls.getVideoInfo(getAVEditPath())
        var totalTime = 1L;
        mVideoInfo?.duration?.let { duration ->
            totalTime = duration.toLong()
        }

        //总时长
        val format = TimeUtil.format(totalTime)
        tv_duration.setText(format)
        tv_end_time.setText(format)
        //滑动截取时间控件改变的回调
        range_seek_bar.setRangeChangeListener(object : RangeSlider.OnRangeChangeListener {
            override fun onRangeChange(view: RangeSlider, type: Int, lThumbIndex: Int, rThumbIndex: Int) {
                startTime = (lThumbIndex.toFloat() / 100 * totalTime).toLong()
                endTime = (rThumbIndex.toFloat() / 100 * totalTime).toLong()
                val duration = endTime - startTime
                runOnUiThread {
                    when (type) {
                        RangeSlider.TYPE_LEFT -> {
                            tv_start_time.setText(TimeUtil.format(startTime))
                            JNIManager.getAVPlayEngine()?.seekTo(startTime.toDouble() / 1000)
                        }
                        RangeSlider.TYPE_RIGHT -> {
                            tv_end_time.setText(TimeUtil.format(endTime))
                            JNIManager.getAVPlayEngine()?.seekTo(endTime.toDouble() / 1000)
                        }
                    }
                    tv_duration.setText(TimeUtil.format(duration))
                }
            }
        })
    }

    private fun setThumbnailData(mThumbnailAdapter: ThumbnailAdapter) {
        var mFile = File("sdcard/aveditor/thumbnail/")
        FileUtils.deleteDirectory(mFile)
        mFile.mkdirs()

        var videoPath = ""
        getAVEditPath()?.let { path ->
            videoPath = path
        }

        if (!FileUtils.isExists(videoPath)) return
        AVEditor.video2pic(
            videoPath,
            "${mFile.absolutePath}/${System.currentTimeMillis()}_%3d.jpg",
            60,
            60,
            1.0f,
            object : ExecuteCallback {
                override fun onStart(executionId: Long?) {
                    initProgressDialog()
                    showProgressDialog()
                }

                override fun onSuccess(executionId: Long) {
                    val e = Log.e(TAG, "video2pic ok")
                    var files = mFile?.listFiles()
                    files.forEach { file ->
                        mThumbnail.add(file)
                    }
                    mThumbnailAdapter.notifyDataSetChanged()
                    dismissProgressDialog()
                }

                override fun onFailure(executionId: Long, error: String?) {
                    Log.e(TAG, "video2pic error:${error}")
                    showMessage(error)
                    dismissProgressDialog()
                }

                override fun onCancel(executionId: Long) {
                }

                override fun onFFmpegExecutionMessage(logMessage: LogMessage?) {
                    Log.d(TAG, logMessage?.text)
                }

                override fun onProgress(progress: Float) {
                    Log.e(TAG, "video2pic onProgress:$progress")
                    updateProgress(progress)
                }
            })
    }

    private fun play() {
        //TODO -- 开启硬件解码目前会导致播放第二段视频会产生花屏
//        if (MediaCodecHelper.isSupportVideoDMediaCodec())
//            player_view.setMediaCodec(true)
//        else
        editor_view.setNativeRender(false)
        editor_view.start()
    }

    override fun initData() {
    }

    override fun initListener() {
        camera_filter.addOnClickListener(500, this)
        back.addOnClickListener(500, this)
        clip.addOnClickListener(500, this)
        change_sound.addOnClickListener(500, this)
        select_bg_music.addOnClickListener(500, this)
        effect.addOnClickListener(500, this)
        txt.addOnClickListener(500, this)
        sticker.addOnClickListener(500, this)
        next.addOnClickListener(500, this)
    }


    override fun onClick(v: View) {
        when (v) {
            //滤镜
            camera_filter -> {
                mSelectFilterDialog = SelectFilterDialog()
                mSelectFilterDialog?.setOnFilterItemClickListener(this)
                mSelectFilterDialog?.show(supportFragmentManager, this::javaClass.toString())
            }
            //返回
            back -> {
                if (fl_edit.isVisible) {
                    setChildViewsStatus(true)
                    fl_edit.visibility = View.GONE
                    return
                }
                finish()
            }
            //剪辑
            clip -> {
                setChildViewsStatus(false)
                editAnima()
            }
            //选择配乐
            select_bg_music -> {
            }
            //特效
            effect -> {
            }
            //文字
            txt -> {
            }
            //贴纸
            sticker -> {
            }
            //下一步
            next -> {
                val outPath = createMergeFilePath();
                FileUtils.createFileByDeleteOldFile(outPath)
//                JNIManager.getAVEditorEngine()?.avStartMerge(outPath, PackerType.MP4.name)
            }
        }
    }

    /**
     * 设置录制过程中 UI 画面上需要隐藏或显示的 View
     */
    private fun setChildViewsStatus(isVisibility: Boolean) {
        var isVisible = View.VISIBLE
        if (isVisibility)
            isVisible = View.VISIBLE
        else
            isVisible = View.GONE


        camera_filter.visibility = isVisible
        clip.visibility = isVisible
        change_sound.visibility = isVisible
        select_bg_music.visibility = isVisible
        effect.visibility = isVisible
        txt.visibility = isVisible
        sticker.visibility = isVisible
        next.visibility = isVisible
    }

    /**
     * 改变 View 显示状态
     */
    private fun changeViewStatus(isVisibility: Boolean, viewchild: View) {
        if (isVisibility) {
            viewchild.visibility = View.VISIBLE
        } else {
            viewchild.visibility = View.INVISIBLE
        }
    }

    override fun onFilterItemClick(position: Int, item: FilterEntity) {
        LogHelper.d(TAG, "position:${position} ${item}")
        editor_view?.setGPUImageFilter(item.avFilterType, object : OnSelectFilterListener {
            override fun onSelectFilter(gpuImageFilter: GPUImageFilter?) {
                gpuImageFilter?.let { mSelectFilterDialog?.setSelectFilter(it) }
            }
        })
    }


    override fun onProgressChanged(progress: Int) {
        LogHelper.e(TAG, "progress:${progress}")
    }

    /**
     * 创建录制文件
     */
    private fun createMergeFilePath(): String =
        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "AVEditor/merge.mp4"


    override fun onDestroy() {
        super.onDestroy()
        editor_view.stop()
    }

    public fun editAnima() {
        fl_edit.visibility = View.VISIBLE
        val propertyValuesHolder1 = PropertyValuesHolder.ofFloat("translationY", 150f, 0f)
        val propertyValuesHolder2 = PropertyValuesHolder.ofFloat("alpha", 0f, 0.2f, 0.5f, 0.75f, 1.0f)
        val propertyValuesHolder3 = PropertyValuesHolder.ofFloat("scaleX", 0f, 0.2f, 0.5f, 0.75f, 1.0f)
        ObjectAnimator.ofPropertyValuesHolder(
            fl_edit,
            propertyValuesHolder1,
            propertyValuesHolder2,
            propertyValuesHolder3
        )
            .setDuration(500).start()

    }

}