package ru.tensor.sbis.design.media_player.helpers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.FileDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.extractor.DefaultExtractorsFactory
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.PlaybackSpeed
import ru.tensor.sbis.communication_decl.communicator.media.data.SourceData
import ru.tensor.sbis.communication_decl.communicator.media.data.UriResolver
import ru.tensor.sbis.design.media_player.data.LoadControl
import ru.tensor.sbis.mediaplayer.datasource.DelegateMediaSourceFactory
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.verification_decl.login.LoginInterface
import timber.log.Timber

@UnstableApi
/**
 * Хелпер для инициализации и подготовки плеера.
 *
 * @author da.zhukov
 */
internal class ExoPlayerHelper(
    private val mediaPlayerStateHelper: MediaPlayerStateHelper,
    private val loginInterface: LoginInterface,
    private val apiService: ApiService,
    val appContext: Context,
) {
    private var uriResolver: UriResolver? = null
    private var onError: (error: Throwable?) -> Unit = {}

    @Suppress("DEPRECATION")
    private var initializedPlayer: ExoPlayer? = null

    /**@SelfDocumented*/
    @Suppress("DEPRECATION")
    val player: ExoPlayer
        get() = initializedPlayer ?: initPlayer().also {
            it.addListener(mediaPlayerStateHelper)
            it.setVideoFrameMetadataListener(mediaPlayerStateHelper)
            initializedPlayer = it
        }

    /**@SelfDocumented*/
    fun init(onError: (error: Throwable?) -> Unit) {
        this.onError = onError
    }

    /**@SelfDocumented*/
    fun setUriResolver(uriResolver: UriResolver?) {
        this.uriResolver = uriResolver
    }

    /**@SelfDocumented*/
    fun release() {
        initializedPlayer?.playWhenReady = false
        initializedPlayer?.release()
        initializedPlayer = null
    }

    /**
     * Подготовиться к проигрыванию медиа сообщений.
     */
    fun prepare(
        mediaInfo: MediaInfo,
        playWhenReady: Boolean,
        onPrepared: (speed: PlaybackSpeed) -> Unit
    ) {
        val sourceData = mediaInfo.mediaSource.data
        val uriResolver = this.uriResolver
        when {
            sourceData is SourceData.UriData -> {
                preparePlayer(sourceData.uri, playWhenReady, onPrepared, mediaInfo.playbackSpeed)
            }
            uriResolver != null && sourceData is SourceData.DiskData -> {
                uriResolver.resolve(sourceData.attachId) { attachId: Long, uri: Uri?, error: Throwable? ->
                    if (attachId != sourceData.attachId) return@resolve
                    if (uri != null) {
                        preparePlayer(uri, playWhenReady, onPrepared, mediaInfo.playbackSpeed)
                    } else {
                        onError(error)
                    }
                }
            }
            else -> Timber.e("Cannot resolve uri for playing.")
        }
    }

    private fun preparePlayer(
        mediaUri: Uri,
        playWhenReady: Boolean,
        onPrepared: (speed: PlaybackSpeed) -> Unit,
        speed: PlaybackSpeed
    ) {
        val playerSource = if (mediaUri.scheme == ContentResolver.SCHEME_FILE) {
            val dataSpec = DataSpec(mediaUri)
            val dataSource = FileDataSource()

            try {
                dataSource.open(dataSpec)
            } catch (ex: FileDataSource.FileDataSourceException) {
                Timber.e(ex, "ExoPlayerHelper.preparePlayer")
                onError(ex)
                return
            }

            val factory = DataSource.Factory { dataSource }
            ProgressiveMediaSource.Factory(
                factory,
                DefaultExtractorsFactory()
            ).createMediaSource(MediaItem.fromUri(mediaUri))
        } else {
            DelegateMediaSourceFactory(appContext, loginInterface, apiService, appContext.cacheDir)
                .createMediaSource(ru.tensor.sbis.mediaplayer.MediaInfo(mediaUri))
        }
        player.playWhenReady = playWhenReady
        @Suppress("DEPRECATION")
        player.prepare(playerSource, true, true)
        onPrepared(speed)
    }

    @Suppress("DEPRECATION")
    private fun initPlayer(): ExoPlayer {
        val trackSelectionFactory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(appContext, trackSelectionFactory)
        val loadControl = LoadControl()
        val renderersFactory = DefaultRenderersFactory(appContext.applicationContext)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        val player = ExoPlayer.Builder(appContext, renderersFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()

        if (isDebugExoPlayer) {
            player.addAnalyticsListener(EventLogger(null))
        }
        return player
    }
}

/**
 * Опция для включения отладки работы библиотеки ExoPlayer.
 */
private const val isDebugExoPlayer = false