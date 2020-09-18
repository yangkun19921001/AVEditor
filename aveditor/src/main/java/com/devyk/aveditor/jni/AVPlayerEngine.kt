package com.devyk.aveditor.jni

import com.devyk.aveditor.entity.MediaEntity
import com.devyk.aveditor.entity.Speed

/**
 * <pre>
 *     author  : devyk on 2020-08-12 16:57
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVPlayerEngine
 * </pre>
 */
public class AVPlayerEngine : IPlayer {


    /**
     * 设置是否硬件解码
     */
    public  override external fun setMediaCodec(isMediacodec: Boolean)


    public external override fun setPlayVolume(v: Int);

    override fun setPlaySpeed(speed: Speed) {
        setPlaySpeed(speed.value)
    }

    private external fun setPlaySpeed(speed: Double);

    /**
     * init 初始化
     */
    public external override fun initSurface(surface: Any)

    /**
     * 设置播放源
     */
    public external override fun setDataSource(source: String?)

    /**
     * 设置播放源
     */
    public external override fun setDataSource(sources: ArrayList<MediaEntity>?)

    /**
     * 播放
     */
    public external override fun start()

    /**
     * 播放
     */
    public external override fun progress(): Double

    /**
     * 暂停
     */
    public external override fun setPause(status: Boolean)

    /**
     * 指定跳转到某个时间点播放
     */
    public external override fun seekTo(seek: Double): Int;

    /**
     * 停止
     */
    public external override fun stop()
}