package ru.tensor.sbis.communication_decl.communicator.media.data

import android.net.Uri
import java.util.*

/**
 * На момент начала проигрывания [Uri] для проигрывания может быть не известен.
 * Для таких случаев извне должен быть предоставлен механизм для получения [Uri] по иденитификатору.
 *
 * @author da.zhukov
 */
interface UriResolver {

    fun resolve(
        attachId: Long,
        callback: (attachId: Long, resolvedUri: Uri?, error: Throwable?) -> Unit,
    )
}

/**
 * Источник проигрывания.
 *
 * @author da.zhukov
 */
sealed class MediaSource {

    /**
     * Уникальный идентификатор источника проигрывания - необходим для восстановления состояния проигрывания на UI.
     * Если не задан, буден сгенерирован автоматически.
     */
    abstract val uuid: UUID?

    /**
     * Данные источника.
     */
    abstract val data: SourceData

    /**
     * Источник проигрывания аудио.
     */
    data class AudioSource(
        override val data: SourceData,
        override val uuid: UUID = UUID.randomUUID(),
    ) : MediaSource()

    /**
     * Источник проигрывания видео.
     */
    data class VideoSource(
        override val data: SourceData,
        override val uuid: UUID = UUID.randomUUID(),
    ) : MediaSource()
}

/**
 * Данные источника проигрывания.
 */
sealed interface SourceData {

    /**
     * Данные uri файла.
     */
    data class UriData(val uri: Uri): SourceData

    /**
     * Данные файла диска.
     * @param attachId идентификатор вложения, будет использован [UriResolver] для получения uri файла.
     */
    data class DiskData(val attachId: Long): SourceData
}