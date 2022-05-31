package com.truongdc21.mediaplaymusic.Repository

import android.content.ContentResolver
import com.truongdc21.mediaplaymusic.Interface.ISongMedia
import com.truongdc21.mediaplaymusic.Model.Song

class SongRepository (private val iSongMedia: ISongMedia) {

    suspend fun getListSong (contentResolver: ContentResolver): MutableList<Song>{
        return iSongMedia.getListSong(contentResolver)
    }
}
