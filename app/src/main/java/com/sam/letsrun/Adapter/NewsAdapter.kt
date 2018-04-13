package com.sam.letsrun.Adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.News
import com.sam.letsrun.R

/**
 * 新闻适配器
 * 多布局
 */
class NewsAdapter(data: ArrayList<News>) : BaseMultiItemQuickAdapter<News, BaseViewHolder>(data) {
    init {
        addItemType(News.SINGLE, R.layout.item_news_single)
        addItemType(News.MULTI, R.layout.item_news_multi)
    }

    override fun convert(helper: BaseViewHolder, item: News) {
        when (helper.itemViewType) {
            News.SINGLE -> {
                helper.setText(R.id.newsTitle, item.title)
                        .setText(R.id.newsDate, item.date)
                        .setText(R.id.newsAuthor, item.author_name)

                GlideApp.with(mContext)
                        .load(item.thumbnail_pic_s)
                        .into(helper.getView(R.id.newsImageView))
            }
            News.MULTI -> {
                helper.setText(R.id.newsTitle, item.title)
                        .setText(R.id.newsDate, item.date)
                        .setText(R.id.newsAuthor, item.author_name)

                GlideApp.with(mContext)
                        .load(item.thumbnail_pic_s)
                        .into(helper.getView(R.id.newsImageView1))

                GlideApp.with(mContext)
                        .load(item.thumbnail_pic_s02)
                        .into(helper.getView(R.id.newsImageView2))

                GlideApp.with(mContext)
                        .load(item.thumbnail_pic_s03)
                        .into(helper.getView(R.id.newsImageView3))
            }
        }
    }
}