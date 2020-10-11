package com.devyk.aveditor.muxer

import android.media.MediaFormat
import android.media.MediaExtractor
import android.media.MediaCodec
import android.media.MediaCodec.BufferInfo
import android.media.MediaMuxer
import android.annotation.TargetApi
import android.util.Log
import java.io.IOException
import java.nio.ByteBuffer


/**
 * <pre>
 *     author  : devyk on 2020-10-10 10:26
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is JavaMp4Muxer Java 端 Mp4 合并
 * </pre>
 */
@TargetApi(18)
public class JavaMp4Muxer(private val mVideoList: ArrayList<String>, private val mOutFilename: String) {
    private val TAG = "VideoComposer"
    private var mMuxer: MediaMuxer? = null
    private val mReadBuf: ByteBuffer
    private var mOutAudioTrackIndex: Int = 0
    private var mOutVideoTrackIndex: Int = 0
    private var mAudioFormat: MediaFormat? = null
    private var mVideoFormat: MediaFormat? = null
    init {
        mReadBuf = ByteBuffer.allocate(1048576)
    }

    fun merge(): Boolean {
        var getAudioFormat = false
        var getVideoFormat = false
        val videoIterator = mVideoList.iterator()

        //--------step 1 MediaExtractor拿到多媒体信息，用于MediaMuxer创建文件
        while (videoIterator.hasNext()) {
            val videoPath = videoIterator.next()
            val extractor = MediaExtractor()

            try {
                extractor.setDataSource(videoPath)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            var trackIndex: Int
            if (!getVideoFormat) {
                trackIndex = this.selectTrack(extractor, "video/")
                if (trackIndex < 0) {
                    Log.e(TAG, "No video track found in $videoPath")
                } else {
                    extractor.selectTrack(trackIndex)
                    mVideoFormat = extractor.getTrackFormat(trackIndex)
                    getVideoFormat = true
                }
            }

            if (!getAudioFormat) {
                trackIndex = this.selectTrack(extractor, "audio/")
                if (trackIndex < 0) {
                    Log.e(TAG, "No audio track found in $videoPath")
                } else {
                    extractor.selectTrack(trackIndex)
                    mAudioFormat = extractor.getTrackFormat(trackIndex)
                    getAudioFormat = true
                }
            }

            extractor.release()
            if (getVideoFormat && getAudioFormat) {
                break
            }
        }

        try {
            mMuxer = MediaMuxer(this.mOutFilename, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (getVideoFormat) {
            mOutVideoTrackIndex = mMuxer!!.addTrack(mVideoFormat!!)
        }
        if (getAudioFormat) {
            mOutAudioTrackIndex = mMuxer!!.addTrack(mAudioFormat!!)
        }
        mMuxer!!.start()
        //--------step 1 end---------------------------//


        //--------step 2 遍历文件，MediaExtractor读取帧数据，MediaMuxer写入帧数据，并记录帧信息
        var ptsOffset = 0L
        val trackIndex = mVideoList.iterator()
        while (trackIndex.hasNext()) {
            val videoPath = trackIndex.next()
            var hasVideo = true
            var hasAudio = true
            val videoExtractor = MediaExtractor()

            try {
                videoExtractor.setDataSource(videoPath)
            } catch (var27: Exception) {
                var27.printStackTrace()
            }

            val inVideoTrackIndex = this.selectTrack(videoExtractor, "video/")
            if (inVideoTrackIndex < 0) {
                hasVideo = false
            }

            videoExtractor.selectTrack(inVideoTrackIndex)
            val audioExtractor = MediaExtractor()

            try {
                audioExtractor.setDataSource(videoPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val inAudioTrackIndex = this.selectTrack(audioExtractor, "audio/")
            if (inAudioTrackIndex < 0) {
                hasAudio = false
            }

            audioExtractor.selectTrack(inAudioTrackIndex)
            val bMediaDone = false
            var presentationTimeUs = 0L
            var audioPts = 0L
            var videoPts = 0L

            while (!bMediaDone) {
                if (!hasVideo && !hasAudio) {
                    break
                }

                val outTrackIndex: Int
                val extractor: MediaExtractor
                val currenttrackIndex: Int
                if ((!hasVideo || audioPts - videoPts <= 50000L) && hasAudio) {
                    currenttrackIndex = inAudioTrackIndex
                    outTrackIndex = mOutAudioTrackIndex
                    extractor = audioExtractor
                } else {
                    currenttrackIndex = inVideoTrackIndex
                    outTrackIndex = mOutVideoTrackIndex
                    extractor = videoExtractor
                }

                mReadBuf.rewind()
                val chunkSize = extractor.readSampleData(mReadBuf, 0)//读取帧数据
                if (chunkSize < 0) {
                    if (currenttrackIndex == inVideoTrackIndex) {
                        hasVideo = false
                    } else if (currenttrackIndex == inAudioTrackIndex) {
                        hasAudio = false
                    }
                } else {
                    if (extractor.sampleTrackIndex != currenttrackIndex) {
                        Log.e(
                            TAG,
                            "WEIRD: got sample from track " + extractor.sampleTrackIndex + ", expected " + currenttrackIndex
                        )
                    }

                    presentationTimeUs = extractor.sampleTime//读取帧的pts
                    if (currenttrackIndex == inVideoTrackIndex) {
                        videoPts = presentationTimeUs
                    } else {
                        audioPts = presentationTimeUs
                    }

                    val info = BufferInfo()
                    info.offset = 0
                    info.size = chunkSize
                    info.presentationTimeUs = ptsOffset + presentationTimeUs//pts重新计算
                    if (extractor.sampleFlags and MediaCodec.BUFFER_FLAG_KEY_FRAME != 0) {
                        info.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                    }

                    mReadBuf.rewind()
                    Log.i(
                        TAG,
                        String.format(
                            "write sample track %d, size %d, pts %d flag %d",
                            *arrayOf(
                                Integer.valueOf(outTrackIndex),
                                Integer.valueOf(info.size),
                                java.lang.Long.valueOf(info.presentationTimeUs),
                                Integer.valueOf(info.flags)
                            )
                        )
                    )

                    val presentationTimeUs = info.presentationTimeUs

                    Log.e(TAG,"合成进度：${presentationTimeUs/1000_000}")

                    mMuxer!!.writeSampleData(outTrackIndex, mReadBuf, info)//写入文件
                    extractor.advance()
                }
            }

            //记录当前文件的最后一个pts，作为下一个文件的pts offset
            ptsOffset += if (videoPts > audioPts) videoPts else audioPts
            ptsOffset += 10000L//前一个文件的最后一帧与后一个文件的第一帧，差10ms，只是估计值，不准确，但能用

            Log.i(TAG, "finish one file, ptsOffset $ptsOffset")

            videoExtractor.release()
            audioExtractor.release()
        }

        if (mMuxer != null) {
            try {
                mMuxer!!.stop()
                mMuxer!!.release()
            } catch (e: Exception) {
                Log.e(TAG, "Muxer close error. No data was written")
            }

            mMuxer = null
        }

        Log.i(TAG, "video join finished")
        return true
    }
    private fun selectTrack(extractor: MediaExtractor, mimePrefix: String): Int {
        val numTracks = extractor.trackCount
        for (i in 0 until numTracks) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString("mime")
            if (mime!!.startsWith(mimePrefix)) {
                return i
            }
        }

        return -1
    }
}
