package com.devyk.aveditor.callback

import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter

/**
 * <pre>
 *     author  : devyk on 2020-08-12 11:46
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is OnSelectFilterListener
 * </pre>
 */
public interface OnSelectFilterListener {
    fun onSelectFilter(gpuImageFilter: GPUImageFilter?)
}