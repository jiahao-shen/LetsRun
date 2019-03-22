package com.sam.letsrun.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.Logger
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

    private var currentSelected = -1
    private var isPlaying = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.mView = this
        presenter.loadLocalMusic(activity!!)

        playButton.setOnClickListener {
            if (isPlaying) {
                isPlaying = false
                playButton.setImageResource(R.drawable.ic_music_start)

                EventBus.getDefault().postSticky(MusicController(Const.MUSIC_PAUSE, null))
            } else {
                isPlaying = true
                playButton.setImageResource(R.drawable.ic_music_pause)

                if (currentSelected == -1) {
                    currentSelected = 0
                    (musicAdapter.getItem(currentSelected) as Music).selected = true
                    musicAdapter.notifyItemChanged(currentSelected)

                    EventBus.getDefault().postSticky(MusicController(Const.MUSIC_START, musicList[currentSelected].data))
                } else {
                    EventBus.getDefault().postSticky(MusicController(Const.MUSIC_RESUME, null))
                }
            }
        }

        musicRefreshLayout.setOnRefreshListener {
            presenter.loadLocalMusic(activity!!)
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
                (musicAdapter.getItem(currentSelected) as Music).selected = false
                musicAdapter.notifyItemChanged(currentSelected)
                currentSelected = (currentSelected + 1) % musicList.size
                (musicAdapter.getItem(currentSelected) as Music).selected = true
                musicAdapter.notifyItemChanged(currentSelected)

                EventBus.getDefault().postSticky(MusicController(Const.MUSIC_START, musicList[currentSelected].data))
            }
            Const.MUSIC_PERCENT -> {
                Logger.e(event.info.toString())
                val percent = (100 * (event.info as Double)).toInt()
                playButton.setProgress(percent, false)
            }
        }
    }

    override fun loadLocalMusicSuccess(musics: ArrayList<Music>) {
        musicList = musics

        musicAdapter = MusicAdapter(musicList)
        musicRecyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        musicRecyclerView.adapter = musicAdapter
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(this.context!!, R.drawable.friend_list_decoration)!!)
        musicRecyclerView.addItemDecoration(decoration)
        (musicRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        musicRefreshLayout.finishRefresh(true)
        musicAdapter.setOnItemClickListener { _, _, position ->
            if (currentSelected != -1) {
                (musicAdapter.getItem(currentSelected) as Music).selected = false
                musicAdapter.notifyItemChanged(currentSelected)
            }
            if (currentSelected != position) {
                currentSelected = position
                (musicAdapter.getItem(currentSelected) as Music).selected = true
                musicAdapter.notifyItemChanged(currentSelected)
                isPlaying = true
                playButton.setImageResource(R.drawable.ic_music_pause)

                EventBus.getDefault().postSticky(MusicController(Const.MUSIC_START, musicList[currentSelected].data))

            }
        }
    }

    override fun loadLocalMusicFailed() {
        toast("音乐加载失败,请稍后再试")
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}// Required empty public constructor
