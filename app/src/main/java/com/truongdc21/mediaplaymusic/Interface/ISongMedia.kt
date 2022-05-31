package com.truongdc21.mediaplaymusic.Interface

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import com.truongdc21.mediaplaymusic.Model.Song

interface ISongMedia {
    suspend fun getListSong (contentResolver: ContentResolver) : MutableList<Song>
}
