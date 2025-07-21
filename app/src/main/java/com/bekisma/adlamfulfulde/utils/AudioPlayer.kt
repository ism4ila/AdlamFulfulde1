package com.bekisma.adlamfulfulde.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

object AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context, @RawRes rawResId: Int) {
        stopSound()
        mediaPlayer = MediaPlayer.create(context, rawResId)?.apply {
            setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
            }
            start()
        }
    }

    fun stopSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}