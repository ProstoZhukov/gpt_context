package ru.tensor.sbis.pushnotification.controller.base.strategy

import ru.tensor.sbis.common.util.collections.Predicate
import ru.tensor.sbis.pushnotification.model.PushData

/**
 * Стратегия обновления пуш-уведомления.
 * Используется при изменении целевого документа на облаке для обновления информации в пуше.
 *
 * Если пуш уведомление по целевому документу было ранее опубликовано и пользователь к примеру удалил его по свайпу,
 * то стратегия не будет вызвана - обновлять нечего.
 *
 * @author am.boldinov
 */
interface PushUpdateStrategy<DATA : PushData> {

    /**
     * Возвращает предикат для проверки условия по пуш-уведомлениям на изменение.
     * Если измененные данные относятся к уже опубликованному пушу (ид документа, ид уведомления и т.д)
     * то необходимо вернуть true и содержимое пуша будет обновлено.
     */
    fun getUpdateMatcher(updateData: DATA): Predicate<DATA>?
}