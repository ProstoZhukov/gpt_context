package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.UUID

/**
 * Параметры вложения СБИС Диска для передачи в мастер создания задачи.
 * @property id Идентификатор документа.
 * @property localIds идентификаторы локальной БД.
 * @property name Имя документа с расширением.
 * @property attributes Аттрибуты документа.
 *
 * @author aa.sviridov
 */
@Parcelize
data class DiskAttachment(
    val id: String?,
    val localIds: LocalIds,
    val name: String,
    val attributes: Attributes,
) : Parcelable {

    /**
     * Локальные идентификаторы документа СБИС Диска.
     * @property id Идентификатор вложения в локальной БД.
     * @property redId Идентификатор редакции в локальной БД.
     *
     * @author aa.sviridov
     */
    @Parcelize
    data class LocalIds(
        val id: Long,
        val redId: Long,
        val uuid: UUID?,
    ) : Parcelable

    /**
     * Класс аттрибутов документа.
     * @property isFolder является ли документ папкой.
     * @property previewUri ссылка на превью документа.
     * @property foreignSignsCount количество чужих подписей.
     * @property signedByMe подписан ли документ текущим пользователем.
     * @property encrypted зашифрован ли документ.
     * @property size размер документа в байтах.
     * @property isFromBuffer принадлежит ли документ СБИС буферу.
     * @property isLink является ли документ ссылкой. Может пригодиться для дальнейшего прикрепления, ссылки нужно
     * прикреплять как ссылки.
     *
     * @author aa.sviridov
     */
    data class Attributes @JvmOverloads constructor(
        val isFolder: Boolean = false,
        val previewUri: String? = null,
        val foreignSignsCount: Int = 0,
        val signedByMe: Boolean = false,
        val encrypted: Boolean = false,
        val size: Long = 0L,
        val isFromBuffer: Boolean = false,
        val isLink: Boolean = false,
        val imageWidth: Int? = null,
        val imageHeight: Int? = null
    ) : Serializable
}
