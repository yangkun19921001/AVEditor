package com.devyk.aveditor.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.*
import android.opengl.EGL14
import android.opengl.EGLExt
import android.os.Build
import android.text.TextUtils
import android.util.Pair
import com.devyk.aveditor.entity.MediaEntity
import com.devyk.aveditor.utils.LogHelper.TAG
import java.io.*
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.util.*

/**
 * <pre>
 *     author  : devyk on 2020-08-16 18:09
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is MediaMergeUtils
 * </pre>
 */
public object MediaMergeUtils {
    /**
     * Version >= 14
     */
    val AFTER_ICE_CREAM_SANDWICH = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
    /**
     * Version >= 18
     */
    val AFTER_JELLY_BEAN_MR2 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
    /**
     * Version >= 21
     */
    val AFTER_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    /**
     * Version < 16
     */
    val BEFORE_JELLY_BEAN = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
    /**
     * Version < 18
     */
    val BEFORE_JELLY_BEAN_MR2 = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2

    private fun getCacheDir(context: Context): File {
        val extCache = context.externalCacheDir
        return if (extCache != null && extCache.exists()) extCache else context.cacheDir
    }

    /**
     * 注意，这个函数在video文件没写完或者什么异常的情况下，好像会卡死
     *
     * @param videoPath
     * @return
     */
    fun getVideoDuration(videoPath: String): Long {
        val mmr = MediaMetadataRetriever()
        var duration: Long = 0
        try {
            mmr.setDataSource(videoPath)

            val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = java.lang.Long.parseLong(durationStr)
        } catch (ex: Exception) {
            LogHelper.e(TAG,"MediaMetadataRetriever exception ${ex.message}")
        } finally {
            mmr.release()
        }
        return duration
    }

    @Throws(IOException::class)
    fun getVideoFrameCount(input: String): Pair<Int, Int> {
        val extractor = MediaExtractor()
        extractor.setDataSource(input)
        val trackIndex = selectTrack(extractor, false)
        extractor.selectTrack(trackIndex)
        var keyFrameCount = 0
        var frameCount = 0
        while (true) {
            val flags = extractor.sampleFlags
            if (flags > 0 && flags and MediaExtractor.SAMPLE_FLAG_SYNC != 0) {
                keyFrameCount++
            }
            val sampleTime = extractor.sampleTime
            if (sampleTime < 0) {
                break
            }
            frameCount++
            extractor.advance()
        }
        extractor.release()
        return Pair(keyFrameCount, frameCount)
    }

    fun deleteListRecord(list: MutableList<String>?) {
        if (list == null || list.size == 0) {
            return
        }
        for (i in list.indices) {
            val file = File(list[i])
            if (file.exists()) {
                file.delete()
            }
        }
        list.clear()
    }



    fun deleteFile(path: String) {
        if (TextUtils.isEmpty(path)) {
            return
        }
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }


