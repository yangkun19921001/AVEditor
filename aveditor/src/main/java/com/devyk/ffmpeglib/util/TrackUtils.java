package com.devyk.ffmpeglib.util;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

/**
 * <pre>
 *     author  : devyk on 2020-09-28 21:45
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is TrackUtils
 * </pre>
 */
public class TrackUtils {

    private static String TAG = "TrackUtils";

    /**
     * 查找视频轨道
     *
     * @param extractor
     * @return
     */
    public static int selectVideoTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();

        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                Log.d(TAG, "Extractor selected track $i ($mime): $format");
                return i;
            }
        }


        return -1;
    }

    /**
     * 查找音频轨道
     *
     * @param extractor
     * @return
     */
    public static int selectAudioTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();

        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                Log.d(TAG, "Extractor selected track $i ($mime): $format");
                return i;
            }
        }


        return -1;
    }
}
