package com.devyk.ffmpeglib.entity;

/**
 * <pre>
 *     author  : devyk on 2020-09-30 14:48
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoInfo
 * </pre>
 */
 public class VideoInfo{

    public String duration;
    public String keyTitle;
    public String mimetype;
    public String bitrate;
    public String fps;
    public String videoWidth;
    public String videoHeight;

    public VideoInfo(String duration, String keyTitle, String mimetype, String bitrate, String fps, String videoWidth, String videoHeight) {
        this.duration = duration;
        this.keyTitle = keyTitle;
        this.mimetype = mimetype;
        this.bitrate = bitrate;
        this.fps = fps;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
    }

    public VideoInfo() {
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle) {
        this.keyTitle = keyTitle;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public String getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(String videoWidth) {
        this.videoWidth = videoWidth;
    }

    public String getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(String videoHeight) {
        this.videoHeight = videoHeight;
    }
}