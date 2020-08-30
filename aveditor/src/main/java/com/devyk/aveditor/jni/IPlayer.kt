package com.devyk.aveditor.jni

import com.devyk.aveditor.entity.MediaEntity
import com.devyk.aveditor.entity.Speed
import java.util.ArrayList

/**
 * <pre>
 *     author  : devyk on 2020-08-12 17:17
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IPlayer
 * </pre>
 */
public interface IPlayer {
    /**
     * init 初始化
     */
    public fun initSurface(surface: Any)

    /**
     * 设置播放源
     */
    public fun setDataSource(source: String?)

    /**
     * 设置多个播放源第一个播放完成之后再播放第二个
     */
    public fun setDataSource(sources: ArrayList<MediaEntity>?)


    /**
     * 设置播放的声音音量
     */
    public fun setPlayVolume(v:Int);

    /**
     * 设置播放的速率
     */
    public fun  setPlaySpeed(speed: Speed)

    /**
     * 播放
     */
    public fun start()

    /**
     * 播放
     */
    public fun progress(): Double

    /**
     * 暂停
     */
    public fun setPause(status: Boolean)

    /**
     * 指定跳转到某个时间点播放
     */
    public fun seekTo(seek: Double): Int;

    /**
     * 停止
     */
    public fun stop()


}