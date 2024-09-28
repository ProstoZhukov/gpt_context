package ru.tensor.sbis.version_checker_decl.data

/**
 * Статус (тип) обновления
 * @param id идентификатор статуса
 *
 * @author as.chadov
 */
sealed class UpdateStatus(val id: Int) {

    /** Возможно рекомендательное обновление */
    object Recommended : UpdateStatus(1)

    /** Требуется принудительное обновление */
    object Mandatory : UpdateStatus(2)

    /** Обновление не требуется или еще не проверено */
    object Empty : UpdateStatus(0)
}
