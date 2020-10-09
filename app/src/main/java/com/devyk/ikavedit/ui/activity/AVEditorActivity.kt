package com.devyk.ikavedit.ui.activity

import android.os.Environment
import android.os.SystemClock
import android.view.View
import com.devyk.aveditor.utils.LogHelper
import com.devyk.ikavedit.R
import com.devyk.ikavedit.base.BaseActivity
import com.devyk.aveditor.entity.MediaEntity
import com.devyk.aveditor.jni.JNIManager
import com.devyk.aveditor.mediacodec.MediaCodecHelper
import com.devyk.aveditor.stream.packer.PackerType
import com.devyk.aveditor.utils.FileUtils
import com.devyk.aveditor.utils.MediaMergeUtils
import com.devyk.aveditor.utils.ThreadUtils
import com.devyk.aveditor.widget.AVPlayView
import com.devyk.ffmpeglib.AVEditor
import com.devyk.ffmpeglib.callback.ExecuteCallback
import com.devyk.ffmpeglib.entity.AVVideo
import com.devyk.ffmpeglib.entity.LogMessage
import com.devyk.ffmpeglib.entity.OutputOption
import com.devyk.ffmpeglib.util.VideoUitls
import com.devyk.ikavedit.callback.OnFilterItemClickListener
import com.devyk.ikavedit.entity.FilterEntity
import com.devyk.ikavedit.widget.AnimTextView
import com.devyk.ikavedit.widget.dialog.SelectFilterDialog
import com.tencent.mars.xlog.Log
import kotlinx.android.synthetic.main.activity_aveditor.*
import kotlinx.android.synthetic.main.activity_aveditor.player_view
import kotlinx.android.synthetic.main.activity_play.*
import java.io.File

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
                JNIManager.getAVPlayEngine()?.setDataSource(path)
                play()
            }
        }
    }

    private fun play() {
        //TODO -- 开启硬件解码目前会导致播放第二段视频会产生花屏
//        if (MediaCodecHelper.isSupportVideoDMediaCodec())
//            player_view.setMediaCodec(true)
//        else
            player_view.setMediaCodec(false)
        player_view.start()
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


        //播放进度
        player_view.addProgressListener(this)
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
                finish()
            }
            //剪辑
            clip -> {
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

    override fun onFilterItemClick(position: Int, item: FilterEntity) {
        LogHelper.d(TAG, "position:${position} ${item}")
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
        player_view.stop()
    }
}