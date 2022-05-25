package com.truongdc21.mediaplaymusic.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.R

class AdapterSong (
    val mListSong : MutableList<Song> ,
    val itemClick : (Song) -> Unit
) : RecyclerView.Adapter<AdapterSong.SongViewholder>()  {

    inner class SongViewholder (val ctView : View): RecyclerView.ViewHolder(ctView){
        val imgSong = ctView.findViewById<ImageView>(R.id.item_ImgSong)
        val tvTitleSong = ctView.findViewById<TextView>(R.id.item_TitleSong)
        val tvAuthorSong = ctView.findViewById<TextView>(R.id.item_Author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_song , parent , false)
        return SongViewholder(view)
    }

    override fun onBindViewHolder(holder: SongViewholder, position: Int) {
        val itemSong = mListSong.get(position)
        holder.imgSong.setBackgroundResource(R.drawable.ic_music)

        holder.tvTitleSong.text = itemSong.Title
        holder.tvAuthorSong.text = itemSong.Author

        holder.itemView.setOnClickListener {
            itemClick.invoke(itemSong)
        }
    }

    override fun getItemCount(): Int {
        return mListSong.size
    }
}

