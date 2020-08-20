package com.devyk.aveditor.mediacodec

import android.media.MediaCodecList
import android.media.MediaCodecInfo



/**
 * <pre>
 *     author  : devyk on 2020-08-20 20:56
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is MediaCodecHelper
 * </pre>
 */
object MediaCodecHelper {

    /**
     * Returns the first codec capable of encoding the specified MIME type, or null if no match was
     * found.
     */
    fun selectCodec(mimeType: String): MediaCodecInfo? {
        val numCodecs = MediaCodecList.getCodecCount()
        for (i in 0 until numCodecs) {
            val codecInfo = MediaCodecList.getCodecInfoAt(i)
            if (!codecInfo.isEncoder) {
                continue
            }
            val types = codecInfo.supportedTypes
            for (j in types.indices) {
                if (types[j].equals(mimeType, ignoreCase = true)) {
                    return codecInfo
                }
            }
        }
        return null
    }
}