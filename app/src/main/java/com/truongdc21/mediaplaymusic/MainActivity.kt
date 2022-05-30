package com.truongdc21.mediaplaymusic

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.loader.content.CursorLoader
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongdc21.mediaplaymusic.Adapter.AdapterSong
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.Presenter.MainPresenter
import com.truongdc21.mediaplaymusic.Service.MusicService
import com.truongdc21.mediaplaymusic.Utils.Constance
import com.truongdc21.mediaplaymusic.Utils.GetMediaStore
import com.truongdc21.mediaplaymusic.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity()   {

    private val jobGetListSOng = Job()
    private var isPlaying : Boolean? = null
    private var Position : Int? = null
    private var listSong : MutableList<Song>? = null
    private lateinit var mPresenter : MainPresenter
    private lateinit var binding : ActivityMainBinding
    private var broadcast = object : BroadcastReceiver(){
        override fun onReceive(contextREVEIVER: Context?, intentRECEIVER: Intent?) {
            val bundle = intentRECEIVER?.extras
            if (bundle == null) return

            Position = bundle.getInt(Constance.POSITION_SONG)
            isPlaying = bundle.getBoolean(Constance.STATUS_SONG)
            val actionMusic = bundle.getString(Constance.ACTION_MUSIC)
            actionMusic?.let { listenerActionMusic(it) }
            binding.seekBar.max = bundle.getInt(Constance.ACTION_SEND_TIME_END)
            binding.tvNameOfSong.text = listSong!![Position!!].Title
        }

    }
    private val broadcastMediaTime = object : BroadcastReceiver(){
        override fun onReceive(contextRECEIVER: Context?, intentRECEIVER: Intent?) {
            val timeLooper = intentRECEIVER?.getIntExtra(Constance.ACTION_SEND_TIME_FIRST_LOOP , 0)
            val timeFormat = SimpleDateFormat("mm:ss",Locale.getDefault())
            timeLooper?.let {
                binding.seekBar.progress = it
                binding.tvShowTimePlayingMusic.text = timeFormat.format(it)
            }

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regiterANDUnregisterReceiver(Constance.REGISTER_RECEIVER)
        mPresenter = MainPresenter()
        checkPermissions()
        

    }

    private fun checkPermissions()  {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1000
                )
                return
            }
        }
        setApdapterSong()
    }

    private fun setApdapterSong() = CoroutineScope( jobGetListSOng + Dispatchers.Main ).launch{
        listSong = GetMediaStore.getListMusic(this@MainActivity.contentResolver).await()
        listSong?.let {
            binding.apply {
                rvListSong.layoutManager = LinearLayoutManager(this@MainActivity)
                rvListSong.adapter = AdapterSong(listSong!!, itemClick = { positionSong , title ->
                    Toast.makeText(this@MainActivity, "Play Music: ${title}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, MusicService::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable(Constance.OBJECT_LIST_SONG, listSong as Serializable)
                    bundle.putInt(Constance.OBJECT_POSITION_SONG , positionSong)
                    intent.putExtras(bundle)
                    startService(intent)
                    sendOnSeekbarProgress(listSong!!)
                    clickMediaButtons()

                })
            }
        }


    }

    private fun listenerActionMusic(action : String){
        when (action){
            Constance.ACTION_START -> {
                binding.viewPlayMusic.visibility = View.VISIBLE
                setStatusButtonOnMeidaPlay()
            }
            Constance.ACTION_PAUSE -> {setStatusButtonOnMeidaPlay()}
            Constance.ACTION_REMUSE -> {setStatusButtonOnMeidaPlay()}
            Constance.ACTION_PREVIOUS -> {}
            Constance.ACTION_NEXT -> {}
            Constance.ACTION_CANCEL -> {binding.viewPlayMusic.visibility = View.GONE}
        }
    }
    private fun setStatusButtonOnMeidaPlay(){
       isPlaying?.let {
           if (it)  binding.btnPlayANDPauseMusic.setBackgroundResource(R.drawable.ic_pause2)
           else binding.btnPlayANDPauseMusic.setBackgroundResource(R.drawable.ic_play2)
       }
    }

    private fun sendActionTOService(actionMusic: String) {
         Intent(this, MusicService::class.java).also {
            it.action = actionMusic
            startService(it)
        }
    }

    private fun clickMediaButtons(){
        binding.apply {
            btnPreviousMusic.setOnClickListener {
                sendActionTOService(Constance.ACTION_PREVIOUS)
            }
            btnPlayANDPauseMusic.setOnClickListener{
                isPlaying?.let {
                    if (it) sendActionTOService(Constance.ACTION_PAUSE)
                    else sendActionTOService(Constance.ACTION_REMUSE)
                }
            }
            btnNextMusic.setOnClickListener {
                sendActionTOService(Constance.ACTION_NEXT)
            }
        }
    }

    private fun sendOnSeekbarProgress(listSong : MutableList<Song>){
        binding.seekBar.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val intent = Intent(this@MainActivity , MusicService::class.java)
                val bundle = Bundle()
                bundle.putSerializable(Constance.OBJECT_LIST_SONG, listSong as Serializable)
                bundle.putInt(Constance.SEEKBAR_PROGRESS , seekBar!!.progress)
                intent.putExtras(bundle)
                startService(intent)
            }
        })
    }
    private fun regiterANDUnregisterReceiver(key : String) {
        if (key == "register"){
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcast , IntentFilter(Constance.ACTION_SEND_TO_MAIN))
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastMediaTime , IntentFilter(Constance.ACTION_SEND_TIME))
        } else {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcast)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastMediaTime)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        jobGetListSOng.cancel()
        regiterANDUnregisterReceiver("")
    }


}

