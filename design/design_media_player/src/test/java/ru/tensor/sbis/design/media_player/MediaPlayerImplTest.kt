package ru.tensor.sbis.design.media_player

import android.view.TextureView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource
import ru.tensor.sbis.communication_decl.communicator.media.data.PlaybackSpeed
import ru.tensor.sbis.communication_decl.communicator.media.data.SourceData
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import ru.tensor.sbis.communication_decl.communicator.media.data.UriResolver
import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.design.media_player.helpers.ExoPlayerHelper
import ru.tensor.sbis.design.media_player.helpers.MediaPlayerInfo
import ru.tensor.sbis.design.media_player.helpers.MediaPlayerStateHelper
import ru.tensor.sbis.design.media_player.helpers.ProgressTimerHelper
import ru.tensor.sbis.design.media_player.helpers.UIThreadHelper
import ru.tensor.sbis.design.media_player.helpers.WakeLockHelper
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import java.util.UUID

class MediaPlayerImplTest {

    private val mediaPlayer: MediaPlayerImpl by lazy { createMediaPlayer() }
    private val mediaPlayerStateHelper = MediaPlayerStateHelper()
    private val mediaPlayerInfo = MediaPlayerInfo()
    private val mediaInfo: MediaInfo = MediaInfo(
        MediaSource.VideoSource(
            uuid = UUID.fromString("1ac6f599-8a3a-4cf6-99a8-91de4c342203"),
            data = SourceData.DiskData(72)
        )
    )

    private val wakeLockHelper = mock<WakeLockHelper>()
    private val uiThreadHelper = mock<UIThreadHelper>()
    private val progressTimerHelper = mock<ProgressTimerHelper>()
    private val simpleExoPlayer = mock<ExoPlayer> {
        val testDuration = 123L
        on { duration } doReturn testDuration
        on { audioAttributes } doReturn mock()
    }
    private val negativeCallStateProvider = mock<CallStateProvider> {
        on { isCallRunning() } doReturn false
    }
    private val positiveCallStateProvider = mock<CallStateProvider> {
        on { isCallRunning() } doReturn true
    }
    private val mockMediaPlayerStateHelper = mock<MediaPlayerStateHelper> {
        on { currentSubscription } doReturn mock()
    }
    private val mockMediaPlayerInfo: MediaPlayerInfo = mock { on { currentMediaInfo } doReturn null }
    private val exoPlayerHelper = mock<ExoPlayerHelper> {
        on { player } doReturn simpleExoPlayer
    }

    private fun createMediaPlayer(
        mediaPlayerStateHelper: MediaPlayerStateHelper? = null,
        mediaPlayerInfo: MediaPlayerInfo? = null,
        featureProvider: FeatureProvider<CallStateProvider>? = null
    ) = MediaPlayerImpl(
        mock(),
        mock(),
        mock(),
        exoPlayerHelper,
        mediaPlayerStateHelper ?: this.mockMediaPlayerStateHelper,
        mediaPlayerInfo ?: this.mediaPlayerInfo,
        wakeLockHelper,
        uiThreadHelper,
        progressTimerHelper,
        featureProvider ?: createCallStateProvider(false)
    )

    private fun createCallStateProvider(isCallRunning: Boolean) = mock<FeatureProvider<CallStateProvider>> {
        on { get() } doReturn if (isCallRunning) positiveCallStateProvider else negativeCallStateProvider
    }

    @Test
    fun `When setting the uri resolver, then set it in the helper`() {
        val uriResolver: UriResolver = mock()
        mediaPlayer.setUriResolver(uriResolver)

        verify(exoPlayerHelper).setUriResolver(uriResolver)
    }

