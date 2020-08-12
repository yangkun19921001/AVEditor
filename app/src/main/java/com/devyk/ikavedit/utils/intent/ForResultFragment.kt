package com.devyk.ikavedit.utils.intent

import android.content.Intent
import android.app.Activity.RESULT_OK



/**
 * <pre>
 *     author  : devyk on 2020-08-10 10:56
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is ForResultFragment
 * </pre>
 */
public class ForResultFragment : androidx.fragment.app.Fragment() {

    private var REQUEST_CODE = 0x001
    private var mListener: OnResultListener<Intent>? = null

    companion object {
        var mFragment: ForResultFragment? = null
        public fun getInstance(): ForResultFragment {
            if (mFragment == null)
                mFragment = ForResultFragment()
            return mFragment as ForResultFragment
        }
    }


    // 打开系统的文件选择器
    fun startActivityForResult(intent: Intent,listener: OnResultListener<Intent>) {
        this.mListener = listener
       // specify "audio/mp3" to filter only mp3 files
        startActivityForResult(intent,REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null && data.data != null) {
            mListener?.onResult(data) }
        }

}