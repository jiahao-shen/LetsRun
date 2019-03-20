package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.Logger
import com.sam.letsrun.Activity.MainActivity
import com.sam.letsrun.Adapter.MusicAdapter
import com.sam.letsrun.Model.Music
import com.sam.letsrun.Presenter.MusicFragmentPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.MusicFragmentView
import kotlinx.android.synthetic.main.fragment_music.*
import org.jetbrains.anko.support.v4.toast


/**
 * 音乐Fragment
 */
class MusicFragment : Fragment(), MusicFragmentView {

    private var presenter = MusicFragmentPresenter()
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.mView = this
        presenter.loadLocalMusic(mainActivity)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun loadMusicSuccess(musicList: ArrayList<Music>) {
        Logger.e(musicList[1].data + "," + musicList[1].album + "," + musicList[1].artist)
        val adapter = MusicAdapter(musicList)
        musicRecyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        musicRecyclerView.adapter = adapter
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(this.context!!, R.drawable.friend_list_decoration)!!)
        musicRecyclerView.addItemDecoration(decoration)
        musicRefreshLayout.finishRefresh(true)
        adapter.setOnItemClickListener { adapter, view, position ->

        }

    }

    override fun loadMusicFailed() {
        toast("音乐加载失败,请稍后再试")
    }
}// Required empty public constructor
