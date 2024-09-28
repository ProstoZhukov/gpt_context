package ru.tensor.sbis.yandex_app_metrica

import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import io.appmetrica.analytics.AppMetrica as YandexAppMetrica

/**
 * Отправить событие метрики
 * @param contentType - тип контекста
 * @param event - событие
 * Настроено на одно вложение
 */
fun sendAnalyticsEvent(contentType: AppMetricaContentType, event: AppMetricaEvent) =
    sendAnalyticsEvent(contentType, mapOf(event.key to Unit))

/**
 * Отправить событие метрики
 * @param contentType - тип контекста
 * @param event - событие
 * Настроено на одно вложение
 */
fun sendAnalyticsEvent(contentType: AppMetricaContentType, events: Map<String, Any>) {
    var disposable: Disposable? = null
    disposable = runInBackground(
        {
            try {
                YandexAppMetrica.reportEvent(contentType.key, events)
            } catch (e: Exception) {
                return@runInBackground
            }
        },
        {
            disposable?.dispose()
            disposable = null
        }
    )
}

/**
 * Отправить событие метрики
 * @param contentType - тип контекста
 * @param jsonObject параметры события в виде json согласно документации
 * https://appmetrica.yandex.ru/docs/data-collection/events-android.html
 */
suspend fun sendAnalyticsEvent(
    contentType: AppMetricaContentType,
    jsonObject: JSONObject
) {
    withContext(Dispatchers.IO) {
        YandexAppMetrica.reportEvent(contentType.key, jsonObject.toString())
    }
}


/**
 * Отправить событие некритической ошибки
 * @param throwable - исключение
 */
fun sendAnalyticsErrorEvent(throwable: Throwable) {
    var disposable: Disposable? = null
    disposable = runInBackground(
        { YandexAppMetrica.reportError(throwable.message ?: "", throwable) },
        {
            disposable?.dispose()
            disposable = null
        }
    )
}

private fun runInBackground(action: () -> Unit, onComplete: () -> Unit): Disposable =
    Completable.fromCallable { action() }
        .subscribeOn(Schedulers.io())
        .doOnComplete { onComplete() }
        .subscribe()


/**
 * Маркерный интерфейс для определение контента события
 */
interface AppMetricaContentType {
    val key: String
}


/**
 * Маркерный интерфейс для определение события
 */
interface AppMetricaEvent {
    val key: String
}
