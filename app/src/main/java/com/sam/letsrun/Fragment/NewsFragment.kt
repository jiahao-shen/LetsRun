package com.sam.letsrun.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.afollestad.materialdialogs.MaterialDialog
import com.sam.letsrun.Adapter.NewsAdapter
import com.sam.letsrun.Model.News
import com.sam.letsrun.Presenter.NewsFragmentPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.NewsFragmentView
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import kotlinx.android.synthetic.main.fragment_news.*
import org.jetbrains.anko.support.v4.toast

/**
 * 新闻Fragment
 */
class NewsFragment : Fragment(), NewsFragmentView {

    private var presenter = NewsFragmentPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.mView = this
        presenter.loadNews()

        newsRefreshLayout.setOnRefreshListener {
            presenter.loadNews()
        }

    }

    override fun loadSuccess(newsList: ArrayList<News>) {
        val adapter = NewsAdapter(newsList)
        newsRecyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        newsRecyclerView.adapter = adapter
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(this.context!!, R.drawable.friend_list_decoration)!!)
        newsRecyclerView.addItemDecoration(decoration)
        newsRefreshLayout.finishRefresh(true)
        adapter.setOnItemClickListener { _, _, position ->
            val news = adapter.getItem(position) as News

            val webDialog = MaterialDialog.Builder(context!!)
                    .customView(R.layout.dialog_webview, true)
                    .positiveText("关闭")
                    .build()

            val rootView: View = webDialog.customView!!
            val webView: WebView = rootView.findViewById(R.id.webView)
            webView.loadUrl(news.url)
            webDialog.show()
        }
    }

    override fun loadError() {
        toast("加载失败,请稍后再试")
        newsRefreshLayout.finishRefresh(false)
    }

    override fun netError() {
        toast("网络异常,请检查")
    }
}// Required empty public constructor
