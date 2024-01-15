package com.example.lecteur

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager.InvalidDisplayException
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.example.lecteur.R.id.*
import java.net.IDN

class LecteurplayerActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvArtist: TextView
    private lateinit var seekBarTime: SeekBar
    private lateinit var seekBarVolume: SeekBar
    private lateinit var btnPlay: Button

    var lecteurPlayer: MediaPlayer? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecteurplayer)

        val song = if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("song", Song::class.java)
        } else {
            intent.getSerializableExtra("song") as Song?
        }
        tvTime = findViewById(R.id.tvTime)
        tvDuration = findViewById(R.id.tvDuration)
        tvTitle = findViewById(R.id.tvTitle)
        tvArtist = findViewById(R.id.tvArtist)
        seekBarTime = findViewById(R.id.seekBarTime)
        seekBarVolume = findViewById(R.id.seekBarTime)
        btnPlay = findViewById(R.id.btnPlay)

        tvTitle.text = song!!.title
        tvArtist.text = song.artist
        lecteurPlayer = MediaPlayer()
        try {
            lecteurPlayer!!.setDataSource(song?.path)
            lecteurPlayer!!.prepare()
        }catch (e: InvalidDisplayException) {
            e.printStackTrace()
        }
        lecteurPlayer!!.isLooping = true
        lecteurPlayer!!.seekTo(0)
        lecteurPlayer!!.setVolume(0.5f, 0.5f)
        val duration = millisecondsToString(lecteurPlayer!!.duration)
        tvDuration.text = duration
        seekBarVolume.progress = 50
        seekBarVolume.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                lecteurPlayer!!.setVolume(volume, volume)

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?){

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?){

            }
        })

        seekBarTime.max = lecteurPlayer!!.duration
        seekBarTime.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    lecteurPlayer!!.seekTo(progress)
                    seekBar.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        btnPlay.setOnClickListener {
            if(lecteurPlayer!!.isPlaying) {
                lecteurPlayer!!.pause()
                btnPlay.setBackgroundResource(R.drawable.fichh)
            } else
            {
                lecteurPlayer!!.start()
                btnPlay.setBackgroundResource(R.drawable.pause)
            }
        }

        Thread {
            while (lecteurPlayer != null) {
                if(lecteurPlayer!!.isPlaying) {
                    try {
                        val current = lecteurPlayer!!.currentPosition.toDouble()
                        val elapsedTime = millisecondsToString(current.toInt())
                        runOnUiThread {
                            tvTime.text = elapsedTime
                            seekBarTime.progress = current.toInt()
                        }
                        Thread.sleep(1000)
                    }catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        lecteurPlayer!!.stop()
    }

    private fun millisecondsToString(time: Int): String {
        var elapsedTime: String? =""
        val minutes = time / 1000 / 60
        val seconds = time / 1000 % 60
        elapsedTime = "$minutes:"
        if (seconds < 10) {
            elapsedTime += "0"
        }
        elapsedTime += seconds
        return elapsedTime
    }
}