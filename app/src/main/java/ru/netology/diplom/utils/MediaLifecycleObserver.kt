package ru.netology.diplom.utils

import android.media.MediaPlayer
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MediaLifecycleObserver : LifecycleEventObserver {
    var mediaPlayer: MediaPlayer? = MediaPlayer()
    private var currentPos = 0
    private var scope: CoroutineScope? = null
    fun onPlay(seekBar: SeekBar) {
        mediaPlayer?.setOnPreparedListener {
            it.apply {
                start()
                scope?.cancel()
                CoroutineScope(Dispatchers.Default).apply {
                    scope = this
                }.launch {
                    initSeekBar(seekBar)
                }
                setOnCompletionListener {
                    stop()
                    seekBar.max = 0
                }
            }
        }
        mediaPlayer?.prepareAsync()
    }

    fun onPause() {
        mediaPlayer?.pause()
    }

    fun isPaused(): Boolean = (!mediaPlayer?.isPlaying!! && mediaPlayer?.currentPosition!! > 0)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> mediaPlayer?.pause()
            Lifecycle.Event.ON_STOP -> {
                mediaPlayer?.release()
                mediaPlayer = null
            }
            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }

    suspend fun getCurrentPosition(duration: Int, pos: Int): Flow<Int> = flow {
        while (pos < duration) {
            delay(1_000L)
            emit(mediaPlayer!!.currentPosition)
        }
    }.flowOn(Dispatchers.Default)

    suspend fun initSeekBar(seekBar: SeekBar) {
        seekBar.max = mediaPlayer!!.duration
        getCurrentPosition(seekBar.max, currentPos).collect {
            seekBar.progress = it
            currentPos = it
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                        currentPos = progress
                    }

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

        }

    }

}