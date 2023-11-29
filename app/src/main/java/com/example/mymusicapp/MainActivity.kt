package com.example.mymusicapp

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.mymusicapp.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mediaPlayer: MediaPlayer
    lateinit var timer: Timer
    var isPlaying = true
    var isUserChanging = false
    var isMute = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareMusic()
        binding.btnPlayPause.setOnClickListener { configureMusic() }
        binding.btnGoBefore.setOnClickListener { goBeforeMusic() }
        binding.btnGoAfter.setOnClickListener { goAfterMusic() }
        binding.btnVolumeOnOff.setOnClickListener { configureVolume() }

        binding.sliderMain.addOnChangeListener { slider, value, fromUser ->
            binding.txtLeft.text = convertMillisToString(value.toLong())
            isUserChanging = fromUser
        }

        binding.sliderMain.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {

            @SuppressLint("RestrictedApi")
            override fun onStartTrackingTouch(slider: Slider) {

            }

            @SuppressLint("RestrictedApi")
            override fun onStopTrackingTouch(slider: Slider) {
                mediaPlayer.seekTo(slider.value.toInt())
            }

        })

    }

    @SuppressLint("InlinedApi")
    private fun configureVolume() {

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        if (isMute) {
            audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnVolumeOnOff.setImageResource(R.drawable.ic_volume_on)
            isMute = false

        } else {
            audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnVolumeOnOff.setImageResource(R.drawable.ic_volume_off)
            isMute = true
        }

    }

    private fun goAfterMusic() {

        val now = mediaPlayer.currentPosition
        val newValue = now + 15000
        mediaPlayer.seekTo(newValue)

    }

    private fun goBeforeMusic() {

        val now = mediaPlayer.currentPosition
        val newValue = now - 15000
        mediaPlayer.seekTo(newValue)

    }

    private fun configureMusic() {

        if (isPlaying) {

            mediaPlayer.pause()
            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            isPlaying = false

        } else {

            mediaPlayer.start()
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            isPlaying = true

        }

    }

    private fun prepareMusic() {

        mediaPlayer = MediaPlayer.create(this, R.raw.narefigh_khoob)

        mediaPlayer.start()
        isPlaying = true

        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)

        binding.sliderMain.valueTo = mediaPlayer.duration.toFloat()

        binding.txtRight.text = convertMillisToString(mediaPlayer.duration.toLong())

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!isUserChanging) {
                        binding.sliderMain.value = mediaPlayer.currentPosition.toFloat()
                    }
                }
            }
        }, 1000, 1000)

    }

    private fun convertMillisToString(duration: Long): String {

        val second = duration / 1000 % 60
        val minute = duration / (1000 * 60) % 60

        return java.lang.String.format(Locale.US, "%02d:%02d", minute, second)
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.cancel()
        mediaPlayer.release()

    }
}
