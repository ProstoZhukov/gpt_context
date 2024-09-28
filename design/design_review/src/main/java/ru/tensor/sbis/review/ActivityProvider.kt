package ru.tensor.sbis.review

import android.app.Activity

/**
 * Интерфейс предоставляющий активити для сервиса оценок
 *
 * @author ma.kolpakov
 */
internal interface ActivityProvider {
    fun activity(): Activity?
}
