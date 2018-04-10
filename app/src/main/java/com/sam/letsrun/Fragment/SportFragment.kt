package com.sam.letsrun.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Activity.SportActivity
import com.sam.letsrun.R
import kotlinx.android.synthetic.main.fragment_sport.*
import org.jetbrains.anko.support.v4.startActivity

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 运动fragment
 */
class SportFragment : Fragment() {


    private var goalSteps: Int = 10000
    private var currentSteps: Int = 7000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sportButton.setOnClickListener {
            startActivity<SportActivity>()
        }

        showSportProgress()
    }

    /**
     * 动态显示今日步数
     */
    private fun showSportProgress() {
        val percent = (currentSteps * 100.0 / goalSteps).toInt()

        Observable.interval(20, TimeUnit.MILLISECONDS)
                .take(percent + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { s ->
                    sportProgress.percent = s * 0.01f
                    currentStepsView.text = (s * 0.01 * goalSteps).toInt().toString()
                }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
//            showSportProgress()
        }
    }

}// Required empty public constructor
