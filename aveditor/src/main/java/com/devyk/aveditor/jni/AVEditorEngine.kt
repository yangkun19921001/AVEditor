package com.devyk.aveditor.jni

import com.devyk.aveditor.entity.MediaEntity


/**
 * <pre>
 *     author  : devyk on 2020-08-15 23:02
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVMergeEngine
 * </pre>
 */
public class AVEditorEngine : IAVEditor {

    /**
     * 开始进行合并
     * @param outPath 输出的音视频文件
     * @param mediaFormat 输出的媒体格式类型
     */
    override external fun avStartMerge(outPath: String, mediaFormat: String)

    /**
     * 合并的进度
     * @return 百分号
     */
    override external fun avMergeProgress(): Int

    /**
     * 添加一个音视频文件到将要合并的队列中
     */
    override external fun addAVFile(path: MediaEntity)

    /**
     * 插入一个媒体到 index 位置
     */
    override external fun insertAVFile(index: Int, media: MediaEntity)

    /**
     * 添加多个媒体文件
     */
    override external fun addAVFiles(medias: ArrayList<MediaEntity>)

    /**
     * 插入多个媒体文件
     */
    override external fun insertAVFiles(index: Int, path: ArrayList<MediaEntity>)

    /**
     * 添加配乐
     */
    override external fun addMusicFile(startTime: Long, endTime: Long, path: String, bgVolume: Int, musicVolume: Int)

    /**
     * 删除一个片段
     */
    override external fun removeAVFile(index: Int)
}