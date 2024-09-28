package ru.tensor.sbis.mediaplayer.datasource

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.source.MediaSource
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.mediaplayer.MediaInfo
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import java.io.File

@UnstableApi
/**
 * Делегат для определения целевой фабрики создания медиафайлов
 *
 * @author sa.nikitin
 */
class DelegateMediaSourceFactory(
    context: Context,
    loginInterface: LoginInterface,
    apiService: ApiService,
    cacheDir: File?
) : MediaSourceFactory {

    private val hlsMediaSourceFactory = HlsMediaSourceFactory(context, loginInterface, apiService, cacheDir)
    private val progressiveMediaSourceFactory =
        ProgressiveMediaSourceFactory(context, loginInterface, apiService, cacheDir)

    override val supportedTypes: IntArray = intArrayOf(
        *hlsMediaSourceFactory.supportedTypes,
        *progressiveMediaSourceFactory.supportedTypes
    )

    override fun createMediaSource(mediaInfo: MediaInfo): MediaSource {
        val contentType = Util.inferContentType(mediaInfo.uri)
            .takeIf { it != C.CONTENT_TYPE_OTHER || mediaInfo.extension.isBlank() }
            ?: Util.inferContentTypeForExtension(mediaInfo.extension)
        return if (contentType == C.CONTENT_TYPE_HLS) hlsMediaSourceFactory.createMediaSource(mediaInfo)
        else progressiveMediaSourceFactory.createMediaSource(mediaInfo)
    }

    override fun release() {
        hlsMediaSourceFactory.release()
        progressiveMediaSourceFactory.release()
    }
}