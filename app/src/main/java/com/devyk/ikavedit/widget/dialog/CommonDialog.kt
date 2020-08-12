package com.devyk.ikavedit.widget.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devyk.ikavedit.R

/**
 * <pre>
 *     author  : devyk on 2020-08-09 23:16
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is CommonDialog
 * </pre>
 */
public class CommonDialog : BaseBottomSheetDialog() {
    override fun getLayoutId(): Int = 0

    override fun getHeight(): Int {
        return super.getHeight() / 2
    }
}