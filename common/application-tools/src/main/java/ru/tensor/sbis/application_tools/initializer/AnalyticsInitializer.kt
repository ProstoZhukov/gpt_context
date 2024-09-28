package ru.tensor.sbis.application_tools.initializer

import android.annotation.SuppressLint
import android.content.Context

/**
 * @author du.bykov
 *
 * Инициализатор отправки аналитики Google Analytics и Firebase Analytics.
 * Отправка событий аналитики разрешена только для релизной сборки и боевого стенда.
 */
class AnalyticsInitializer(val application: Context) : () -> Unit {

    /**
     * Инициализиорлвать отправку логов. Метод необходимо вызвать единожды, при страте приложения.
     * Пермишены для GoogleAnalytics и FirebaseAnalytics подтянутся из библиотеки при мерже манифестов.
     */
    @SuppressLint("MissingPermission")
    override fun invoke() = Unit
}