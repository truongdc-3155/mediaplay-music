package com.truongdc21.mediaplaymusic.Presenter

import android.content.ContentResolver
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.Unit.GetMediaStore

class MainPresenter (
    private val context: Context,
    private val contentResolver: ContentResolver ){

    val getListMusic  = MutableLiveData<MutableList<Song>>()

    init {
       getListMusic.value = GetMediaStore.getListMusic(contentResolver)
    }






}
