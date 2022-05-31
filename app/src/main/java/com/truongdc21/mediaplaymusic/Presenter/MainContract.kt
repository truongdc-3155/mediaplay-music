package com.truongdc21.mediaplaymusic.Presenter

import com.truongdc21.mediaplaymusic.Model.Song

interface MainContract {
    interface View {
        fun setAdapter(listSong : MutableList<Song>)
        fun actionStart(title : String , uriImg: String)
        fun actionPause()
        fun actionRemuse()
        fun acionPrevious()
        fun actionNext()
        fun actionCancel()
        fun setTimeLooperSeekbar(timeLooper : Int)
        fun setTimeEndANDInfor (time : Int )
    }
}
