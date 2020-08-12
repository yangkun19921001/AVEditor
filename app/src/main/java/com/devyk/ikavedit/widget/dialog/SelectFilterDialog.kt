package com.devyk.ikavedit.widget.dialog

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devyk.aveditor.utils.ThreadUtils
import com.devyk.aveditor.video.filter.gpuimage.base.GPUImageFilter
import com.devyk.aveditor.video.filter.helper.AVFilterFactory
import com.devyk.aveditor.video.filter.helper.AVGPUImageFliterTools
import com.devyk.ikavedit.R
import com.devyk.ikavedit.callback.OnFilterItemClickListener
import com.devyk.ikavedit.entity.FilterEntity
import com.devyk.ikavedit.ui.adapter.FilterAdapter
import com.devyk.ikavedit.utils.FilterTypeHelper
import com.devyk.ikavedit.widget.DYSeekBar
import kotlinx.android.synthetic.main.activity_av_handle.*
import kotlinx.android.synthetic.main.dialog_select_filter.*

/**
 * <pre>
 *     author  : devyk on 2020-08-09 23:16
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is CommonDialog
 * </pre>
 */
public class SelectFilterDialog : BaseBottomSheetDialog(), OnFilterItemClickListener {


    override fun getLayoutId(): Int = R.layout.dialog_select_filter

    private var mAdapter: FilterAdapter? = null

    private var mFliterLists = ArrayList<FilterEntity>()

    private var mView: View? = null

    private var listener: OnFilterItemClickListener? = null

    /**
     * 对过滤器的处理
     */
    private var mFilterAdjuster: AVGPUImageFliterTools.FilterAdjuster? = null

    override fun getHeight(): Int {
        return super.getHeight() / 3 + 50 * resources.displayMetrics.scaledDensity.toInt()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = super.onCreateView(inflater, container, savedInstanceState)
        initView(mView)
        return mView

    }


    private fun initView(view: View?) {
        var recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView?.let { recyclerView ->
            //            recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = GridLayoutManager(activity, 5)
            initTestData()
            mAdapter = FilterAdapter(mFliterLists)
            recyclerView.adapter = mAdapter
            mAdapter?.setOnFilterItemClickListener(this)
            listener?.let { mAdapter?.setOnFilterItemClickListener(it) }

            recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.left =
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
                    outRect.right =
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
                }
            })
            view?.findViewById<DYSeekBar>(R.id.dy_seekbar)?.progress = 60
            view?.findViewById<DYSeekBar>(R.id.dy_seekbar)?.setOnChangeListener(object :
                DYSeekBar.OnChangeListener {
                override fun onProgressChanged(seekBar: DYSeekBar) {
                    mFilterAdjuster?.adjust(seekBar.progress)
                }

                override fun onTrackingTouchStart(seekBar: DYSeekBar) {
                }

                override fun onTrackingTouchFinish(seekBar: DYSeekBar) {
                }

            })
        }
    }

    private fun initTestData() {
        for (filter in FilterTypeHelper.FILTER_DATA)
            mFliterLists.add(FilterEntity(FilterTypeHelper.FilterType2Thumb(filter), false, filter))
    }

    fun setOnFilterItemClickListener(listener: OnFilterItemClickListener) {
        this.listener = listener
    }

    override fun onFilterItemClick(position: Int, item: FilterEntity) {
        listener?.onFilterItemClick(position, item)
    }

    fun setSelectFilter(gpuImageFilter: GPUImageFilter) {
        mFilterAdjuster = AVFilterFactory.getAdjustFilter(gpuImageFilter)
        if (mFilterAdjuster != null) {
            ThreadUtils.runMainThread {
                mView?.findViewById<DYSeekBar>(R.id.dy_seekbar)?.visibility = View.VISIBLE
            }
            mFilterAdjuster?.adjust(mView?.findViewById<DYSeekBar>(R.id.dy_seekbar)!!.progress)
        } else
            ThreadUtils.runMainThread {
                mView?.findViewById<DYSeekBar>(R.id.dy_seekbar)?.visibility = View.INVISIBLE
            }

    }
}