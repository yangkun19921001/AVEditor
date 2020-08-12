package com.devyk.aveditor.video.filter

import java.nio.FloatBuffer

/**
 * <pre>
 *     author  : devyk on 2020-08-08 13:11
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is IFilter
 * </pre>
 */
public interface IFilter {

    /**
     * 准备工作
     */
    fun onReady(width: Int, height: Int)

    /**
     * 根据传递进来的纹理 ID 进行加工绘制
     */
    fun onDrawFrame(textureId: Int):Int

    /**
     * 销毁执行程序
     */
    fun release()


    fun getVData():FloatBuffer?{

        return null
    }

    fun getFData(): FloatBuffer?{

        return null
    }
}