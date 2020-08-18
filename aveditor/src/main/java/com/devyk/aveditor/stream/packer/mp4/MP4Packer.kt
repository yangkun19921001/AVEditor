package com.devyk.aveditor.stream.packer.mp4

import android.annotation.TargetApi
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.os.FileUtils
import android.util.ArrayMap
import com.devyk.aveditor.stream.packer.Packer
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.utils.LogHelper.TAG
import com.tencent.mars.xlog.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and

/**
 * <pre>
 *     author  : devyk on 2020-08-09 13:17
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is MP4Packer
 * </pre>
 */
public class MP4Packer(outFilePath: String?) : Packer {
    private var mPacketListener: Packer.OnPacketListener? = null
    private var mMakeMP4Packer: MakeMP4Packer? = null

    private var mTrackAudioIndex = -1;
    private var mTrackVideoIndex = -1;


    private var mFileOutputStream: FileOutputStream? = null


    init {
        mMakeMP4Packer = MakeMP4Packer(outFilePath)
//        var temp = "sdcard/aveditor/123.h264"
//        File(temp).createNewFile()
//        mFileOutputStream = FileOutputStream(temp, true)
    }


    override fun setPacketListener(packetListener: Packer.OnPacketListener) {
        mPacketListener = packetListener
    }

    override fun onVideoData(bb: ByteBuffer?, bi: MediaCodec.BufferInfo?) {
        if (mTrackVideoIndex != -1 && mMakeMP4Packer?.isStart()!!) {
            LogHelper.e(TAG, "onVideoData:")
            mMakeMP4Packer?.writeSampleData(mTrackVideoIndex, bb!!, bi!!)
        }
    }

    override fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        if (mTrackAudioIndex != -1 && mMakeMP4Packer?.isStart()!!) {
            LogHelper.e(TAG, "onAudioData:")
            mMakeMP4Packer?.writeSampleData(mTrackAudioIndex, bb, bi)
        }
    }


    override fun onAudioOutformat(outputFormat: MediaFormat?) {
        super.onAudioOutformat(outputFormat)
        LogHelper.e(TAG, "onAudioOutformat:${outputFormat.toString()}")
        mTrackAudioIndex = mMakeMP4Packer?.addTrack(outputFormat)!!
        if (mTrackVideoIndex != -1)
            mMakeMP4Packer?.start()
    }

    override fun onVideoOutformat(outputFormat: MediaFormat?) {
        //这里如果是写入文件的话不用打开
        LogHelper.e(TAG, "onVideoOutformat:${outputFormat.toString()}")
        mTrackVideoIndex = mMakeMP4Packer?.addTrack(outputFormat)!!
        if (mTrackAudioIndex != -1)
            mMakeMP4Packer?.start()
        super.onVideoOutformat(outputFormat)
    }


    override fun start() {
        mMakeMP4Packer?.setStart(true)
    }


    override fun stop() {
        mMakeMP4Packer?.release()
        mTrackAudioIndex = -1
        mTrackVideoIndex = -1
        mMakeMP4Packer?.setStart(false)
    }
}