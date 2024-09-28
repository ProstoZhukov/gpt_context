package ru.tensor.sbis.design.navigation.view.view.util

import androidx.lifecycle.Observer

/**
 * Заглушка подписки, чтобы счётчики не теряли значения, пока нет подписок со стороны view.
 * Подробнее в [статье](https://n.sbis.ru/news/e72dc383-c5e4-47bb-976c-a04986d14937).
 *
 * @author ma.kolpakov
 */
internal object ObserverStub : Observer<Any?> {
    override fun onChanged(t: Any?) = Unit
}