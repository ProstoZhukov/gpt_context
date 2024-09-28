package ru.tensor.sbis.link_opener.data

import android.content.Intent

/**
 * Классификатор входной ссылки.
 *
 * @param isSbis true если это СБИС-ссылка.
 * @param isForeign true если это ссылка на не СБИС ресурс.
 * @param isValid true если это ссылка.
 * @param isSabylink true если это ссылка создана из другого СБИС-МП для редиректа в это МП.
 */
internal enum class IncomingLinkType(
    val isSbis: Boolean = false,
    val isForeign: Boolean = false,
    val isValid: Boolean = false,
    var isSabylink: Boolean = false
) {
    /** СБИС-ссылка. */
    SBIS(isSbis = true, isValid = true),
    
    /** Перенаправленная СБИС-ссылка из другого приложения. */
    SABYLINK(isSbis = true, isSabylink = true, isValid = true),

    /** Сторонняя ссылка. */
    FOREIGN(isForeign = true, isValid = true),

    /** НЕ ссылка. */
    INVALID
}
