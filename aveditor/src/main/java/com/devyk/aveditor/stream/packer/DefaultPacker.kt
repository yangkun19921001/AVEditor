package com.devyk.aveditor.stream.packer

import android.media.MediaCodec
import com.devyk.aveditor.stream.PacketType
import java.nio.ByteBuffer
import kotlin.experimental.and

/**
 * <pre>
 *     author  : devyk on 2020-07-16 21:28
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is DefaultPacker 存在 bug
 * </pre>
 */
public class DefaultPacker : Packer {
    override fun start() {

    }

    override fun stop() {
    }

    private var TAG = javaClass.simpleName

    override fun onVideoSpsPpsData(sps: ByteArray, pps: ByteArray, spsPps: PacketType) {
        mPacketListener?.onPacket(sps, pps, PacketType.SPS_PPS)
    }

    override fun onVideoData(bb: ByteBuffer?, bi: MediaCodec.BufferInfo?) {
        bb?.let { buffer ->
            bi?.let { mediaBuffer ->
                buffer.position(mediaBuffer.offset)
                buffer.limit(mediaBuffer.offset + mediaBuffer.size)
                val video = ByteArray(mediaBuffer.size)
                buffer.get(video)
                val tag = video[4].and(0x1f).toInt()

                var keyFrame = PacketType.VIDEO
                if (tag == 0x05) {//关键帧
                    keyFrame = PacketType.KEY_FRAME
                } else {
                    keyFrame = PacketType.VIDEO
                }
                mPacketListener?.onPacket(video, keyFrame)
            }
        }
    }

    private var mPacketListener: Packer.OnPacketListener? = null


    override fun onAudioData(bb: ByteBuffer, bi: MediaCodec.BufferInfo) {
        bb?.let { buffer ->
            bi?.let { mediaBuffer ->
                buffer.position(mediaBuffer.offset)
                buffer.limit(mediaBuffer.offset + mediaBuffer.size)
                val audio = ByteArray(mediaBuffer.size)
                buffer.get(audio)
                mPacketListener?.onPacket(audio, PacketType.AUDIO)
            }
        }
    }

    override fun setPacketListener(packetListener: Packer.OnPacketListener) {
        mPacketListener = packetListener
    }

}