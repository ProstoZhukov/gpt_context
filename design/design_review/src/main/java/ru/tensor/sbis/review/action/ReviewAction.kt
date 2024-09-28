package ru.tensor.sbis.review.action

import android.app.Activity
import androidx.lifecycle.LiveData

/**
 * Событие вызываемое при срабатывании триггера
 *
 * @author ma.kolpakov
 */
internal interface ReviewAction {

    /**
     * Метод который будет вызван когда необходимо показать окно "оцените приложение"
     */
    fun startReview(activity: Activity, event: Enum<*>)

    /**
     * Подписка на состояния сервиса оценок
     */
    val reviewState: LiveData<ReviewState>

}
