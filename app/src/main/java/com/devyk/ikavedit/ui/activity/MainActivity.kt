package com.devyk.ikavedit.ui.activity

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.devyk.ikavedit.base.BaseActivity
import com.devyk.ikavedit.entity.TabEntity
import com.devyk.ikavedit.ui.fragment.CurCityFragment
import com.devyk.ikavedit.ui.fragment.HomeFragment
import com.devyk.ikavedit.ui.fragment.MeFragment
import com.devyk.ikavedit.ui.fragment.MessageFragment
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import android.widget.Toast
import com.devyk.ikavedit.R
import com.devyk.ikavedit.widget.AnimTextView


/**
 * <pre>
 *     author  : devyk on 2020-08-06 00:11
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is MainActivity
 * </pre>
 */
public class MainActivity : BaseActivity<Int>() {
    private val mTitles = arrayOf("首页", "定位", "", "消息", "我")
    private val mFragments = ArrayList<Fragment>()
    private val mCustomTabEntity = ArrayList<CustomTabEntity>()
    private var mViewPagerAdapter: DouyinHomePageAdapter? = null

    private val mSelectTextSize = 16f
    private val mUnSelectTextSize = 12f


    override fun initData() {
    }

    override fun onContentViewBefore() {
        super.onContentViewBefore()
        setNotTitleBar()
    }

    override fun init() {
        //初始化 TAB 和 UI
        addButtonTabAndFragment();

    }

    private fun addButtonTabAndFragment() {
        //首页
        mFragments.add(HomeFragment.getInstance(mTitles[0]))
        //当前城市
        mFragments.add(CurCityFragment.getInstance(mTitles[1]))
        //消息页
        mFragments.add(MessageFragment.getInstance(mTitles[2]))
        //个人首页
        mFragments.add(MeFragment.getInstance(mTitles[3]))

        mViewPagerAdapter = DouyinHomePageAdapter(supportFragmentManager)
        viewplayer.adapter = mViewPagerAdapter

        for (title in mTitles)
            mCustomTabEntity.add(TabEntity(title))
        douyin_button_tab.setTabData(mCustomTabEntity)

        douyin_button_tab.indicatorWidth = getTextLen(douyin_button_tab.getTitleView(0))
        viewplayer.setCurrentItem(0, false)
    }

    //拿到 text len
    public fun getTextLen(textView: TextView): Float {
        val paint = textView.getPaint()
        paint.measureText(textView.getText().toString())
        return paint.measureText(textView.getText().toString()) / resources.displayMetrics.scaledDensity
    }

    override fun initListener() {
        douyin_button_tab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                var pos = position
                douyin_button_tab.indicatorWidth = getTextLen(douyin_button_tab.getTitleView(position))
                if (pos == 3)
                    pos -= 1
                viewplayer.setCurrentItem(pos, false)
                douyin_button_tab.setTextsize(mSelectTextSize, mUnSelectTextSize)
            }

            override fun onTabReselect(position: Int) {
            }
        })


        video_handle.addOnClickListener (500,object :AnimTextView.OnClickListener{
            override fun onClick(v: View) {
                startActivity(Intent(applicationContext, AVHandleActivity::class.java))
                overridePendingTransition(R.anim.pp_bottom_in, R.anim.no_anim)
            }
        })



    }



    override fun getLayoutId(): Int = R.layout.activity_main


    private inner class DouyinHomePageAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment = mFragments.get(position)
        override fun getCount(): Int = mFragments.size
        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }
    }
}