    private class BufferedWritableFileByteChannel private constructor(private val outputStream: OutputStream) :
        WritableByteChannel {

        private var isOpen = true
        private val byteBuffer: ByteBuffer
        private val rawBuffer = ByteArray(BUFFER_CAPACITY)

        init {
            this.byteBuffer = ByteBuffer.wrap(rawBuffer)
        }

        @Throws(IOException::class)
        override fun write(inputBuffer: ByteBuffer): Int {
            val inputBytes = inputBuffer.remaining()

            if (inputBytes > byteBuffer.remaining()) {
                dumpToFile()
                byteBuffer.clear()

                if (inputBytes > byteBuffer.remaining()) {
                    throw BufferOverflowException()
                }
            }

            byteBuffer.put(inputBuffer)

            return inputBytes
        }

        override fun isOpen(): Boolean {
            return isOpen
        }

        @Throws(IOException::class)
        override fun close() {
            dumpToFile()
            isOpen = false
        }

        private fun dumpToFile() {
            try {
                outputStream.write(rawBuffer, 0, byteBuffer.position())
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }

        companion object {
            private val BUFFER_CAPACITY = 1000000
        }
    }

    fun useSurfaceRecord(): Boolean {
        return true
    }


    /**
     * 获取视频缩略图
     */
    fun getVideoThumbnail(filePath: String): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            bitmap = retriever.frameAtTime
        } catch (e: IllegalArgumentException) {
            LogHelper.e(TAG, e.message)
        } catch (e: RuntimeException) {
            LogHelper.e(TAG, e.message)
        } finally {
            try {
                retriever.release()
            } catch (e: RuntimeException) {
                LogHelper.e(TAG, e.message)
            }
        }
        return bitmap
    }


    /**
     * 根据toWidth和toHieght，返回适用于bitmap的srcRect,只裁剪不压缩
     * 裁剪方式为裁上下或两边
     *
     * @param srcRect
     * @param bitmapWidth
     * @param bitmapHeight
     * @param toWidth
     * @param toHeight
     * @return
     */
    fun getCroppedRect(srcRect: Rect?, bitmapWidth: Int, bitmapHeight: Int, toWidth: Float, toHeight: Float): Rect {
        var srcRect = srcRect
        if (srcRect == null) {
            srcRect = Rect()
        }
        val rate = toWidth / toHeight
        val bitmapRate = bitmapWidth / bitmapHeight.toFloat()

        if (Math.abs(rate - bitmapRate) < 0.01) {

            srcRect.left = 0
            srcRect.top = 0
            srcRect.right = bitmapWidth
            srcRect.bottom = bitmapHeight
        } else if (bitmapRate > rate) {
            //裁两边
            val cutRate = toHeight / bitmapHeight.toFloat()
            val toCutWidth = cutRate * bitmapWidth - toWidth
            val toCutWidthReal = toCutWidth / cutRate

            srcRect.left = (toCutWidthReal / 2).toInt()
            srcRect.top = 0
            srcRect.right = bitmapWidth - (toCutWidthReal / 2).toInt()
            srcRect.bottom = bitmapHeight
        } else {
            //裁上下
            val cutRate = toWidth / bitmapWidth.toFloat()
            val toCutHeight = cutRate * bitmapHeight - toHeight
            val toCutHeightReal = toCutHeight / cutRate

            srcRect.left = 0
            srcRect.top = (toCutHeightReal / 2).toInt()
            srcRect.right = bitmapWidth
            srcRect.bottom = bitmapHeight - (toCutHeightReal / 2).toInt()

        }
        return srcRect
    }

    fun isCN(): Boolean {
        val locale = Locale.getDefault()
        return locale == Locale.CHINA || locale == Locale.CHINESE || locale == Locale.SIMPLIFIED_CHINESE
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun eglPresentationTimeANDROID(time: Long) {
        EGLExt.eglPresentationTimeANDROID(
            EGL14.eglGetCurrentDisplay(),
            EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),
            time
        )
    }

    fun getVersionCode(context: Context): Int {
        try {
            val pi = context.packageManager.getPackageInfo(context.packageName, 0)
            return pi.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return 0
        }

    }


    /**
     * 开启一个什么都不做的音频线程来提前申请权限
     */
    fun moniRecordForPermission() {
        AudioThread().start()
    }

    private class AudioThread : Thread() {
        private val AUDIO_SOURCES = intArrayOf(
            MediaRecorder.AudioSource.CAMCORDER,
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.DEFAULT,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            MediaRecorder.AudioSource.VOICE_RECOGNITION
        )

        override fun run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
            try {
                val min_buffer_size = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                var buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER
                if (buffer_size < min_buffer_size) {
                    buffer_size = (min_buffer_size / SAMPLES_PER_FRAME + 1) * SAMPLES_PER_FRAME * 2
                }

                var audioRecord: AudioRecord? = null
                for (source in AUDIO_SOURCES) {
                    try {
                        audioRecord = AudioRecord(
                            source, SAMPLE_RATE,
                            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size
                        )
                        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                            audioRecord = null
                        }
                    } catch (e: Exception) {
                        return
                    }

                    if (audioRecord != null) {
                        break
                    }
                }
                if (audioRecord != null) {
                    try {
                        LogHelper.e(TAG, "AudioThread:start audio recording")
                        audioRecord.startRecording()
                        audioRecord.stop()
                    } finally {
                        audioRecord.release()
                    }
                } else {
                    LogHelper.e(TAG, "failed to initialize AudioRecord")
                }
            } catch (e: Exception) {
                LogHelper.e("AudioThread#run", e.message)
            }

            LogHelper.d(TAG, "AudioThread:finished")
        }

        companion object {
            private val MIME_TYPE = "audio/mp4a-latm"
            private val SAMPLE_RATE = 44100    // 44.1[KHz] is only setting guaranteed to be available on all devices.
            private val BIT_RATE = 64000
            val SAMPLES_PER_FRAME = 1024    // AAC, bytes/frame/channel
            val FRAMES_PER_BUFFER = 25    // AAC, frame/buffer/sec
        }
    }

    @Throws(IOException::class)
    fun combineVideoSegments(videoList: List<MediaEntity>?, outputPath: String) {
        if (videoList == null || videoList.size == 0) {
            return
        }
        if (videoList.size == 1) {
            videoList[0].path?.let { copySingleFile(it, outputPath) }
            return
        }
        val mediaMuxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var extractor: MediaExtractor
        var videoTrackIndex = -1
        var audioTrackIndex = -1
        var preVideoSegmentsTime: Long = 0
        var preAudioSegmentsTime: Long = 0

        var lastVideoFrameTime: Long = 0
        var lastAudioFrameTime: Long = 0

        var videoBuffer: ByteBuffer? = null

        val info = MediaCodec.BufferInfo()
        var muxerStated = false
        for (i in videoList.indices) {
            val path = videoList[i].path
            extractor = MediaExtractor()
            if (path != null) {
                extractor.setDataSource(path)
            }else
                continue
            val videoTrack = selectTrack(extractor, false)
            val audioTrack = selectTrack(extractor, true)

            val videoFormat = extractor.getTrackFormat(videoTrack)
            if (videoTrackIndex < 0) {
                videoTrackIndex = mediaMuxer.addTrack(videoFormat)
                val rotation = if (videoFormat.containsKey(MediaFormat.KEY_ROTATION))
                    videoFormat.getInteger(MediaFormat.KEY_ROTATION)
                else
                    0
                mediaMuxer.setOrientationHint(rotation)
            }
            if (audioTrackIndex < 0 && audioTrack >= 0) {
                val audioFormat = extractor.getTrackFormat(audioTrack)
                audioTrackIndex = mediaMuxer.addTrack(audioFormat)
            }
            if (!muxerStated) {
                muxerStated = true
                mediaMuxer.start()
            }
            //每个视频的值不一致，所以每次都要检查buffer是否够用
            val maxBufferSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
            if (videoBuffer == null || maxBufferSize > videoBuffer.capacity()) {
                videoBuffer = ByteBuffer.allocateDirect(maxBufferSize)
            }
            //写视频帧
            extractor.selectTrack(videoTrack)
            while (true) {
                val sampleTime = preVideoSegmentsTime + extractor.sampleTime
                info.presentationTimeUs = sampleTime
                info.flags = extractor.sampleFlags
                info.size = extractor.readSampleData(videoBuffer!!, 0)
                if (info.size < 0) {
                    break
                }
                LogHelper.i(
                    TAG,
                    "write video:" + info.flags + " time:" + info.presentationTimeUs / 1000 + "ms" + " size:" + info.size
                )
                mediaMuxer.writeSampleData(videoTrackIndex, videoBuffer, info)
                lastVideoFrameTime = sampleTime
                extractor.advance()
            }
            //写音频帧
            if (audioTrackIndex >= 0) {
                extractor.unselectTrack(videoTrack)
                extractor.selectTrack(audioTrack)
                while (true) {
                    val sampleTime = if (preVideoSegmentsTime > preAudioSegmentsTime)
                        preVideoSegmentsTime
                    else
                        preAudioSegmentsTime + extractor.sampleTime
                    info.presentationTimeUs = sampleTime
                    info.size = extractor.readSampleData(videoBuffer, 0)
                    info.flags = extractor.sampleFlags
                    if (info.size < 0) {
                        break
                    }
                    LogHelper.i(
                        TAG,
                        "write audio:" + info.flags + " time:" + info.presentationTimeUs / 1000 + "ms" + " size:" + info.size
                    )
                    mediaMuxer.writeSampleData(audioTrackIndex, videoBuffer, info)
                    lastAudioFrameTime = sampleTime
                    extractor.advance()
                }
            }
            preVideoSegmentsTime = lastVideoFrameTime
            preAudioSegmentsTime = lastAudioFrameTime
            extractor.release()
        }
        mediaMuxer.stop()
        mediaMuxer.release()
    }

    fun selectTrack(extractor: MediaExtractor, audio: Boolean): Int {
        val numTracks = extractor.trackCount
        for (i in 0 until numTracks) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (audio) {
                if (mime!!.startsWith("audio/")) {
                    return i
                }
            } else {
                if (mime!!.startsWith("video/")) {
                    return i
                }
            }
        }
        return -5
    }

    @Throws(IOException::class)
    fun copySingleFile(src: String, dst: String) {
        val from = FileInputStream(src).channel
        val to = FileOutputStream(dst).channel
        from.transferTo(0, from.size(), to)
        from.close()
        to.close()
    }

}

