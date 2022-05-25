package com.truongdc21.mediaplaymusic.Unit
import android.provider.MediaStore

import android.content.ContentResolver
import android.content.Context
import android.util.Log
import com.truongdc21.mediaplaymusic.Model.Song

object GetMediaStore {

    fun getListMusic(contentResolver: ContentResolver ): MutableList<Song>{
        val mListMusic = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = contentResolver.query(uri,null,selection,null,null)
        if (cursor != null){
            Log.d("test" , "Cusor : ${cursor}")
            while(cursor.moveToNext()){

                with(cursor){
                    val url = if (getColumnIndex(MediaStore.Audio.Media.DATA)<0) ""
                    else cursor.getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val Id =if (getColumnIndex(MediaStore.Audio.Media._ID)<0) ""
                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val title =if (getColumnIndex(MediaStore.Audio.Media.TITLE)<0) ""
                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                    val author = if (getColumnIndex(MediaStore.Audio.Media.ARTIST)<0) ""
                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val albumUri = if (cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)<0) ""
                    else getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    if (url!="") {
                        mListMusic.add(Song(Id.toInt(),title,author,albumUri,url))
                    }
                    Log.d("test" , "TETS : ID = ${Id} || Title : ${title} || Author : ${author} || AlbumUr: ${albumUri} || URL : ${url} ")
                }
            }
        }
        return mListMusic

    }


}
