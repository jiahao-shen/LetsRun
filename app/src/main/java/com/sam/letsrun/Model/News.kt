package com.sam.letsrun.Model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by sam on 2018/4/10.
 */
data class News(var type: Int = 0,
                val uniquekey: String,
                val title: String,
                val date: String,
                val category: String,
                val author_name: String,
                val url: String,
                val thumbnail_pic_s: String?,
                val thumbnail_pic_s02: String?,
                val thumbnail_pic_s03: String?) : MultiItemEntity {

    override fun getItemType(): Int {
        return type
    }

    companion object {
        const val SINGLE = 1
        const val MULTI = 3
    }
}

