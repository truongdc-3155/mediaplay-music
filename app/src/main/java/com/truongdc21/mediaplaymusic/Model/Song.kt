package com.truongdc21.mediaplaymusic.Model

import java.io.Serializable

data class Song (
    val IdSong : Int,
    val Title : String,
    val Singer : String,
    val UriImg : String,
    val Uri : String
    ): Serializable

