package ru.tensor.sbis.toolbox_decl.linkopener

import android.content.Intent
import android.net.Uri
import androidx.annotation.MainThread
import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс описывающий навигацию при открытии ссылок на контент/документы.
 * Предназначен для открытия документов по ссылкам:
 * - по ссылкам вида **.sbis.ru**
 * - ссылок интернет ресурсов по протоколу (http(s))
 *
 * @author as.chadov
 */
interface OpenLinkController {
    /**
     * Обработать [intent] с целью запуска целевого документа, по принципу вызвал-забыл.
     * Данная реализация предпочтительна для обработки ссылок стартовавших МП.
     *
     * @param intent интент с данными цифрового ресурса.
     * @param isOuter был ли интент получен извне МП, т.е. выступил инициатором запуска МП.
     * По умолчанию false.
     * @return false если интент НЕ будет обработан надлежащим образом.
     * Например, если [Intent] не содержит адрес ссылки.
     */
    @MainThread
    fun processAndForget(intent: Intent, isOuter: Boolean = false): Boolean

    /**
     * Обработать [uri] с целью запуска целевого документа, по принципу вызвал-забыл.
     * Данная реализация предпочтительна для обработки ссылок запускаемых из МП.
     *
     * @param uri [Uri] с данными цифрового ресурса.
     * @return false если интент НЕ будет обработан надлежащим образом.
     * Например, если [Intent] не содержит адрес ссылки.
     */
    @MainThread
    fun processAndForget(uri: String): Boolean

    /**
     * Запустить целевой документ по ссылке [link] без дополнительной обработки.
     * Данная реализация предпочтительна для обработки ссылок запускаемых из МП.
     *
     * @param link превью ссылки на документ.
     */
    @MainThread
    fun processAndForget(link: LinkPreview)

    /**
     * Запустить целевой документ по ссылке [data] без дополнительной обработки.
     * Данная реализация предпочтительна для обработки ссылок запускаемых из МП.
     *
     * @param data модель контроллера с данными по ссылке [LinkPreview].
     */
    @Throws(IllegalArgumentException::class)
    @MainThread
    fun processAndForget(data: LinkPreviewData)

    /**
     * Обработать интент с целью запуска целевого документа.
     *
     * @param intent ожидаемый интент соответствующий интент-фильтру [deepLinkHostName].
     * @param ignorePredictable true если игнорировать данные спрогнозированные локально
     * (информация о ссылке не была синхронизирована с облаком).
     * @return false если интент не был обработан надлежащим образом.
     */
    fun process(
        intent: Intent,
        ignorePredictable: Boolean = false
    ): Observable<Boolean>

    /**
     * Обработать [Uri] интента с целью запуска целевого документа.
     *
     * @param uri ожидаемый [Uri] интента.
     * @param ignorePredictable true если игнорировать данные спрогнозированные локально
     * (информация о ссылке не была синхронизирована с облаком).
     * @return false если [Uri] не был обработан надлежащим образом.
     */
    fun process(
        uri: String,
        ignorePredictable: Boolean = false
    ): Observable<Boolean>

    /**
     * Обработать интент клиентом.
     *
     * @param intent ожидаемый интент соответствующий интент-фильтру [deepLinkHostName].
     * @param ignorePredictable true если игнорировать данные спрогнозированные локально
     * (информация о ссылке не была синхронизирована с облаком).
     * @return observable-объект с данными о ссылке полученной в интент.
     */
    fun processYourself(
        intent: Intent,
        ignorePredictable: Boolean = false
    ): Observable<LinkPreview>

    /**
     * Обработать [Uri] интента клиентом.
     *
     * @param uri ожидаемый [Uri] интента.
     * @param ignorePredictable true если игнорировать данные спрогнозированные локально
     * (информация о ссылке не была синхронизирована с облаком).
     * @return observable-объект с данными о ссылке полученной из [Uri].
     */
    fun processYourself(
        uri: String,
        ignorePredictable: Boolean = false
    ): Observable<LinkPreview>

    /**
     * Поставщик реализации контрола навигации при открытии ссылок на контент/документ [OpenLinkController].
     */
    interface Provider : Feature {
        val openLinkController: OpenLinkController
    }

    companion object {
        /**
         * Интент параметр для маркировки принудительной навигации к WebView.
         * Сценарий: пользователь ожидает открытия ссылки в браузере, но МП браузера отсутствует на устройстве.
         */
        const val FORCED_NAVIGATION_TO_WEBVIEW = "FORCED_NAVIGATION_TO_WEBVIEW"
    }
}