package com.sam.letsrun.Model

import android.widget.Checkable

data class Music(val data: String,
                 val title: String,
                 val album: String,
                 val artist: String,
                 var selected: Boolean=false)

