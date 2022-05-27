package com.truongdc21.mediaplaymusic.Service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.ContactsContract
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.R
import com.truongdc21.mediaplaymusic.Receiver.MyReceiver
import com.truongdc21.mediaplaymusic.Unit.Contans
import java.text.FieldPosition
import java.util.*
import kotlin.random.Random

class MusicService  : Service() {

    private var mediaPlayer : MediaPlayer? = null
    private var isPlaying : Boolean = false
    private var PositionSong = 0
    private var mListSong = mutableListOf<Song>()
    private var managerCompat : NotificationManagerCompat? = null

    override fun onCreate() {
        super.onCreate()
        managerCompat = NotificationManagerCompat.from(this)
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras
        if (bundle != null) {
            mListSong = bundle.get(Contans.OBJECT_LIST_SONG) as MutableList<Song>
            PositionSong = bundle.getInt(Contans.OBJECT_POSITION_SONG)
            startMusic(PositionSong)
        }
        intent?.action?.let { listenerActionMusic(it) }
        return START_NOT_STICKY
    }

    private fun startMusic(positionSong : Int) {
        if (isPlaying == true) mediaPlayer?.release()
        val myUri = Uri.parse(mListSong[positionSong].Uri)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, myUri)
            prepare()
            start()
        }
        isPlaying = true
        sendNotification()
    }

    private fun sendNotification(){
        val bitmap = BitmapFactory.decodeResource(Resources.getSystem() , R.drawable.ic_music)
        val mediaSessionCompat = MediaSessionCompat(this, "tag")
        val notification = NotificationCompat.Builder(this, Contans.CHANEL_ID).apply {
             setSmallIcon(R.drawable.ic_small_music)
             setContentTitle(mListSong[PositionSong].Title)
             setContentText(mListSong[PositionSong].Singer)
             setLargeIcon(bitmap)

             addAction(R.drawable.ic_back , "Previous" , getPendingItent(this@MusicService , Contans.ACTION_PREVIOUS))
             if (isPlaying){
                addAction(R.drawable.ic_pause2 , "Pause", getPendingItent(this@MusicService , Contans.ACTION_PAUSE) )
             }else{
                addAction(R.drawable.ic_play , "Pause", getPendingItent(this@MusicService , Contans.ACTION_REMUSE) )
             }
             addAction(R.drawable.ic_next , "Next" , getPendingItent(this@MusicService , Contans.ACTION_NEXT))

             addAction(R.drawable.ic_close , "Close" , getPendingItent(this@MusicService , Contans.ACTION_CANCEL))

             setStyle(androidx.media.app.NotificationCompat
                .MediaStyle()
                .setShowActionsInCompactView(1 ,3)
                .setMediaSession(mediaSessionCompat.sessionToken)
            )
        }.build()
        managerCompat?.notify(Contans.CHANEL_COMPAT ,notification)
    }

    private fun listenerActionMusic (action : String ) {
        Contans.apply {
            when(action){
                ACTION_PREVIOUS -> actionPrevious()
                ACTION_PAUSE -> actionPause()
                ACTION_NEXT -> actionNext()
                ACTION_REMUSE -> actionRemuse()
                ACTION_CANCEL -> actionCancel()
            }
        }
    }

    private fun actionPrevious() {
        PositionSong -= 1
        if (PositionSong < 0) {
            PositionSong = mListSong.size -1
        }
        startMusic(PositionSong)
    }
    private fun actionNext() {
        PositionSong = (PositionSong + 1) % mListSong.size
        startMusic(PositionSong)
    }

    private fun actionPause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            sendNotification()
        }
    }
    private fun actionRemuse() {
        if (mediaPlayer != null && !isPlaying){
            mediaPlayer?.start()
            isPlaying = true
            sendNotification()
        }
    }
    private fun actionCancel() {
        managerCompat?.cancel(Contans.CHANEL_COMPAT)
        stopSelf()
    }

    private fun getPendingItent(context: Context , action: String) : PendingIntent {
        Intent(context, MyReceiver::class.java).also { mIntent ->
            mIntent.putExtra(Contans.ACTION_MUSIC, action)
            mIntent.action = action
            return PendingIntent.getBroadcast(
                context.applicationContext,
                Random.nextInt(),
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null){
            mediaPlayer?.release()
            mediaPlayer= null
        }
    }

}

