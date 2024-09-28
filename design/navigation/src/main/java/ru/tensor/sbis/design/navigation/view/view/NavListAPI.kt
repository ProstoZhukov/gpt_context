package ru.tensor.sbis.design.navigation.view.view

import android.view.View
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.navigation.util.ControllerNavIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.navigation.view.model.NavigationViewModel

/**
 * Интерфейс плоского списка для навигации(ННП и Аккордеон).
 *
 * @author ma.kolpakov
 */
internal interface NavListAPI {

    /** @SelfDocumented */
    fun setAdapter(map: Map<out NavigationItem, NavigationViewModel>)

    /** @SelfDocumented */
    fun remove(item: NavigationItem)

    /** @SelfDocumented */
    fun insert(item: NavigationItem, viewModel: NavigationViewModel)

    /**
     * Упорядочить ранее добавленные элементы, согласно порядку их следования в [items].
     * Новые элементы, содержащиеся в [items], будут проигнорированы.
     */
    fun reorder(items: List<NavigationItem>)

    /** @SelfDocumented */
    fun changeItemIcon(item: NavigationItem, icon: ControllerNavIcon)

    /** @SelfDocumented */
    fun changeItemLabel(item: NavigationItem, label: NavigationItemLabel)

    /** @SelfDocumented */
    fun hide(item: NavigationItem)

    /** @SelfDocumented */
    fun show(item: NavigationItem)

    /** @SelfDocumented */
    fun getItemPosition(item: NavigationItem): Int

    /** @SelfDocumented */
    fun getDisposable(): Disposable

    /** @SelfDocumented */
    fun getVisibleItemCount(): Int

    /** @SelfDocumented */
    fun getItemView(navigationItem: NavigationItem): View?

    /** @SelfDocumented */
    var showItemListener: ((NavigationItem) -> Unit)?

    /** @see NavViewConfiguration */
    var configuration: NavViewConfiguration
}