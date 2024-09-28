package ru.tensor.sbis.design.navigation.view.adapter

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.design.navigation.view.DebouncedObserver
import ru.tensor.sbis.design.navigation.view.model.AllItemsUnselected
import ru.tensor.sbis.design.navigation.view.model.ItemSelected
import ru.tensor.sbis.design.navigation.view.model.ItemSelectedByUser
import ru.tensor.sbis.design.navigation.view.model.NavViewModel
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter
import ru.tensor.sbis.design.navigation.view.model.NavigationCounters
import ru.tensor.sbis.design.navigation.view.model.NavigationEvent
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemState
import ru.tensor.sbis.design.navigation.view.model.NavigationViewModel
import ru.tensor.sbis.design.navigation.view.model.SelectedByUserState
import ru.tensor.sbis.design.navigation.view.model.SelectedSame
import ru.tensor.sbis.design.navigation.view.model.SelectedSameItem
import ru.tensor.sbis.design.navigation.view.model.SelectedState
import ru.tensor.sbis.design.navigation.view.model.UnselectedState
import ru.tensor.sbis.design.navigation.view.model.content.NavigationItemContent
import ru.tensor.sbis.design.navigation.view.view.NavListAPI
import ru.tensor.sbis.design.navigation.view.view.NavViewConfiguration
import ru.tensor.sbis.design.navigation.view.view.navmenu.icon_button.NavIconButton
import ru.tensor.sbis.design.navigation.view.view.navmenu.icon_button.NavIconButtonImpl
import ru.tensor.sbis.design.navigation.view.widget.CounterWidget
import ru.tensor.sbis.design.navigation.view.widget.EmptyWidget
import ru.tensor.sbis.design.navigation.view.widget.NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT
import ru.tensor.sbis.design.navigation.view.widget.NavItemWidget
import ru.tensor.sbis.design.navigation.view.widget.ScannerWidget
import ru.tensor.sbis.design.utils.checkNotNullSafe
import java.util.TreeMap
import java.util.WeakHashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * Адаптер, который хранит элементы меню и обеспечивает синхронизацию
 * внутренних событий и событий выбора элементов меню.
 *
 * @param sorted если параметр равен `true` элементы меню должны реализовать [Comparable], они будут
 * отсортированы их реализацией [Comparable]. Если атрибут `false` элементы меню будут размещены в
 * порядке добавления
 * @param E тип элемента меню. Реализация [NavigationItem]
 *
 * @author ma.kolpakov
 * Создан 11/8/2018
 */
