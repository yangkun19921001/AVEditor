package com.devyk.ffmpeglib.util;

import android.annotation.TargetApi;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import com.devyk.ffmpeglib.entity.VideoInfo;
import com.devyk.ffmpeglib.util.TrackUtils;

import java.text.DecimalFormat;


/**
 * <pre>
 *     author  : devyk on 2020-09-28 21:32
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoUitls
 * </pre>
 */
public class VideoUitls {

    /**
     * 获取视频信息
     *
     * @param url
     * @return 视频时长（单位微秒）
     */
    public static long getDuration(String url) {
        try {
            MediaExtractor mediaExtractor = new MediaExtractor();
            mediaExtractor.setDataSource(url);
            int videoExt = TrackUtils.selectVideoTrack(mediaExtractor);
            if (videoExt == -1) {
                videoExt = TrackUtils.selectAudioTrack(mediaExtractor);
                if (videoExt == -1) {
                    return 0;
                }
            }

            long res = 0;
            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(videoExt);
            if (mediaFormat.containsKey(MediaFormat.KEY_DURATION))
                res = mediaFormat.getLong(MediaFormat.KEY_DURATION);
            else //时长
                res = 0;
            mediaExtractor.release();
            return res;
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 获取音轨数量
     *
     * @return
     */
    public static int getChannelCount(String url) {
        try {
            MediaExtractor mediaExtractor = new MediaExtractor();
            mediaExtractor.setDataSource(url);
            int audioExt = TrackUtils.selectAudioTrack(mediaExtractor);
            if (audioExt == -1) {
                return 0;
            }
            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(audioExt);
            int channel = 0;
            if (mediaFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT))
                channel = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

            mediaExtractor.release();
            return channel;
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * 获取源数据的宽高
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static VideoInfo getVideoInfo(String url) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(url);
        // 获得时长
        String duration = findMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_DURATION);
        // 获得名称
        String keyTitle = findMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE);
        // 获得媒体类型
        String mimetype = findMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        // 获得码率
        String bitrate = findMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_BITRATE);
        //获取视频宽
        String videoWidth = findMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        //获取视频高
        String videoHeight = findMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        //帧率
        String fps = findMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
        return new VideoInfo(duration, keyTitle, mimetype, bitrate, fps, videoWidth, videoHeight);
    }

    private static String findMetadata(MediaMetadataRetriever metadataRetriever, int type) {
        return metadataRetriever.extractMetadata(type);
    }


    /**
     * @param size
     * @return
     */
    public static String getSize(long size) {
        //获取到的size为：1705230
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize = "";
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = df.format(size / (float) GB) + "GB";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = df.format(size / (float) MB) + "MB";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = df.format(size / (float) KB) + "KB";
        } else {
            resultSize = size + "B   ";
        }

        return resultSize;
    }
}
