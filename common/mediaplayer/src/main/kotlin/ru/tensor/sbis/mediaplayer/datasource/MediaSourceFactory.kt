package ru.tensor.sbis.mediaplayer.datasource

import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import ru.tensor.sbis.mediaplayer.MediaInfo

/**
 * Интерфейс фабрики предоставления медиафайлов для воспроизведения в [ExoPlayer]
 *
 * @author sa.nikitin
 */
interface MediaSourceFactory {

    /** Поддерживаемые фабрикой типы мультимедиа [C.ContentType] */
    val supportedTypes: IntArray

    /** @SelfDocumented */
    fun createMediaSource(mediaInfo: MediaInfo): MediaSource

    /** @SelfDocumented */
    fun release()
}