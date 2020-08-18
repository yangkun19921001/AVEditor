package com.devyk.aveditor.muxer

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import com.devyk.aveditor.utils.LogHelper
import java.nio.ByteBuffer

/**
 * <pre>
 *     author  : devyk on 2020-07-08 17:55
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseMediaMuxer
 * </pre>
 */
public open class BaseMediaMuxer(path: String?, outType: Int) : IMuxer {


    protected var mMeidaMuxer: MediaMuxer? = null

    private var TAG = javaClass.simpleName

    private var isStart = false

    init {
        init(path, outType)
    }

    final override fun init(path: String?, outType: Int) {
        checkNotNull(path)
        mMeidaMuxer = MediaMuxer(path, outType)
        isStart = false
    }

    override fun start() {
        mMeidaMuxer?.start()
        isStart = true
    }

    override fun addTrack(format: MediaFormat?): Int? = mMeidaMuxer?.addTrack(format!!)

    override fun writeSampleData(trackIndex: Int, byteBuf: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        mMeidaMuxer?.writeSampleData(trackIndex, byteBuf, bufferInfo)
    }

    public fun isStart(): Boolean? = isStart


    public fun setStart(start:Boolean){
        isStart = start
    }

    override fun release() {
        if (!isStart()!!) return
        try {
            mMeidaMuxer?.stop()
            mMeidaMuxer?.release()
            isStart = false
        } catch (error: Exception) {
            LogHelper.e(TAG, error.toString())
        }

    }
}