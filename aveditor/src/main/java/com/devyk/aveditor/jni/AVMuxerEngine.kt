package com.devyk.aveditor.jni

/**
 * <pre>
 *     author  : devyk on 2020-08-20 17:32
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVMuxerEngine
 * </pre>
 */
public class AVMuxerEngine : INativeMuxer {
    override external fun initMuxer(outPath: String?, outFormat: String?)

    override external fun enqueue(byteArray: ByteArray?,isAudio: Boolean)

    override external fun close()
}