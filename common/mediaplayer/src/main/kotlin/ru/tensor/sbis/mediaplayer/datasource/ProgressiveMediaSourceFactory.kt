package ru.tensor.sbis.mediaplayer.datasource

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import ru.tensor.sbis.mediaplayer.MediaInfo
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.io.File

@UnstableApi
/**
 * Фабрика для создания медиафайлов с типом мультимедиа [C.TYPE_OTHER]
 *
 * @author sa.nikitin
 */
class ProgressiveMediaSourceFactory(
    private val context: Context,
    private val loginInterface: LoginInterface,
    private val apiService: ApiService,
    private val cacheDir: File?
) : MediaSourceFactory {

    private var dataSourceFactory: SbisTubeDataSourceFactory? = null

    override val supportedTypes: IntArray = intArrayOf(C.CONTENT_TYPE_OTHER)

    override fun createMediaSource(mediaInfo: MediaInfo): MediaSource =
        SbisTubeDataSourceFactory(context, loginInterface, apiService, mediaInfo.uri, cacheDir).run {
            dataSourceFactory = this
            ProgressiveMediaSource.Factory(this).createMediaSource(MediaItem.fromUri(mediaInfo.uri))
        }

    override fun release() {
        dataSourceFactory?.release()
    }
}