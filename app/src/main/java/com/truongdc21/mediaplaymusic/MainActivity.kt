package com.truongdc21.mediaplaymusic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongdc21.mediaplaymusic.Adapter.AdapterSong
import com.truongdc21.mediaplaymusic.Presenter.MainPresenter
import com.truongdc21.mediaplaymusic.Service.MusicService
import com.truongdc21.mediaplaymusic.Unit.Contans
import com.truongdc21.mediaplaymusic.Unit.GetMediaStore
import com.truongdc21.mediaplaymusic.databinding.ActivityMainBinding
import java.io.Serializable


class MainActivity : AppCompatActivity() {

    private lateinit var mPresenter : MainPresenter
    private lateinit var bingding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bingding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bingding.root)

        mPresenter = MainPresenter()
        checkPermissions()


    }
    private fun checkPermissions() {
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

    private fun setApdapterSong (){
        val listSong = GetMediaStore.getListMusic(this.contentResolver)
        bingding.apply {
            rvListSong.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListSong.adapter = AdapterSong(listSong, itemClick = { positionSong , title ->
                Toast.makeText(this@MainActivity, "Play Music: ${title}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, MusicService::class.java)
                val bundle = Bundle()
                bundle.putSerializable(Contans.OBJECT_LIST_SONG, listSong as Serializable)
                bundle.putInt(Contans.OBJECT_POSITION_SONG , positionSong)
                intent.putExtras(bundle)
                startService(intent)
                bingding.viewPlayMusic.visibility = View.VISIBLE
            })
        }
    }


}
