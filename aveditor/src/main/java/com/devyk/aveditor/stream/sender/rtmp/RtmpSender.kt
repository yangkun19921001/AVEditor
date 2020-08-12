package com.devyk.aveditor.stream.sender.rtmp

import android.util.Log
import com.devyk.aveditor.Contacts
import com.devyk.aveditor.callback.OnConnectListener
import com.devyk.aveditor.stream.PacketType
import com.devyk.aveditor.stream.sender.Sender


import java.util.*

/**
 * <pre>
 *     author  : devyk on 2020-07-16 21:27
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is RtmpSender
 * </pre>
 */
public class RtmpSender : Sender {
    private var TAG = javaClass.simpleName
    private var listener: OnConnectListener? = null
    private var mRtmpUrl: String? = null

    companion object {
        init {
            System.loadLibrary("AVRtmpPush")
        }
    }


    override fun onData(data: ByteArray, type: PacketType) {
        if (type == PacketType.FIRST_AUDIO || type == PacketType.AUDIO) {
            //音频数据
            pushAudio(data, data.size, type.type)
        } else if (type == PacketType.FIRST_VIDEO ||
            type == PacketType.KEY_FRAME ||  type == PacketType.VIDEO) {
            //视频数据
            pushVideo(data, data.size, type.type)
        }
    }


    fun setDataSource(source: String) {
        mRtmpUrl = source
    }

    fun connect() {
        NativeRtmpConnect(mRtmpUrl)

    }

    fun close() {
        NativeRtmpClose()
        onClose()
    }

    fun setOnConnectListener(lis: OnConnectListener) {
        listener = lis
    }


    /**
     * C++ 层调用
     * 开始链接
     */
    fun onConnecting() {
        listener?.onConnecting()
    }

    /**
     * C++ 层调用
     * 连接成功
     */
    fun onConnected() {
        listener?.onConnected()
    }

    /**
     * C++ 层调用
     * 关闭成功
     */
    fun onClose() {
        listener?.onClose()
    }


    /**
     * C++ 层调用
     * 推送失败
     */
    fun onError(errorCode: Int) {
        listener?.onFail(errorCode2errorMessage(errorCode))
    }

    private fun errorCode2errorMessage(errorCode: Int): String {

        var message = "未知错误，请联系管理员!"
        if (errorCode == Contacts.RTMP_CONNECT_ERROR) {
            message = "RTMP server connect fail!"
        } else if (errorCode == Contacts.RTMP_INIT_ERROR) {
            message = "RTMP native init fail!"
        } else if (errorCode == Contacts.RTMP_SET_URL_ERROR) {
            message = "RTMP url set fail!"
        }
        return message
    }


    private external fun NativeRtmpConnect(url: String?);
    private external fun NativeRtmpClose();
    private external fun pushAudio(data: ByteArray, size: Int, type: Int)
    private external fun pushVideo(data: ByteArray, size: Int, isKeyFrame: Int)
    private external fun pushSpsPps(sps: ByteArray, size: Int, pps: ByteArray, size1: Int)
}