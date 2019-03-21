package com.sam.letsrun.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sam.letsrun.Activity.MainActivity
import com.sam.letsrun.Adapter.MusicAdapter
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.EventMusicList
import com.sam.letsrun.Model.Music
import com.sam.letsrun.Model.MusicController
import com.sam.letsrun.Model.MusicResponse
import com.sam.letsrun.Presenter.MusicFragmentPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.MusicFragmentView
import kotlinx.android.synthetic.main.fragment_music.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast


/**
 * 音乐Fragment
 */
class MusicFragment : Fragment(), MusicFragmentView {

    private var presenter = MusicFragmentPresenter()
    private lateinit var mainActivity: MainActivity

    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicList: ArrayList<Music>
    private var preSelected = -1
    private var currentSelected = -1
    private var isPlaying = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.mView = this

        playButton.setOnClickListener {
            if (isPlaying) {
                isPlaying = false
                playButton.setImageResource(R.drawable.ic_music_start)
                EventBus.getDefault().postSticky(MusicController(Const.MUSIC_PAUSE, currentSelected))
            } else {
                isPlaying = true
                playButton.setImageResource(R.drawable.ic_music_pause)
                if (currentSelected == -1) {
                    currentSelected = 0
                    (musicAdapter.getItem(currentSelected) as Music).selected = true
                    musicAdapter.notifyItemChanged(currentSelected)
                    EventBus.getDefault().postSticky(MusicController(Const.MUSIC_START, currentSelected))
                } else {
                    EventBus.getDefault().postSticky(MusicController(Const.MUSIC_RESUME, currentSelected))
                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessgae(event: MusicResponse) {
        when (event.msg) {
            //播放下一首哥
            Const.MUSIC_COMPLETE -> {
                isPlaying = true
                playButton.setImageResource(R.drawable.ic_music_pause)

                preSelected = currentSelected
                (musicAdapter.getItem(preSelected) as Music).selected = false
                musicAdapter.notifyItemChanged(preSelected)

                currentSelected = (currentSelected + 1) % musicList.size
                (musicAdapter.getItem(currentSelected) as Music).selected = true
                musicAdapter.notifyItemChanged(currentSelected)
                preSelected = currentSelected

                EventBus.getDefault().postSticky(MusicController(Const.MUSIC_START, currentSelected))
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event: EventMusicList) {
        musicList = event.musicList
        if (musicList.size == 0) {
            toast("音乐加载失败,请稍后再试")
        } else {
            musicAdapter = MusicAdapter(musicList)
            musicRecyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
            musicRecyclerView.adapter = musicAdapter
            val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            decoration.setDrawable(ContextCompat.getDrawable(this.context!!, R.drawable.friend_list_decoration)!!)
            musicRecyclerView.addItemDecoration(decoration)
            musicRefreshLayout.finishRefresh(true)
            musicAdapter.setOnItemClickListener { _, _, position ->
                currentSelected = position
                if (preSelected != -1) {
                    (musicAdapter.getItem(preSelected) as Music).selected = false
                    musicAdapter.notifyItemChanged(preSelected)
                }
                (musicAdapter.getItem(currentSelected) as Music).selected = true
                musicAdapter.notifyItemChanged(currentSelected)
                if (currentSelected != preSelected) {
                    preSelected = currentSelected
                    EventBus.getDefault().postSticky(MusicController(Const.MUSIC_START, currentSelected))
                    isPlaying = true
                    playButton.setImageResource(R.drawable.ic_music_pause)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}// Required empty public constructor
