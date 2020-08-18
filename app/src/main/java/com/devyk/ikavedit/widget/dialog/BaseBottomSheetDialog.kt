package com.devyk.ikavedit.widget.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import com.devyk.ikavedit.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * <pre>
 *     author  : devyk on 2020-08-09 23:11
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseBottomSheetDialog
 * </pre>
 */
abstract class BaseBottomSheetDialog : BottomSheetDialogFragment() {
    private var bottomSheet: FrameLayout? = null
    var behavior: BottomSheetBehavior<FrameLayout>? = null


    override fun onStart() {
        super.onStart()

        val dialog = dialog as BottomSheetDialog?
        bottomSheet = dialog!!.delegate.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            val layoutParams = bottomSheet!!.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.height = getHeight()
            bottomSheet!!.layoutParams = layoutParams
            behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior?.peekHeight = getHeight()
            // 初始为展开状态
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var commonDialog = R.layout.dialog_common
        if (getLayoutId() != 0)
            commonDialog = getLayoutId()
        return inflater.inflate(commonDialog, container);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog)
        isCancelable = true

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setCanceledOnTouchOutside(true)
        }
    }

    abstract fun getLayoutId(): Int

    public open fun getHeight(): Int = resources.displayMetrics.heightPixels

    /**
     * dp转px
     *
     * @param context
     * @param dpValue
     * @return
     */
    protected fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}
