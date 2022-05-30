package com.truongdc21.mediaplaymusic.Service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.truongdc21.mediaplaymusic.Model.Song
import com.truongdc21.mediaplaymusic.R
import com.truongdc21.mediaplaymusic.Receiver.MyReceiver
import com.truongdc21.mediaplaymusic.Utils.Constance
import kotlinx.coroutines.*
import java.util.*
import kotlin.random.Random

class MusicService : Service() {

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
            mListSong = bundle.get(Constance.OBJECT_LIST_SONG) as MutableList<Song>
            PositionSong = bundle.getInt(Constance.OBJECT_POSITION_SONG)
            startMusic(PositionSong)
            mediaPlayer?.seekTo(bundle.getInt(Constance.SEEKBAR_PROGRESS ))
            Log.d("test" , "TEST TIME PR : ${bundle.getInt(Constance.SEEKBAR_PROGRESS )} ")
        }
        intent?.action?.let { listenerActionMusic(it) }
        mediaPlayer?.setOnCompletionListener { actionNext() }
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
        sendActiontoMain(Constance.ACTION_START)
        sendMediaplayTime()
    }

    private fun sendNotification(){
        val bitmap = BitmapFactory.decodeResource(Resources.getSystem() , R.drawable.ic_music)
        val mediaSessionCompat = MediaSessionCompat(this, "tag")
        val notification = NotificationCompat.Builder(this, Constance.CHANEL_ID).apply {
             setSmallIcon(R.drawable.ic_small_music)
             setContentTitle(mListSong[PositionSong].Title)
             setContentText(mListSong[PositionSong].Singer)
             setLargeIcon(bitmap)
             addAction(R.drawable.ic_back , "Previous" , getPendingItent(this@MusicService , Constance.ACTION_PREVIOUS))
             if (isPlaying) addAction(R.drawable.ic_pause2 , "Pause", getPendingItent(this@MusicService , Constance.ACTION_PAUSE) )
             else addAction(R.drawable.ic_play2 , "Pause", getPendingItent(this@MusicService , Constance.ACTION_REMUSE) )
             addAction(R.drawable.ic_next , "Next" , getPendingItent(this@MusicService , Constance.ACTION_NEXT))
             addAction(R.drawable.ic_close , "Close" , getPendingItent(this@MusicService , Constance.ACTION_CANCEL))
             setStyle(androidx.media.app.NotificationCompat
                .MediaStyle()
                .setShowActionsInCompactView(1 ,3)
                .setMediaSession(mediaSessionCompat.sessionToken)
            )
        }.build()
        managerCompat?.notify(Constance.CHANEL_COMPAT ,notification)
    }

    private fun listenerActionMusic (action : String ) {
        Constance.apply {
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
        sendActiontoMain(Constance.ACTION_PREVIOUS)
    }
    private fun actionNext() {
        PositionSong = (PositionSong + 1) % mListSong.size
        startMusic(PositionSong)
        sendActiontoMain(Constance.ACTION_NEXT)
    }

    private fun actionPause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            sendNotification()
            sendActiontoMain(Constance.ACTION_PAUSE)
        }
    }
    private fun actionRemuse() {
        if (mediaPlayer != null && !isPlaying){
            mediaPlayer?.start()
            isPlaying = true
            sendNotification()
            sendActiontoMain(Constance.ACTION_REMUSE)
        }
    }
    private fun actionCancel() {
        managerCompat?.cancel(Constance.CHANEL_COMPAT)
        stopSelf()
        sendActiontoMain(Constance.ACTION_CANCEL)
    }

    private fun getPendingItent(context: Context , action: String) : PendingIntent {
        Intent(context, MyReceiver::class.java).also { mIntent ->
            mIntent.putExtra(Constance.ACTION_MUSIC, action)
            mIntent.action = action
            return PendingIntent.getBroadcast(
                context.applicationContext,
                Random.nextInt(),
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    }

    private fun sendActiontoMain(action: String){
        val intent = Intent(Constance.ACTION_SEND_TO_MAIN)
        val bundle = Bundle().apply {
            putInt(Constance.POSITION_SONG , PositionSong)
            putBoolean(Constance.STATUS_SONG , isPlaying)
            putString(Constance.ACTION_MUSIC , action)
            mediaPlayer?.duration?.let { putInt(Constance.ACTION_SEND_TIME_END , it) }
        }
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun sendMediaplayTime(){
        val handler = Handler(Looper.getMainLooper())
            handler.postDelayed( object: Runnable {
            override fun run() {
                val intent = Intent(Constance.ACTION_SEND_TIME)
                intent.putExtra(Constance.ACTION_SEND_TIME_FIRST_LOOP , mediaPlayer?.currentPosition)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                handler.postDelayed(this, 1000)
            }
        } , 0)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null){
            mediaPlayer?.release()
            mediaPlayer= null
        }
        managerCompat?.cancel(Constance.CHANEL_COMPAT)
    }

}

