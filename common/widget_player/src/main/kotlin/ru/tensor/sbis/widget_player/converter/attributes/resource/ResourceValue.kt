package ru.tensor.sbis.widget_player.converter.attributes.resource

import java.util.UUID

/**
 * Содержимое ресурса.
 *
 * @author am.boldinov
 */
sealed interface ResourceValue<VALUE> {

    val source: VALUE

    /**
     * Внутренний ресурс.
     *
     * @property source ссылка на внутренний ресурс
     */
    @JvmInline
    value class Internal(override val source: String) : ResourceValue<String>

    /**
     * Внешний ресурс.
     *
     * @property source ссылка на внешний ресурс
     */
    @JvmInline
    value class External(override val source: String) : ResourceValue<String>

    /**
     * Ресурс на сервисе file-transfer.
     *
     * @property source ссылка на ресурс в сервисе file-transfer
     */
    @JvmInline
    value class FileTransfer(override val source: String) : ResourceValue<String>

    /**
     * Ресурс на СбисДиске.
     *
     * @property source uuid файла на СбисДиске
     */
    @JvmInline
    value class SbisDisk(override val source: UUID) : ResourceValue<UUID>

    /**
     * Ресурс в виде blob.
     *
     * @property source содержимое blob файла
     */
    @JvmInline
    value class Blob(override val source: String) : ResourceValue<String>

    /**
     * Пустой (отсутствующий) ресурс.
     */
    object Empty : ResourceValue<String> {
        override val source: String = ""
    }
}