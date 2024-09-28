package ru.tensor.sbis.mediaplayer.datasource

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import ru.tensor.sbis.mediaplayer.MediaInfo
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.io.File

@UnstableApi
/**
 * Фабрика для создания медиафайлов с типом мультимедиа [C.TYPE_HLS]
 *
 * @author sa.nikitin
 */
class HlsMediaSourceFactory(
    private val context: Context,
    private val loginInterface: LoginInterface,
    private val apiService: ApiService,
    private val cacheDir: File?,
    private val addAccessToken: Boolean = true
) : MediaSourceFactory {

    private var dataSourceFactory: SbisTubeDataSourceFactory? = null

    override val supportedTypes: IntArray = intArrayOf(C.CONTENT_TYPE_HLS)

    override fun createMediaSource(mediaInfo: MediaInfo): MediaSource =
        SbisTubeDataSourceFactory(context, loginInterface, apiService, mediaInfo.uri, cacheDir, addAccessToken).run {
            dataSourceFactory = this
            HlsMediaSource.Factory(this)
                .setAllowChunklessPreparation(true)
                .createMediaSource(MediaItem.fromUri(mediaInfo.uri))
        }

    override fun release() {
        dataSourceFactory?.release()
    }
}