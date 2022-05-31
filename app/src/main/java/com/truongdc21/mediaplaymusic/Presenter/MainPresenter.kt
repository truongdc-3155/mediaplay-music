package com.truongdc21.mediaplaymusic.Presenter

import android.content.*
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.truongdc21.mediaplaymusic.Interface.ISongMedia
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.Repository.SongRepository
import com.truongdc21.mediaplaymusic.Service.MusicService
import com.truongdc21.mediaplaymusic.Utils.Constance
import com.truongdc21.mediaplaymusic.Utils.GetMediaStore
import kotlinx.coroutines.*
import java.io.Serializable

class MainPresenter (
    private val job: Job,
    private val context: Context,
    private val mView : MainContract.View ,

    ) : ISongMedia {
    var isPlaying : Boolean? = null
    private var mPosition : Int ? = null
    private var mListSong : MutableList<Song>? = null
    private var songRepository:  SongRepository

    init {
        songRepository = SongRepository(this)
        CoroutineScope(job + Dispatchers.IO).launch{
           mListSong = songRepository.getListSong(context.contentResolver)
            withContext(Dispatchers.Main){
                mListSong?.let { mView.setAdapter(it) }
            }
        }
    }

    private val mBroadcast = object : BroadcastReceiver(){
        override fun onReceive(contectReceiver: Context?, intentReceiver: Intent?) {
            val bundle = intentReceiver?.extras
            bundle?.let { bundleIt->
                mPosition = bundleIt.getInt(Constance.POSITION_SONG)
                isPlaying = bundleIt.getBoolean(Constance.STATUS_SONG)
                val actionMusic = bundleIt.getString(Constance.ACTION_MUSIC)
                actionMusic?.let { listenerActionMusic(it) }
                val timeEnd = bundleIt.getInt(Constance.ACTION_SEND_TIME_END)
                mView.setTimeEndANDInfor(timeEnd)
            }
        }
    }

    private val mBroadcastTime = object : BroadcastReceiver(){
        override fun onReceive(contextRecceiver: Context?, intentReceiver: Intent?) {
            val timeLooper = intentReceiver?.getIntExtra(Constance.ACTION_SEND_TIME_FIRST_LOOP , 0)
            timeLooper?.let {  mView.setTimeLooperSeekbar(it)}
        }
    }

    private fun listenerActionMusic(action : String) = CoroutineScope( job + Dispatchers.Main).launch{
        when (action){
            Constance.ACTION_START -> {
                mPosition?.let { position ->
                    mListSong?.let { listSong ->
                        mView.actionStart(listSong[position].Title , listSong[position].UriImg)

                    }
                }
            }
            Constance.ACTION_PAUSE -> mView.actionPause()
            Constance.ACTION_REMUSE -> mView.actionRemuse()
            Constance.ACTION_PREVIOUS -> mView.acionPrevious()
            Constance.ACTION_NEXT -> mView.actionNext()
            Constance.ACTION_CANCEL -> mView.actionCancel()
        }
    }


    fun sendSongtoService (Position : Int ) = CoroutineScope(job + Dispatchers.Main).launch{
        val intent = Intent( context, MusicService::class.java)
        val bundle = Bundle()
        bundle.putSerializable(Constance.OBJECT_LIST_SONG,  mListSong as Serializable)
        bundle.putInt(Constance.OBJECT_POSITION_SONG , Position)
        intent.putExtras(bundle)
        context.startService(intent)
    }

    fun sendActionToService (action: String) = CoroutineScope(job + Dispatchers.Main).launch{
        Intent(context, MusicService::class.java).also {
            it.action = action
            context.startService(it)
        }
    }

    fun sendOnSeekbarProgress (seekBar : SeekBar) = CoroutineScope( job + Dispatchers.Main).launch{
        val intent = Intent(context , MusicService::class.java)
        val bundle = Bundle()
        bundle.putSerializable(Constance.OBJECT_LIST_SONG, mListSong as Serializable)
        bundle.putInt(Constance.SEEKBAR_PROGRESS , seekBar.progress)
        mPosition?.let {
            bundle.putInt(Constance.OBJECT_POSITION_SONG , it)
        }
        intent.putExtras(bundle)
        context.startService(intent)
    }

     fun regiterANDUnregisterReceiver( key : String) {
        if (key == "register"){
            LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcast , IntentFilter(Constance.ACTION_SEND_TO_MAIN))
            LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastTime , IntentFilter(Constance.ACTION_SEND_TIME))
        } else {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcast)
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastTime)
        }
    }

    override suspend fun getListSong(contentResolver: ContentResolver): MutableList<Song> {
        return GetMediaStore.getListMusic(contentResolver)
    }
}
