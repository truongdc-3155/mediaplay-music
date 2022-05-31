package com.truongdc21.mediaplaymusic

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.truongdc21.mediaplaymusic.Adapter.AdapterSong
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.Presenter.MainContract
import com.truongdc21.mediaplaymusic.Presenter.MainPresenter
import com.truongdc21.mediaplaymusic.Utils.Constance
import com.truongdc21.mediaplaymusic.Utils.Constance.ALBUM_EXTERNAL_URL
import com.truongdc21.mediaplaymusic.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() , MainContract.View{

    private val jobCoroutines = Job()
    private lateinit var mPresenter : MainPresenter
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mPresenter = MainPresenter(jobCoroutines , this , this )
        mPresenter.regiterANDUnregisterReceiver(Constance.REGISTER_RECEIVER)
    }

    override fun setAdapter(listSong: MutableList<Song>) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                return
            }
        }
        binding.apply {
            rvListSong.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListSong.adapter = AdapterSong( this@MainActivity, listSong, itemClick = { positionSong , title ->
                Toast.makeText(this@MainActivity, "Play: ${title}", Toast.LENGTH_SHORT).show()
                mPresenter.sendSongtoService(positionSong)
                changeOnSeekbarProgress()
                clickMediaButtons()
            })
        }
    }

    private fun clickMediaButtons(){
        binding.apply {
            btnPreviousMusic.setOnClickListener {
               mPresenter.sendActionToService(Constance.ACTION_PREVIOUS)
            }
            btnPlayANDPauseMusic.setOnClickListener{
                mPresenter.isPlaying?.let {
                    if (it) mPresenter.sendActionToService(Constance.ACTION_PAUSE)
                    else mPresenter.sendActionToService(Constance.ACTION_REMUSE)
                }
            }
            btnNextMusic.setOnClickListener {
                mPresenter.sendActionToService(Constance.ACTION_NEXT)
            }
        }
    }

    private fun changeOnSeekbarProgress(){
        binding.seekBar.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { mPresenter.sendOnSeekbarProgress(it) }
            }
        })
    }

    override fun actionStart(title : String , uriImg : String) {
        val art = ContentUris.withAppendedId(
            Uri.parse(ALBUM_EXTERNAL_URL),
            uriImg.toLong()
        )
        art.let {
            Glide.with(this).load(art).placeholder(R.drawable.ic_music).into(binding.imgUriMusic)
        }
        binding.apply {
            viewPlayMusic.visibility = View.VISIBLE
            tvNameOfSong .text = title
        }

        setStatusButtonOnMeidaPlay()
    }

    override fun actionPause() {
        setStatusButtonOnMeidaPlay()
    }

    override fun actionRemuse() {
        setStatusButtonOnMeidaPlay()
    }

    override fun acionPrevious() {
        setStatusButtonOnMeidaPlay()
    }

    override fun actionNext() {
        setStatusButtonOnMeidaPlay()
    }

    override fun actionCancel() {
        binding.viewPlayMusic.visibility = View.VISIBLE
    }

    override fun setTimeLooperSeekbar(timeLooper: Int) {
        val timeFormat = SimpleDateFormat("mm:ss",Locale.getDefault())
           timeLooper.let {
               binding.seekBar.progress = it
               binding.tvShowTimePlayingMusic.text = timeFormat.format(it)
           }
    }

    override fun setTimeEndANDInfor(time: Int) {
        val timeFormat = SimpleDateFormat("mm:ss",Locale.getDefault())
        binding.apply {
            seekBar.max = time
            tvShowTimeEND.text = timeFormat.format(time)
        }
    }

    private fun setStatusButtonOnMeidaPlay(){
        mPresenter.isPlaying?.let {
            if (it)  binding.btnPlayANDPauseMusic.setBackgroundResource(R.drawable.ic_pause2)
            else binding.btnPlayANDPauseMusic.setBackgroundResource(R.drawable.ic_play2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jobCoroutines.cancel()
        mPresenter.regiterANDUnregisterReceiver("")
    }
}
