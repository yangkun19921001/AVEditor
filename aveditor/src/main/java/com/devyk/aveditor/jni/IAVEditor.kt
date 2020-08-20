package com.devyk.aveditor.jni

import com.devyk.aveditor.entity.MediaEntity


/**
 * <pre>
 *     author  : devyk on 2020-08-15 20:39
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IAVEditor 负责音视频编辑
 * </pre>
 */
public interface IAVEditor {
    /**
     * 添加一个音视频片段
     */
    fun addAVFile(path: MediaEntity)

    /**
     * 插入一个音视频片段
     */
    fun insertAVFile(index: Int, media: MediaEntity)

    /**
     * 添加多段音视频片段
     */
    fun addAVFiles(medias: ArrayList<MediaEntity>)

    /**
     * 插入多段音视频片段
     */
    fun insertAVFiles(index: Int, path: ArrayList<MediaEntity>)

    /**
     * 插入配乐
     */
    fun addMusicFile(startTime: Long, endTime: Long, path: String, bgVolume: Int, musicVolume: Int)

    /**
     * 删除一个片段
     */
    fun removeAVFile(index: Int);

    /**
     * 开始输出音视频文件
     */
    fun avStartMerge(outPath: String, mediaFormat: String)

    /**
     * 输出进度
     */
    fun avMergeProgress(): Int

}