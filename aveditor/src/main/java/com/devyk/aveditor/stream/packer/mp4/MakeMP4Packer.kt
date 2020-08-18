package com.devyk.aveditor.stream.packer.mp4

import android.media.MediaMuxer
import com.devyk.aveditor.muxer.BaseMediaMuxer

/**
 * <pre>
 *     author  : devyk on 2020-07-08 18:04
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is MakeMP4
 * </pre>
 */
public class MakeMP4Packer(path: String?, outType: Int = MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4) :
    BaseMediaMuxer(path, outType) {
}