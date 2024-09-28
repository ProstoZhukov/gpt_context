package ru.tensor.sbis.info_decl.news.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Interface which provides NewsActivity Intent
 *
 * @author am.boldinov
 */
interface NewsActivityProvider : Feature {

    @Deprecated("Необходимо перейти на другой метод интерфейса")
    /**
     * Return NewsActivity Intent
     *
     * @param newsUuid - uuid of news.
     * @param messageUuid - uuid or related message. NULLABLE
     * @param dialogUuid - uuid of related dialog. NULLABLE
     * @param personUuid - uuid of related person to answer. NULLABLE
     * @param personName - name of related person to answer. NULLABLE
     */
    fun getNewsActivityIntent(newsUuid: String,
                              messageUuid: UUID?,
                              dialogUuid: UUID?,
                              personUuid: UUID?,
                              personName: String?): Intent

    /**
     * Возвращает Intent для открытия карточки новости
     *
     * @param newsUuid - идентификатор новости
     */
    fun getNewsIntent(newsUuid: String): Intent

    /**
     * Возвращает Intent для открытия карточки новости-мотивации
     *
     * @param newsUuid - идентификатор новости
     * @param positive - является ли мотивация положительной
     */
    fun getNewsMotivationIntent(newsUuid: String, positive: Boolean = true): Intent

    /**
     * Возвращает Intent для открытия карточки новости с ответом на указанный комментарий
     * и автоматической подстановской получателя в поле ввода
     *
     * @param newsUuid - идентификатор новости
     * @param messageUuid - идентификатор сообщения, по которому открывается новость
     * @param dialogUuid - идентификатор темы, к которой относится сообщение
     */
    fun getNewsReplyCommentIntent(newsUuid: String, messageUuid: UUID, dialogUuid: UUID): Intent

    /**
     * Возвращает Intent для открытия карточки новости с подскроллом к указанному комментарию
     *
     * @param newsUuid - идентификатор новости
     * @param messageUuid - идентификатор сообщения, по которому открывается новость и к которому необходимо подскроллить
     */
    fun getNewsShowCommentIntent(newsUuid: String, messageUuid: UUID): Intent

    /**
     * Возвращает Intent для открытия карточки новости с подскроллом к секции комментариев.
     *
     * @param newsUuid - идентификатор новости
     */
    fun getNewsSectionCommentIntent(newsUuid: String): Intent

    /**
     * Возвращает Fragment для открытия карточки новости
     */
    fun getNewsCardFragment(args: Bundle?): Fragment

    /**
     * Возвращает Fragment для открытия карточки новости по ее идентификатору.
     */
    fun getNewsCardFragment(newsUuid: String): Fragment

}