package com.truongdc21.mediaplaymusic

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.truongdc21.mediaplaymusic.Adapter.AdapterSong
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.Presenter.MainPresenter
import com.truongdc21.mediaplaymusic.Unit.GetMediaStore
import com.truongdc21.mediaplaymusic.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var mPresenter : MainPresenter
    private lateinit var bingding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bingding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bingding.root)

        mPresenter = MainPresenter(this, contentResolver)
        checkReadStoragePermissions()


    }
    private fun checkReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        mPresenter.getListMusic.observe(this, Observer { mListSong ->
            bingding.apply {
                rvListSong.layoutManager = LinearLayoutManager(this@MainActivity)
                rvListSong.adapter = AdapterSong(mListSong, itemClick = { itemSong ->
                    Toast.makeText(this@MainActivity, "Play Music: ${itemSong.Title}", Toast.LENGTH_SHORT).show()
                    bingding.viewPlayMusic.visibility = View.VISIBLE
                })
            }
        })

    }


}
