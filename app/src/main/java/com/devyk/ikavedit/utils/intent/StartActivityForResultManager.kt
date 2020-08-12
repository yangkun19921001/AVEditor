package com.devyk.ikavedit.utils.intent

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tbruyelle.rxpermissions2.RxPermissionsFragment

/**
 * <pre>
 *     author  : devyk on 2020-08-10 11:08
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is StartActivityForResultManager
 * </pre>
 *
 * 实现思路参考
 * @see RxPermissions
 */
public class StartActivityForResultManager {
    internal val TAG = StartActivityForResultManager::class.java!!.getSimpleName()


    private var mForResultFragment: ForResultFragment? = null


    constructor(activity: FragmentActivity) {

        this.mForResultFragment = this.getLazySingleton(activity.getSupportFragmentManager())
    }

    constructor(fragment: Fragment) {
        this.mForResultFragment = this.getLazySingleton(fragment.getChildFragmentManager())
    }


    private fun getLazySingleton(fragmentManager: FragmentManager): ForResultFragment? {
        return getForResultFragment(fragmentManager)
    }

    private fun getForResultFragment(fragmentManager: FragmentManager): ForResultFragment? {
        var forResultFragment: Fragment? = this.findResultFragmentFragment(fragmentManager)
        val isNewInstance = forResultFragment == null
        if (isNewInstance) {
            forResultFragment = ForResultFragment.getInstance()
            fragmentManager.beginTransaction().add(forResultFragment, TAG).commitNow()
        } else
            forResultFragment as ForResultFragment
        return forResultFragment
    }

    private fun findResultFragmentFragment(fragmentManager: FragmentManager): Fragment? {
        return fragmentManager.findFragmentByTag(TAG)
    }
    public fun startActivityForResult(intent: Intent, listener: OnResultListener<Intent>) {
        mForResultFragment?.startActivityForResult(intent, listener)
    }
}