package ru.tensor.sbis.toolbox_decl.linkopener.service

import androidx.annotation.WorkerThread
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview

/**
 * Репозиторий, работающий с микросервисом декорирования ссылок.
 *
 * @author us.bessonov
 */
interface LinkDecoratorServiceRepository {

    /** @SelfDocumented */
    interface DataRefreshedCallback {

        /** @SelfDocumented */
        fun onEvent(data: LinkPreview)
    }

    /** @SelfDocumented */
    fun subscribe(callback: DataRefreshedCallback): Subscription

    /**
     * Получить декорированную ссылку, без вызова обработчиков определения подтипа.
     * Не делает синхронных вызовов в облако.
     * Полная информация о ссылке придёт в DataRefreshCallback`е после успешного обновления данных синхронизатором.
     */
    fun getDecoratedLinkWithoutDetection(url: String): LinkPreview?

    /**
     * Получить декорированную ссылку, с вызовом прикладных обработчиков определения подтипа.
     * Возможен синхронный вызов методов облака.
     * Полная информация о ссылке придёт в DataRefreshCallback`е после успешного обновления данных синхронизатором.
     */
    @WorkerThread
    fun getDecoratedLinkWithDetection(url: String): LinkPreview?

    /**
     * Получить несколько декорированных ссылок, без вызова обработчиков определения подтипа.
     * Не делает синхронных вызовов в облако.
     * Полная информация о ссылке придёт в DataRefreshCallback`е после успешного обновления данных синхронизатором.
     */
    fun getDecoratedLinksWithoutDetection(urls: HashSet<String>): List<LinkPreview>

    /**
     *  Найти ссылки в тексте, отметить их html тегами и преобразовать результат в Json.
     */
    fun toJson(text: String): String
}

