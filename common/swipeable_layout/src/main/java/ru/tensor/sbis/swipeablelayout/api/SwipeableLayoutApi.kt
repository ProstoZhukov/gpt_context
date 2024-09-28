package ru.tensor.sbis.swipeablelayout.api

import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.view.SwipeMenuItemsContainer

typealias SwipeEventListener = (SwipeEvent) -> Unit

typealias ItemInListChecker = (String) -> Boolean

/**
 * API компонента "Свайп-меню".
 *
 * @see [SwipeMenuItemsContainer.setMenu]
 *
 * @author us.bessonov
 */
interface SwipeableLayoutApi : SwipeMenuItemsContainer {

    /**
     * Идентификатор элемента.
     * Необходимо устанавливать для корректного сохранения состояния свайп-меню и для идентификации событий при
     * подписке.
     */
    var itemUuid: String?

    /**
     * Определяет возможность смахивания элемента (удаления пользовательским жестом), и тип её реализации.
     * По умолчанию, смахивание отключено.
     *
     * @see SwipeItemDismissType
     */
    var itemDismissType: SwipeItemDismissType

    /**
     * Заблокирована ли возможность двигать содержимое или меню посредством жестов.
     * Параметр не влияет на управление состоянием из кода (методы [openMenu], [dismiss], [close] работают без
     * изменений)
     */
    var isDragLocked: Boolean

    /**
     * Задаёт необходимость смахивания содержимого вправо.
     * Актуально только если не задано меню. По умолчанию, содержимое смахивается влево.
     */
    var shouldSwipeContentToRight: Boolean

    /**
     * Последнее событие изменения состояния элемента.
     *
     * @see [SwipeEvent]
     */
    val lastEvent: SwipeEvent

    /**
     * Опциональный радиус скругления углов контейнера.
     */
    var cornerRadius: BorderRadius?

    /**
     * Задать элементы левого свайп-меню.
     * Предназначено для использования в дополнение к основному (правому) меню.
     * Задание только левого меню НЕ РЕКОМЕНДУЕТСЯ.
     *
     * @see [setMenu]
     */
    fun <ITEM : SwipeMenuItem> setLeftMenu(items: List<ITEM>)

    /**
     * Задать обработчик всех событий изменения состояния свайпа.
     *
     * @param listenerId Идентификатор обработчика. При указании значения, отличного от `null`, повторная установка
     * обработчика с одинаковым [listenerId] заменяет предыдущий обработчик.
     */
    fun addEventListener(listenerId: String? = null, listener: SwipeEventListener)

    /** @SelfDocumented */
    fun removeEventListener(listener: SwipeEventListener)

    /** @SelfDocumented */
    fun removeEventListener(listenerId: String)

    /**
     * Установить лямбду, которая определяет наличие элемента с заданным uuid в списке данных.
     * Необходимо использовать, если задан `itemDismissType = [SwipeItemDismissType.CANCELLABLE]`.
     * После смахивания элемента, который можно восстановить, компонент отслеживает изменения в адаптере, и должен
     * понимать, присутствует ли элемент физически в новом списке, потому что в противном случае элемент должен быть
     * удалён сразу, без ожидания таймаута.
     */
    fun setItemInListChecker(isItemInList: ItemInListChecker?)

    /**
     *
     * Открыть правое свайп-меню, если оно задано.
     *
     * @param animated требуется ли открыть меню с анимацией, а не немедленно.
     */
    fun openMenu(animated: Boolean = true)

    /**
     * Открыть левое свайп-меню, если оно задано.
     *
     * @param animated требуется ли открыть меню с анимацией, а не немедленно.
     */
    fun openLeftMenu(animated: Boolean = true)

    /**
     * Закрывает свайп-меню, если оно задано, иначе возвращает содержимое на исходную позицию.
     *
     * @param animated требуется ли выполнить действие с анимацией, а не немедленно
     */
    fun close(animated: Boolean = true)

    /**
     * Программно инициирует смахивание для удаления элемента. Если задано меню, то оно перекроет
     * содержимое, а иначе содержимое скроется с экрана, после чего отобразится сообщение об удалении.
     *
     * @param animated требуется ли выполнить смахивание с анимацией, а не немедленно
     */
    fun dismiss(animated: Boolean = true)

    /**
     * Программно инициирует смахивание для удаления элемента, выполняемое слева направо.
     *
     * @see dismiss
     */
    fun dismissLeft(animated: Boolean = true)

    /**
     * Задаёт текст, отображаемый после смахивания элемента.
     */
    fun setDismissMessage(dismissMessage: String)

    /** @SelfDocumented */
    fun releaseListeners()
}
