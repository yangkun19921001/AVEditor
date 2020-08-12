package com.devyk.ikavedit.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devyk.ikavedit.R
import com.devyk.ikavedit.base.BaseFragment

/**
 * <pre>
 *     author  : devyk on 2020-08-06 10:38
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is HomeFragment
 * </pre>
 */
public class HomeFragment : BaseFragment() {

    private var mTitle: String? = null


    companion object {
        private var mHomeFragment: HomeFragment? = null
        fun getInstance(title: String): HomeFragment {
            if (mHomeFragment == null)
                mHomeFragment =
                    HomeFragment()
            return mHomeFragment as HomeFragment
        }
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun initData(savedInstanceState: Bundle?) {

    }


}