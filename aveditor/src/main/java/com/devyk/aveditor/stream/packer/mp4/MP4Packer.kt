package com.devyk.aveditor.stream.packer.mp4

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaFormat.*
import com.devyk.aveditor.config.AudioConfiguration
import com.devyk.aveditor.config.VideoConfiguration
import com.devyk.aveditor.jni.JNIManager
import com.devyk.aveditor.stream.packer.Packer
import com.devyk.aveditor.stream.packer.PackerType
import com.devyk.aveditor.utils.LogHelper
import com.devyk.aveditor.utils.LogHelper.TAG
import com.devyk.aveditor.utils.ThreadUtils
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-08-09 13:17
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is MP4Packer
 * </pre>
 */
public class MP4Packer(
    outFilePath: String?,
    aConfiguration: AudioConfiguration?,
    vConfiguration: VideoConfiguration?
) : Packer {
    private var mPacketListener: Packer.OnPacketListener? = null
    private var mMakeMP4Packer: MakeMP4Packer? = null

    private var mTrackAudioIndex = -1;
    private var mTrackVideoIndex = -1;
    private var mFileOutputStream: FileOutputStream? = null
    private var mFileOutputStream2: FileOutputStream? = null
    var mOutFilePath: String? = null
    private var audioConfiguration = AudioConfiguration.createDefault()
    private var videoConfiguration = VideoConfiguration.createDefault()

    init {
        mOutFilePath = outFilePath;
        if (aConfiguration != null) {
            audioConfiguration = aConfiguration
        }
        if (vConfiguration != null) {
            videoConfiguration = vConfiguration
        }
//        mMakeMP4Packer = MakeMP4Packer(outFilePath)

//        var temp = "sdcard/aveditor/123.h264"
//        File(temp).createNewFile()
//        mFileOutputStream = FileOutputStream(temp, true)
////
////
//        var temp2 = "sdcard/aveditor/123.aac"
//        File(temp2).createNewFile()
//        mFileOutputStream2 = FileOutputStream(temp2, true)
    }


    override fun setPacketListener(packetListener: Packer.OnPacketListener) {
        mPacketListener = packetListener
    }

    override fun onVideoData(bb: ByteBuffer?, bi: MediaCodec.BufferInfo?) {
        var data = ByteArray(bi!!.size)
        bb!!.get(data, 0, bi.size)
        JNIManager.getAVMuxerEngine()?.enqueue(data, false, bi.presentationTimeUs)

//        mFileOutputStream?.write(data)
    }

    override fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {

        var data = ByteArray(bi.size)
        bb.get(data, 0, bi.size)
//        mFileOutputStream2?.write(data)
        JNIManager.getAVMuxerEngine()?.enqueue(data, true, bi.presentationTimeUs)
    }

    private fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        val profile = 2 // AAC LC
        val freqIdx = 0x4 // 44100
        val chanCfg = 1 // CPE
        // fill in ADTS data
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF1.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()

    }

    override fun onAudioOutformat(outputFormat: MediaFormat?) {
        super.onAudioOutformat(outputFormat)
        val audioSampleRate = outputFormat?.getInteger(KEY_SAMPLE_RATE)
        val audioChannels = outputFormat?.getInteger(KEY_CHANNEL_COUNT)
        val bitRate = outputFormat?.getInteger(KEY_BIT_RATE)
        LogHelper.e(TAG, "audioSampleRate:${audioSampleRate} audioChannels:${audioChannels} audiRate:${bitRate}")
    }

    override fun onVideoOutformat(outputFormat: MediaFormat?) {
        val videoWidth = outputFormat?.getInteger(KEY_WIDTH)
        val videoHeigth = outputFormat?.getInteger(KEY_HEIGHT)
        val bitRate = outputFormat?.getInteger(KEY_BIT_RATE)
        //这里如果是写入文件的话不用打开
        LogHelper.e(TAG, "onVideoOutformat:${outputFormat.toString()}")
        LogHelper.e(TAG, "videoWidth:${videoWidth} videoHeigth:${videoHeigth} audiRate:${bitRate}")
        super.onVideoOutformat(outputFormat)
    }


    override fun start() {
        Thread {
            JNIManager.getAVMuxerEngine()?.initMuxer(
                mOutFilePath,
                videoConfiguration.width,
                videoConfiguration.height,
                videoConfiguration.fps,
                videoConfiguration.maxBps * 1000,
                audioConfiguration.frequency
                ,
                audioConfiguration.channelCount,
                audioConfiguration.maxBps * 1000
            )
        }.start()
    }

    override fun stop() {
        JNIManager.getAVMuxerEngine()?.close()
//        mFileOutputStream?.close()
//        mFileOutputStream2?.close()
    }


}