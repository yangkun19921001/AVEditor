package com.devyk.ikavedit.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.devyk.ikavedit.R
import com.devyk.ikavedit.callback.OnFilterItemClickListener
import com.devyk.ikavedit.entity.FilterEntity

/**
 * <pre>
 *     author  : devyk on 2020-08-10 16:47
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is FilterAdapter
 * </pre>
 */
public class FilterAdapter : BaseQuickAdapter<FilterEntity, BaseViewHolder> {
    private var onItemClickListener: OnFilterItemClickListener? = null


    constructor(data: MutableList<FilterEntity>?) : super(R.layout.adapter_filter, data)

    override fun convert(holder: BaseViewHolder, item: FilterEntity) {
        if (item.isSelect)
            holder.setVisible(R.id.select_view, true)
        else
            holder.setVisible(R.id.select_view, false)
        holder.setText(R.id.filter_name, item.avFilterType.filterNmae)
            .setImageResource(R.id.filter_thumbnail, item.resId)
            .itemView.setOnClickListener {
            update(holder)
            onItemClickListener?.onFilterItemClick(holder.adapterPosition, item)
            notifyDataSetChanged()
        }
    }

    private fun update(holder: BaseViewHolder) {
        for (index in 0..data.size - 1)
            if (index != holder.adapterPosition && data.get(index).isSelect)
                data.get(index).isSelect = false
            else if (index == holder.adapterPosition)
                data.get(index).isSelect = !data.get(index).isSelect

    }


    public fun setOnFilterItemClickListener(listener: OnFilterItemClickListener) {
        this.onItemClickListener = listener
    }
}