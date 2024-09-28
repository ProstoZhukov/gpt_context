package ru.tensor.sbis.common.navigation

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.design.navigation.view.view.HideableNavigationView
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView

/**
 * Класс для подписки [TabNavView] на события прокрутки. Подписку рекомендуется осуществлять, когда
 * [HideableNavigationView] готово к использованию (появилось на экране), например, в методе
 * `Activity.onStart()`
 *
 * @author ma.kolpakov
 * Создан 11/22/2018
 */
class TabNavScrollHelper private constructor(
        private val subscription: Disposable
) : Disposable by subscription {

    /**
     * @param scrollHelper источник событий [ScrollEvent]. В конструкторе производится подписка на
     * [ScrollHelper.getScrollEventObservable]
     * @param view модель представления, которое поддерживает динамическое скрытие и отображение
     * @param onError обработчик ошибок. По умолчанию используется [FallbackErrorConsumer.DEFAULT]
     */
    @JvmOverloads
    constructor(scrollHelper: ScrollHelper, view: HideableNavigationView,
                onError: Consumer<in Throwable> = FallbackErrorConsumer.DEFAULT) :
            this(scrollHelper.scrollEventObservable.subscribe({ event ->
                when (event) {
                    null -> throw NullPointerException("Nullable scroll event is not supported")
                    ScrollEvent.SCROLL_UP -> view.show()
                    ScrollEvent.SCROLL_UP_FAKE -> view.showAndUnlock()
                    ScrollEvent.SCROLL_DOWN -> view.hide()
                    ScrollEvent.SCROLL_DOWN_FAKE -> view.hideAndLock()
                    ScrollEvent.SCROLL_UP_FAKE_SOFT -> view.show(false)
                }
            }, onError)) {
                when (scrollHelper.latestEvent) {
                    ScrollEvent.SCROLL_UP_FAKE      -> view.showAndUnlock()
                    ScrollEvent.SCROLL_DOWN_FAKE    -> view.hideAndLock()
                    else                            -> { /* ignore */ }
                }
            }
}