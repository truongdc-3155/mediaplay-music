package com.truongdc21.mediaplaymusic.Adapter

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.R
import com.truongdc21.mediaplaymusic.Utils.Constance

class AdapterSong (
    val context: Context,
    val mListSong : MutableList<Song> ,
    val itemClick : (Int , String) -> Unit
) : RecyclerView.Adapter<AdapterSong.SongViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_song , parent , false)
        return SongViewholder(view)
    }

    override fun onBindViewHolder(holder: SongViewholder, position: Int) {
        val itemSong = mListSong.get(position)
        val art = ContentUris.withAppendedId(
            Uri.parse(Constance.ALBUM_EXTERNAL_URL),
            itemSong.UriImg.toLong()
        )
        holder.apply {
            imgSong.setBackgroundResource(R.drawable.ic_music)
            tvTitleSong.text = itemSong.Title
            tvAuthorSong.text = itemSong.Singer
            itemView.setOnClickListener {
                itemClick.invoke(position , itemSong.Title)
            }
            context.let {
                Glide.with(it).load(art).placeholder(R.drawable.ic_music).into(imgSong)
            }
        }
    }

    override fun getItemCount() = mListSong.size

    inner class SongViewholder (ctView : View): RecyclerView.ViewHolder(ctView){
        val imgSong = ctView.findViewById<ImageView>(R.id.item_ImgSong)
        val tvTitleSong = ctView.findViewById<TextView>(R.id.item_TitleSong)
        val tvAuthorSong = ctView.findViewById<TextView>(R.id.item_Author)
    }
}

