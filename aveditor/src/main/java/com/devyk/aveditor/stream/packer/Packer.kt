package com.devyk.aveditor.stream.packer

import android.media.MediaCodec
import android.media.MediaFormat
import com.devyk.aveditor.entity.Speed
import com.devyk.aveditor.stream.PacketType
import java.nio.ByteBuffer


/**
 * <pre>
 *     author  : devyk on 2020-07-16 21:24
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Packer 对音视频数据打包
 * </pre>
 */
public interface Packer {
    interface OnPacketListener {
        fun onPacket(byteArray: ByteArray, packetType: PacketType)
        fun onPacket(sps: ByteArray?, pps: ByteArray?, packetType: PacketType) {}
    }

    /**
     * 设置打包监听器
     */
    fun setPacketListener(packetListener: OnPacketListener)

    /**
     *处理视频硬编编码器输出的数据
     */
    fun onVideoData(bb: ByteBuffer?, bi: MediaCodec.BufferInfo?)

    /**
     * 处理音频硬编编码器输出的数据
     * */
    fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo)

    /**
     * 处理视频 SPS PPS 数据
     */
    fun onVideoSpsPpsData(sps: ByteArray, pps: ByteArray, spsPps: PacketType) {

    }


    /**
     * 视频输出格式
     */
    fun onVideoOutformat(outputFormat: MediaFormat?) {
        //统一获取
        //下面是获取 SPS PPS 为了
//        val spsb = outputFormat?.getByteBuffer("csd-0")
//        spsb?.let { sps ->
//            var sps = ByteArray(spsb!!.remaining())
//            spsb!!.get(sps, 0, sps.size)
//            val ppsb = outputFormat.getByteBuffer("csd-1")
//            var pps = ByteArray(ppsb!!.remaining())
//            ppsb!!.get(pps, 0, pps.size)
//            onVideoSpsPpsData(sps, pps, PacketType.SPS_PPS)
//        }
    }

    /**
     * 音频输出格式
     */
    fun onAudioOutformat(outputFormat: MediaFormat?) {}

    fun start(speed: Speed);
    fun stop();

}
