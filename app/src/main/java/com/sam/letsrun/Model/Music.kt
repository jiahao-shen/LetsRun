package com.sam.letsrun.Model

data class Music(val data: String,
                 val title: String,
                 val album: String,
                 val artist: String,
                 var selected: Boolean=false)