@MainThread
class NavAdapter<E : NavigationItem>(
    private val lifecycleOwner: LifecycleOwner,
    internal val disposable: CompositeDisposable,
    sorted: Boolean = false
) {
    private val currentSelected = mutableListOf<E>()

    /**
     * Список пунктов навигации.
     */
    internal val itemsMap: MutableMap<E, NavigationViewModel> = if (sorted) TreeMap() else LinkedHashMap()
    private val itemParentsMap: MutableMap<E, E> = if (sorted) TreeMap() else LinkedHashMap()
    private val adaptersMap: MutableMap<String, NavListAPI> = WeakHashMap()

    private val eventsLiveData = MutableLiveData<NavigationEvent<E>>()

    /**
     * Возвращает объект [LiveData] для подписки на события компонентов навигации (например,
     * изменение "выбранного" элемента меню). В случае, когда удаляется "выбранный" элемент меню,
     * подписчикам будет доставлено событие [AllItemsUnselected].
     *
     * Если потребитель не успевает обрабатывать события (например, при долгом создании визуальных
     * компонентов) рекомендуется "обернуть" подписку в [DebouncedObserver].
     *
     * При наличии подпунктов события выбора родительского пункта не публикуются.
     */
    val navigationEvents: LiveData<NavigationEvent<E>> = eventsLiveData

    /**
     * @see NavViewConfiguration
     */
    val configuration: NavViewConfiguration = NavViewConfiguration.SCROLL

    /**
     * Добавление элемента меню. Будет добавлен в конец списка, если [NavAdapter] создан с
     * параметром `sorted==false` иначе - в позицию, которая определяется с помощью [Comparable].
     * Если задан родительский пункт меню, то элемент считается подпунктом. Если выбран подпункт, то и родительский
     * элемент становится выбранным. В любой момент времени может быть выбран либо только пункт без подпунктов, либо
     * пункт и один из его подпунктов
     *
     * @param item элемент меню.
     * @param counter счётчик пункта меню.
     * @param parent родительский элемент меню.
     *
     * @throws [IllegalArgumentException], если пункт меню уже добавлен.
     */
    @JvmOverloads
    fun add(item: E, counter: NavigationCounter? = null, parent: E? = null, alignmentItem: E? = null) {
        add(item, counter?.run(::CounterWidget) ?: EmptyWidget, null, parent, alignmentItem)
    }

    /**
     * Добавление элемента меню с дополнительным контентом. Будет добавлен в конец списка, если [NavAdapter] создан с
     * параметром `sorted==false` иначе - в позицию, которая определяется с помощью [Comparable].
     *
     * @param item элемент меню.
     * @param content дополнительный контент элемента меню.
     *
     * @throws [IllegalArgumentException], если пункт меню уже добавлен.
     */
    @JvmOverloads
    fun add(item: E, content: NavigationItemContent, counter: NavigationCounter? = null) {
        add(item, counter?.run(::CounterWidget) ?: EmptyWidget, content, null, null)
    }

    /**
     * Добавление элемента меню. Будет добавлен в конец списка, если [NavAdapter] создан с
     * параметром `sorted==false` иначе - в позицию, которая определяется с помощью [Comparable].
     *
     * @param item элемент меню.
     * @param widget модель виджета для добавления в пункт меню.
     *
     * @throws [IllegalArgumentException], если пункт меню уже добавлен.
     */
    @Deprecated(NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT)
    fun add(item: E, widget: NavItemWidget = EmptyWidget) {
        // метод намеренно не предоставляет возможность работы с дочерними элементами для виджетов - не проработано
        add(item, widget, null, null, null)
    }

    /**
     * Добавление коллекции элементов меню.
     *
     * @see add
     */
    fun add(items: Map<out E, NavigationCounter?>) {
        items.forEach { (item, counter) -> add(item, counter) }
    }

    /**
     * Удаление элемента меню. Если элемент отсутствует в меню, метод отработает без какого-либо
     * эффекта.
     *
     * @param item элемент меню, который нужно удалить.
     * @return boolean - true, если элемент удалён, иначе false.
     */
    fun remove(item: E): Boolean =
        itemsMap[item]?.let {
            applyToEachAdapter { remove(item) }
            itemsMap.remove(item)
            true
        } ?: false

    /**
     * Упорядочить ранее добавленные элементы, согласно порядку их следования в [items].
     * Новые элементы, содержащиеся в [items], будут проигнорированы.
     */
    fun reorder(items: List<E>) {
        applyToEachAdapter { reorder(items) }
    }

    /**
     * Установка "выбранного" элемента меню. Выбранный элемент меню будет доставлен подписчикам
     * [navigationEvents] в событии [ItemSelected], при этом предыдущий выбранный элемент меню,
     * станет невыбранным. Если элемент отсутствует в меню, метод отработает без какого-либо
     * эффекта.
     *
     * @param item элемент меню, который нужно отметить "выбранным".
     */
    fun setSelected(item: E) {
        setSelected(item, null)
    }

    /**
     * Добавление элемента меню. Будет добавлен в конец списка, если [NavAdapter] создан с
     * параметром `sorted==false` иначе - в позицию, которая определяется с помощью [Comparable].
     * Если задан родительский пункт меню, то элемент считается подпунктом. Если выбран подпункт, то и родительский
     * элемент становится выбранным. В любой момент времени может быть выбран либо только пункт без подпунктов, либо
     * пункт и один из его подпунктов.
     *
     * @param item элемент меню.
     * @param counter счётчик пункта меню.
     * @param iconButton кнопка с иконкой справа.
     * @param content прикладной контент отображаемый внутри раскрываемого элемента.
     * @param parent родительский элемент меню.
     *
     * @throws [IllegalArgumentException], если пункт меню уже добавлен.
     */
    fun add(
        item: E,
        counter: NavigationCounter? = null,
        iconButton: NavIconButton? = null,
        content: NavigationItemContent? = null,
        parent: E? = null,
        alignmentItem: E? = null
    ) {
        require(item !in itemsMap.keys) { "Navigation item $item already present" }
        require(item != parent) { "Navigation item cannot be a parent of itself" }
        val vm = addVm(item, counter, content, iconButton, parent, alignmentItem)
        applyToEachAdapter { insert(item, vm) }
    }

    /**
     * Обновить значение счётчиков у элементов списка.
     *
     * @param counters список из соотношений идентификатора элемента [NavigationItem.persistentUniqueIdentifier]
     * и модели счётчика [NavigationCounters].
     */
    fun updateCounters(counters: Map<String, NavigationCounters>) {
        itemsMap.onEach { (item, vm) ->
            val counter =
                if (counters.isEmpty()) NavigationCounters(item.counterName, 0, 0, 0)
                else counters[item.counterName]
            counter?.let {
                vm.updateCounters(it)
            }
        }
    }

    /**
     * Установка "выбранного" элемента меню.
     *
     * @param item элемент меню, который нужно отметить "выбранным".
     * @param sourceName название источника, который изменил состояние.
     */
    private fun setSelected(item: E, sourceName: String? = null) {
        val isSameItem = currentSelected.contains(item)
        currentSelected.forEach { selectState(it, UnselectedState) }
        currentSelected.clear()
        val state =
            if (isSameItem) SelectedSame else sourceName?.let { SelectedByUserState(sourceName) } ?: SelectedState

        val parentItem = itemParentsMap[item]

        val firstChildItem = itemParentsMap.filterValues { it == item }.firstNotNullOfOrNull { it.key }

        selectState(item, state)
        parentItem?.let { selectState(it, state, false) }
        firstChildItem?.let { selectState(it, state, !isSameItem) }
    }

    /**
     * Установка элементов [itemsMap] в адаптер списка.
     */
    internal fun setController(viewHelper: NavigationViewHelper, navListView: NavListAPI) {
        viewHelper.lifecycleOwner = lifecycleOwner
        adaptersMap[viewHelper.sourceName] = navListView
        navListView.setAdapter(itemsMap)
        disposable.add(navListView.getDisposable())
    }

    private fun selectState(item: E, state: NavigationItemState, sendNavigationEvent: Boolean = true) {
        val itemState = checkNotNullSafe(itemsMap[item]) {
            "Can't find item in map $itemsMap with key $item"
        }?.state ?: return
        if (state !is UnselectedState) {
            // добавить item в список выделенных
            currentSelected.add(item)
            // обновить состояние элемента
            itemState.onNext(state)

            if (!sendNavigationEvent) return
            // оповестить прикладной код об изменении
            eventsLiveData.value = when (state) {
                SelectedSame -> SelectedSameItem(item, itemParentsMap[item])
                is SelectedByUserState -> ItemSelectedByUser(item, itemParentsMap[item], state.sourceName)
                SelectedState -> ItemSelected(item, itemParentsMap[item])
                UnselectedState -> throw IllegalStateException("Невалидный state элемента навигации $state")
            }
        } else {
            // если снимаем выделение то просто обновляем состояние
            itemState.onNext(state)
        }

    }

    @Deprecated(NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT)
    private fun add(item: E, widget: NavItemWidget, content: NavigationItemContent?, parent: E?, alignmentItem: E?) {
        require(item !in itemsMap.keys) { "Navigation item $item already present" }
        require(item != parent) { "Navigation item cannot be a parent of itself" }
        val counter = if (widget is CounterWidget) widget.counter else null
        val vm = addVm(item, counter, content, widget, parent, alignmentItem)
        applyToEachAdapter { insert(item, vm) }
    }

    private fun addVm(
        item: E,
        counter: NavigationCounter?,
        content: NavigationItemContent?,
        widget: NavItemWidget,
        parent: E?,
        alignmentItem: E?
    ): NavigationViewModel {
        val navIconButton = (widget as? ScannerWidget)?.let {
            disposable.add(widget.clickListener)
            NavIconButtonImpl(
                widget.icon,
            ) { widget.clickListener.onIconClicked() }
        }
        return addVm(item, counter, content, navIconButton, parent, alignmentItem)
    }

    /**
     * Добавление модели представления элементов меню и подписки на их выбор
     */
    private fun addVm(
        item: E,
        counter: NavigationCounter?,
        content: NavigationItemContent?,
        iconButton: NavIconButton?,
        parent: E?,
        alignmentItem: E?
    ): NavigationViewModel {
        val vm = NavViewModel(item, content, counter, iconButton, ::setSelected)
        vm.ordinal = item.ordinal
        itemsMap[item] = vm
        parent?.let {
            itemParentsMap[item] = parent
        }
        alignmentItem?.let {
            vm.parentOrdinal = it.ordinal
        }

        content?.let { disposable.add(it) }
        return vm
    }

    private fun applyToEachAdapter(action: NavListAPI.() -> Unit) {
        adaptersMap.forEach { (_, adapter) ->
            adapter.action()
        }
    }
}
