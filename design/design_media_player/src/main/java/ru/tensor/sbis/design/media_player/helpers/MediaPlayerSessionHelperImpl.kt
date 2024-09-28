package ru.tensor.sbis.design.media_player.helpers

import android.app.Activity
import android.media.AudioManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerSessionHelper
import ru.tensor.sbis.design.media_player.MediaPlayerPlugin

/**
 * Реализация [MediaPlayerSessionHelper] для настройки сессии плеера,
 * в которую может начаться проигрывание.
 *
 * В частности настраиваются активируются датчики для погашения экрана и переключения звука на верхний динамик.
 *
 * @author vv.chekurda
 */
internal class MediaPlayerSessionHelperImpl :
    MediaPlayerSessionHelper,
    DefaultLifecycleObserver {

    private lateinit var player: MediaPlayer
    private val proximityHelper = MediaPlayerPlugin.feature.getProximityHelper()
    private var lifecycleOwner: LifecycleOwner? = null

    override fun init(fragment: Fragment, customPlayer: MediaPlayer?) {
        init(fragment, fragment.activity, customPlayer)
    }

    override fun init(
        lifecycleOwner: LifecycleOwner,
        activity: Activity?,
        customPlayer: MediaPlayer?,
    ) {
        player = customPlayer ?: MediaPlayerPlugin.feature.getMediaPlayer()
        this.lifecycleOwner = lifecycleOwner

        activity?.also(::setAudioRouteListener)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        proximityHelper.start()
    }

    override fun onStop(owner: LifecycleOwner) {
        proximityHelper.stop()
        player.stop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        proximityHelper.stop()
        player.audioRouteChangeListener = null
        lifecycleOwner?.lifecycle?.removeObserver(this)
        lifecycleOwner = null
    }

    private fun setAudioRouteListener(activity: Activity) {
        player.audioRouteChangeListener = object : MediaPlayer.AudioRouteChangeListener {
            override fun onAudioRouteChanged(frontSpeaker: Boolean) {
                activity.volumeControlStream = if (frontSpeaker) {
                    AudioManager.STREAM_VOICE_CALL
                } else {
                    AudioManager.USE_DEFAULT_STREAM_TYPE
                }
            }
        }
    }
}