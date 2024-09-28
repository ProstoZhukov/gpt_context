package ru.tensor.sbis.pushnotification.controller.base.strategy

import android.os.Bundle
import ru.tensor.sbis.common.util.collections.Predicate
import ru.tensor.sbis.pushnotification.model.PushData
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage

/**
 * Стратегия удаления пуш-уведомлений.
 * Используется при удалении уже опубликованных пушей программно, например при открытии карточки,
 * а так же при получении пуш уведомления с облака о прочтении документа, которое должно инициировать удаление
 * пуша из шторки.
 *
 * @author am.boldinov
 */
interface PushCancelStrategy<DATA : PushData> {

    /**
     * Возвращает предикат для проверки условия по пуш-уведомлениям на удаление, которые приходят с облака:
     * 1) Если данные пуша на удаление совпадают с данными по опубликованному пушу (ид уведомления, ид документа),
     * то необходимо вернуть true - пуш будет удален
     * 2) Если дата публикации пуша об удалении меньше чем дата публикации оригинального пуша, то данный пуш удалять
     * не нужно, т.к скорее всего он был переопубликован с облака.
     * Таким образом можно реализовать любую прикладную логику по проверке валидности удаления.
     *
     * @param cancelMessage пуш-сообщение с данными по пушу, который необходимо удалить
     */
    fun getOuterCancelMatcher(cancelMessage: PushNotificationMessage): Predicate<DATA>?

    /**
     * Возвращает предикат для проверки условия по удалению пуш-уведомлений из шторки, удаление которых было
     * инициировано внутри приложения (программно), например при открытии карточки.
     * @see [ru.tensor.sbis.pushnotification.center.PushCenter.cancel]
     *
     * @param cancelParams набор прикладных параметров, по которым можно найти и удалить опубликованный пуш,
     * например (ид уведомления, ид диалога, ид новости и т.д)
     * @see [ru.tensor.sbis.pushnotification.contract.PushCancelContract]
     */
    fun getInnerCancelMatcher(cancelParams: Bundle): Predicate<DATA>?
}