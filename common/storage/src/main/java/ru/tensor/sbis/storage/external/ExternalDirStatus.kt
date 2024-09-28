package ru.tensor.sbis.storage.external

/**
 * Класс, отражающий состояние внешней директории
 *
 * @property exists
 * @property readable
 * @property writable
 *
 * @author sa.nikitin
 */
class ExternalDirStatus(
    @get:JvmName("isExists") val exists: Boolean,
    @get:JvmName("isReadable") val readable: Boolean,
    @get:JvmName("isWritable") val writable: Boolean
)