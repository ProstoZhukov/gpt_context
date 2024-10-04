package ru.tensor.sbis.design.navigation.view.model.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.navigation.NavigationPlugin.navigationPreferences
import ru.tensor.sbis.design.navigation.view.model.createLiveData

/**
 * Модель дополнительного контента у элементов меню. Внутренние подписки нужно поместить в реализацию [Disposable], они
 * будут остановлены при удалении элемента меню:
 *
 * ```
 * class CustomNavigationContent(
 *     private val disposable: CompositeDisposable = CompositeDisposable()
 * ) : NavigationItemContent<View>, Disposable by disposable {
 *
 *     init {
 *         disposable.add(startSubscription())
 *     }
 * }
 * ```
 *
 * Если в реализации нет подписок:
 * ```
 * class CustomNavigationContent : NavigationItemContent<View>, Disposable by Disposables.empty()
 * ```
 *
 * @author ma.kolpakov
 */
interface NavigationItemContent : Disposable {

    /**
     * Подписка на изменение видимости дополнительного контента и кнопки раскрытия. Если дополнительный контент доступен
     * всегда (или по умолчанию), подписка должна возвращать `true`
     *
     * @sample createLiveData
     */
    val visibility: Observable<Boolean>

    /**
     * Способ раскрыть контент из кода. Работает аналогично [visibility]
     */
    val isExpand: Observable<Boolean>

    /**
     * Уникальный идентификатор контента
     */
    val id: String

    /**
     * Метод создаёт view дополнительного контента. Может возвращать как композитную view, так и [ViewGroup] в случае
     * использования layout ресурса. В случае [ViewGroup] элементы макета можно запомнить для дальнейшего использования
     * в при раскрытии [onContentOpened] и скрытии [onContentClosed] контента.
     */
    fun createContentView(inflater: LayoutInflater, container: ViewGroup): View

    /**
     * Обрабатывает раскрытие дополнительного контента. Метод подходит для запуска подписок, актуализации данных во view.
     */
    fun onContentOpened()

    /**
     * Обрабатывает скрытие дополнительного контента. Метод подходит для остановки подписок, освобождения ресурсов.
     * Метод вызывается, если дополнительный становится недоступным (см. [visibility]).
     */
    fun onContentClosed()

    /**
     * Метод для получения сохраннего в памяти устройства состояния открытия дополнительного контента.
     */
    fun getDefaultExpandedState(): Boolean = navigationPreferences.isExpanded(id)
}
