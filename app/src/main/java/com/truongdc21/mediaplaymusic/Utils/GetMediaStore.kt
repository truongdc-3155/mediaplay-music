package com.truongdc21.mediaplaymusic.Utils
import android.provider.MediaStore

import android.content.ContentResolver
import com.truongdc21.mediaplaymusic.Model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

object GetMediaStore {

     fun getListMusic(contentResolver: ContentResolver) : MutableList<Song>{
        val mListMusic = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val curSor = contentResolver.query(uri,null,selection,null,null)
        curSor?.let { cursor ->
            while(cursor.moveToNext()){
                with(cursor){
                    val Id =if (getColumnIndex(MediaStore.Audio.Media._ID)<0) ""
                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val srtTitle =if (getColumnIndex(MediaStore.Audio.Media.TITLE)<0) ""
                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                    val srtSingle = if (getColumnIndex(MediaStore.Audio.Media.ARTIST)<0) ""
                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val srtImgUri = if (cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)<0) ""
                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    val srtUri = if (getColumnIndex(MediaStore.Audio.Media.DATA)<0) ""
                    else cursor.getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    if (srtUri!="") {
                        mListMusic.add(Song(Id.toInt(),srtTitle,srtSingle,srtImgUri,srtUri))
                    }
                }
            }
        }
        return mListMusic
    }
}