    @Test
    fun `When preparing media source for playback, then check thread`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        verify(uiThreadHelper).ensureMainThread()
    }

    @Test
    fun `When preparing media source for playback, then initialize helpers once`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.setMediaInfo(mediaInfo)

        verify(exoPlayerHelper).init(mediaPlayer::onError)
        verify(mockMediaPlayerStateHelper).init(
            exoPlayerHelper.player,
            mediaPlayer,
            mediaPlayerInfo,
            mediaPlayer::onError
        )
        verify(progressTimerHelper).init(
            mediaPlayerInfo,
            mockMediaPlayerStateHelper,
            exoPlayerHelper.player,
            uiThreadHelper
        )
    }

    @Test
    fun `When preparing a media source for playback, then compare the media source`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.setMediaInfo(mediaInfo)

        verify(exoPlayerHelper).prepare(eq(mediaInfo), eq(true), any())
    }

    @Test
    fun `When preparing a media source for playback, then compare the media and stop playback`() {
        val mediaInfoTest = MediaInfo(
            MediaSource.AudioSource(
                uuid = UUID.fromString("1ac6f599-8a3a-4cf6-99a6-93de4c342204"),
                data = SourceData.DiskData(72)
            )
        )
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.setMediaInfo(mediaInfoTest)

        verify(progressTimerHelper).stopProgressTimer()
    }

    @Test
    fun `When preparing a media source for playback, then prepare the player`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        verify(exoPlayerHelper).prepare(mediaInfo, true, mediaPlayer::setPlaybackSpeed)
    }

    @Test
    fun `When preparing a media source for playback, then check for video call activity`() {
        val mediaPlayer = createMediaPlayer(featureProvider = createCallStateProvider(true))

        mediaPlayer.setMediaInfo(mediaInfo)

        verify(exoPlayerHelper, never()).prepare(mediaInfo, true, mediaPlayer::setPlaybackSpeed)
    }

    @Test
    fun `When receiving a data, then check that the correct one is given`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        val actual = mediaPlayer.getMediaInfo()

        assertEquals(mediaInfo, actual)
    }

    @Test
    fun `When starting playback, then check for data on the current playback source`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerInfo = mockMediaPlayerInfo)

        mediaPlayer.play()

        verify(progressTimerHelper, never()).startProgressTimer()
    }

    @Test
    fun `When starting playback, then check the availability of the player`() {
        mediaPlayer.isEnabled = false
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.play()

        verify(progressTimerHelper, never()).startProgressTimer()
    }

    @Test
    fun `When starting playback, then check for video call activity`() {
        val mediaPlayer = createMediaPlayer(featureProvider = createCallStateProvider(true))
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.play()

        verify(progressTimerHelper, never()).startProgressTimer()
    }

    @Test
    fun `When starting playback, then check thread`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.play()

        verify(uiThreadHelper, times(2)).ensureMainThread()
    }

    @Test
    fun `When playback starts, then update the state of the media source`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.play()

        verify(mockMediaPlayerStateHelper).updateMediaState(any())
    }

    @Test
    fun `When playback starts, then update the state of the media source2`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerStateHelper = mediaPlayerStateHelper)
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.play()

        assertEquals(State.PLAYING, mediaPlayerInfo.currentMediaInfo?.state)
    }

    @Test
    fun `When playback starts, then update the player state`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.play()

        verify(exoPlayerHelper.player).playWhenReady = true
    }

    @Test
    fun `When playback starts, then start the playback timer`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.play()

        verify(progressTimerHelper).startProgressTimer()
    }

    @Test
    fun `When playback starts, then unlock the screen`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.play()

        verify(wakeLockHelper).requestWakeLock(true)
    }

    @Test
    fun `When playback pauses, then check for data on the current playback source`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerInfo = mockMediaPlayerInfo)

        mediaPlayer.pause()

        verify(progressTimerHelper, never()).stopProgressTimer()
    }

    @Test
    fun `When playback pauses, then check thread`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.pause()

        verify(uiThreadHelper, times(2)).ensureMainThread()
    }

    @Test
    fun `When playback pauses, then update the state of the media source`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.pause()

        verify(mockMediaPlayerStateHelper).updateMediaState(any())
    }

    @Test
    fun `When playback pauses, then update the state of the media source2`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerStateHelper = mediaPlayerStateHelper)
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.pause()

        assertEquals(State.PAUSED, mediaPlayerInfo.currentMediaInfo?.state)
        assertEquals(false, mediaPlayerInfo.currentMediaInfo?.waitingActualProgress)
    }

    @Test
    fun `When playback pauses, then update the player state`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.pause()

        verify(exoPlayerHelper.player).playWhenReady = false
    }

    @Test
    fun `When playback pauses, then stop the playback timer`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.pause()

        verify(progressTimerHelper).stopProgressTimer()
    }

    @Test
    fun `When playback pauses, then allow screen lock`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.pause()

        verify(wakeLockHelper).requestWakeLock(false)
    }

    @Test
    fun `When playback stops, then check for data on the current playback source`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerInfo = mockMediaPlayerInfo)

        mediaPlayer.stop()

        verify(progressTimerHelper, never()).stopProgressTimer()
    }

    @Test
    fun `When playback stops, then check thread`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.stop()

        verify(uiThreadHelper, times(2)).ensureMainThread()
    }

    @Test
    fun `When playback stops, then stop the playback timer`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.stop()

        verify(progressTimerHelper).stopProgressTimer()
    }

    @Test
    fun `When playback stops, then update the state of the media source`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.stop()

        verify(mockMediaPlayerStateHelper).updateMediaState(any())
    }

    @Test
    fun `When playback stops, then update the state of the media source2`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerStateHelper = mediaPlayerStateHelper)
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.stop()

        assertEquals(State.DEFAULT, mediaPlayerInfo.currentMediaInfo?.state)
        assertEquals(0L, mediaPlayerInfo.currentMediaInfo?.position)
        assertEquals(false, mediaPlayerInfo.currentMediaInfo?.waitingActualProgress)
    }

    @Test
    fun `When playback stops, then update the player state`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.stop()

        verify(exoPlayerHelper.player).playWhenReady = false
    }

    @Test
    fun `When playback stops, then return the player to the zero position`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.stop()

        verify(exoPlayerHelper.player).seekTo(0)
    }

    @Test
    fun `When playback stops, then allow screen lock`() {
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.stop()

        verify(wakeLockHelper).requestWakeLock(false)
    }

    @Test
    fun `When changing the playback position, then update the state of the media source`() {
        mediaPlayer.setMediaInfo(mediaInfo)
        val position = 123L

        mediaPlayer.setPosition(position)

        verify(mockMediaPlayerStateHelper).updateMediaState(any())
    }

    @Test
    fun `When changing the playback position, then update the state of the media source2`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerStateHelper = mediaPlayerStateHelper)
        mediaPlayer.setMediaInfo(mediaInfo)
        val position = 123L

        mediaPlayer.setPosition(position)

        assertEquals(position, mediaPlayerInfo.currentMediaInfo?.position)
    }

    @Test
    fun `When changing the playback position, then update player position`() {
        val position = 123L

        mediaPlayer.setPosition(position)

        verify(exoPlayerHelper.player).seekTo(position)
    }

    @Test
    fun `When changing the playback progress, then check if the source has changed`() {
        val progress = 0.45f
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.seekToProgress(progress, true)

        assertEquals(progress, mediaPlayerInfo.seekToProgressPending)
        assertEquals(true, mediaPlayerInfo.currentMediaInfo?.waitingActualProgress)
    }

    @Test
    fun `When changing playback progress, then set a new position for the player`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerStateHelper = mediaPlayerStateHelper)
        val progress = 0.45f
        val duration = 123L
        val position = (progress * duration).toLong()
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.seekToProgress(progress, false)

        verify(exoPlayerHelper.player).duration
        verify(exoPlayerHelper.player).seekTo(position)
        assertEquals(position, mediaPlayerInfo.currentMediaInfo?.position)
    }

    @Test
    fun `When setting the texture, then set it to the helper`() {
        val textureView: TextureView = mock()

        mediaPlayer.setVideoTextureView(textureView)

        verify(exoPlayerHelper.player).setVideoTextureView(textureView)
    }

    @Test
    fun `When changing the playback speed, then update the playback speed of the player`() {
        val playbackSpeed = PlaybackSpeed.X2

        mediaPlayer.setPlaybackSpeed(playbackSpeed)

        verify(exoPlayerHelper.player).playbackParameters = PlaybackParameters(playbackSpeed.value)
    }

    @Test
    fun `When changing the playback speed, then update the speed of the media source`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerStateHelper = mediaPlayerStateHelper)
        val playbackSpeed = PlaybackSpeed.X2
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.setPlaybackSpeed(playbackSpeed)

        assertEquals(playbackSpeed, mediaPlayerInfo.currentMediaInfo?.playbackSpeed)
    }

    @Test
    fun `When all involved resources are released and playback is active, then stop it`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerInfo = mediaPlayerInfo.apply { isPlayingActive = true })
        mediaPlayer.setMediaInfo(mediaInfo)

        mediaPlayer.release()

        verify(progressTimerHelper).stopProgressTimer()
    }

    @Test
    fun `When all involved resources are released, then release helper resources`() {
        mediaPlayer.release()

        verify(progressTimerHelper).release()
    }

    @Test
    fun `When all involved resources are released, then release helper resources2`() {
        mediaPlayer.release()

        verify(exoPlayerHelper).release()
    }

    @Test
    fun `When all involved resources are released, then release helper resources3`() {
        mediaPlayer.release()

        verify(mockMediaPlayerStateHelper).release()
    }

    @Test
    fun `When all involved resources are released, then clear data`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerInfo = mockMediaPlayerInfo)
        mediaPlayer.release()

        verify(mockMediaPlayerInfo).clear()
    }

    @Test
    fun `When all involved resources are released, then allow screen lock`() {
        mediaPlayer.release()

        verify(wakeLockHelper).requestWakeLock(false)
    }

    @Test
    fun `When the playback status is requested, then check if the result is correct`() {
        val mediaPlayer = createMediaPlayer(mediaPlayerStateHelper = mediaPlayerStateHelper)
        mediaPlayer.setMediaInfo(mediaInfo)
        mediaPlayer.play()

        val actual = mediaPlayer.isPlayingActive()

        assertEquals(true, actual)
    }

    @Test
    fun `When setting the playback state change listener, set it to the helper`() {
        val listener: MediaPlayer.PlayingStateListener = mock()

        mediaPlayer.setPlayingListener(listener)

        verify(mockMediaPlayerStateHelper).setPlayingListener(listener)
    }

    @Test
    fun `When switching speakers, then call the listener event`() {
        val audioRouteChangeListener = mock<MediaPlayer.AudioRouteChangeListener>()
        mediaPlayer.audioRouteChangeListener = audioRouteChangeListener

        mediaPlayer.changeAudioRoute(true)

        verify(audioRouteChangeListener).onAudioRouteChanged(true)
    }

    @Test
    fun `When switching speakers, then set the player's audio attributes`() {
        val audioAttributes = exoPlayerHelper.player.audioAttributes
        val useFrontSpeaker = true
        mediaPlayer.changeAudioRoute(useFrontSpeaker)

        verify(exoPlayerHelper.player).setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_VOICE_COMMUNICATION)
                .setFlags(audioAttributes.flags)
                .setAllowedCapturePolicy(audioAttributes.allowedCapturePolicy)
                .setSpatializationBehavior(audioAttributes.spatializationBehavior)
                .setContentType(if (useFrontSpeaker) C.AUDIO_CONTENT_TYPE_SPEECH else C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            false
        )
    }
}