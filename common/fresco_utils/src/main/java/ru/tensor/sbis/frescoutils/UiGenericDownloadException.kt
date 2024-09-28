package ru.tensor.sbis.frescoutils

/**
 * Класс исключения при скачивании изображений через Fresco
 *
 * @property errorCode      Код ошибки от сервера
 * @property downloadType   Тип скачивания
 *                          Например, для скачивания превью вложений тип будет "Docview"
 *
 * @author sa.nikitin
 */
class UiGenericDownloadException(
    val errorCode: Int,
    val downloadType: String, cause: Throwable? = null
) : Exception(cause) {

    override fun toString(): String = "UiGenericDownloadException(errorCode=$errorCode, domain='$downloadType')"
}