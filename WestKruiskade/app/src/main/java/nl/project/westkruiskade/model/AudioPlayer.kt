package nl.project.westkruiskade.model

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.transition.TransitionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import nl.project.westkruiskade.util.Coroutines
import nl.project.westkruiskade.util.getPopupWindow
import nl.project.westkruiskade.util.getInflater
import nl.project_.west_kruiskade.R
import nl.project_.west_kruiskade.databinding.MediaPlayerBinding
import java.io.IOException
import javax.inject.Inject

@FragmentScoped
class AudioPlayer @Inject constructor(
    @ApplicationContext context: Context
) : SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioUrl: String
    private lateinit var audioPlayerWindow: PopupWindow

    private val view: View = context.getInflater(R.layout.media_player)
    private val mediaPlayerBinding = MediaPlayerBinding.bind(view)

    init {
        mediaPlayerBinding.volumeBar.setOnSeekBarChangeListener(this)
        mediaPlayerBinding.positionBar.setOnSeekBarChangeListener(this)
        mediaPlayerBinding.playBtn.setOnClickListener(this)
        initMediaPlayer()
    }
    fun intAudioUrl(_audioUrl: AudioUrl){
        audioUrl = _audioUrl.getValue()
    }

    fun isPlayerShowing(): Boolean {
        return audioPlayerWindow.isShowing
    }

    fun showPlayerAtLocation(
        parentView: ViewGroup,
        position: Int = Gravity.TOP,
        xAxis: Int = 0,
        yAxis: Int = 330
    ) {
        setupAudioPlayer()
        TransitionManager.beginDelayedTransition(parentView)
        audioPlayerWindow.showAtLocation(
            parentView, // Location to display popup window
            position, xAxis,
            yAxis
        )
    }

    fun closeAudioPlayer() {
        if (mediaPlayer.currentPosition > 0) {
            mediaPlayer.stop()
        }
        audioPlayerWindow.dismiss()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeLabels(currentPosition: Int) {
        mediaPlayerBinding.positionBar.progress = currentPosition
        // Update time Labels
        val elapsedTime = createTimeLabel(currentPosition)
        val remainingTime = createTimeLabel(mediaPlayer.duration - currentPosition)

        Coroutines.main {
            // Update positionBar
            mediaPlayerBinding.elapsedTimeLabel.text = elapsedTime
            mediaPlayerBinding.remainingTimeLabel.text = "-$remainingTime"
        }
    }

    private fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec
        return timeLabel
    }

    private fun initMediaPlayer() {
        audioPlayerWindow = getPopupWindow(view)
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
    }

    private fun setupAudioPlayer() {
        //set play or pause background image
        if (mediaPlayerBinding.playBtn.isChecked) {
            mediaPlayerBinding.playBtn.isChecked = false
        }

        try {
            if (this::audioUrl.isInitialized) {
                mediaPlayer.apply {
                    reset()
                    setDataSource(audioUrl)
                    prepare()
                    setVolume(0.5f, 0.5f)
                    updateTimeLabels(currentPosition)
                }
            }

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            mediaPlayerBinding.playBtn.id -> {
                if (mediaPlayer.isPlaying) mediaPlayer.pause() else mediaPlayer.start()
                // update time labels remaining time and played time
                Coroutines.io {
                    while (mediaPlayer.isPlaying) {
                        updateTimeLabels(mediaPlayer.currentPosition)

                    }
                }
            }
            mediaPlayerBinding.muteBtn.id -> {
                mediaPlayer.setVolume(0F, 0F)
            }
        }
    }

    override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {

        Coroutines.io {
            when (seek?.id) {
                mediaPlayerBinding.positionBar.id -> {
                    mediaPlayerBinding.positionBar.max = mediaPlayer.duration
                    if (fromUser) {
                        mediaPlayer.seekTo(progress)
                    }
                }

                mediaPlayerBinding.volumeBar.id -> {
                    if (fromUser) {
                        val volumeNum = progress / 100.0f
                        mediaPlayer.setVolume(volumeNum, volumeNum)
                    }
                }
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}
    override fun onStopTrackingTouch(p0: SeekBar?) {}

}



