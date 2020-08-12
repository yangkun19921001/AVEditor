package com.devyk.ikavedit.callback

import com.devyk.ikavedit.entity.FilterEntity

public interface OnFilterItemClickListener {
        fun onFilterItemClick(position: Int, item: FilterEntity)
    }