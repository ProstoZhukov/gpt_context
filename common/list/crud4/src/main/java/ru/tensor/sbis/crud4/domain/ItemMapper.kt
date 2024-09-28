package ru.tensor.sbis.crud4.domain

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import ru.tensor.sbis.crud4.ComponentViewModel
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Маппер элемента коллекции контроллер в элемент списочного компонента.
 * Описание аргументов см в [ComponentViewModel].
 */
@AnyThread
interface ItemMapper<SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, OUTPUT_ITEM, IDENTIFIER> {

    /**
     * Преобразовать [item] в элемент списка. [defaultClickAction] - это событие клика, будет передано компонентом для
     * обработки извне.
     * Пример:
     * override fun map(item: LogPackageViewModel, defaultClickAction: (LogPackageViewModel) -> Unit): AnyItem {
     *  return BindingItem(
     *          item,
     *          DataBindingViewHolderHelper(R.layout.logging_log_package_item),
     *          options = Options( clickAction = { defaultClickAction(item) } )
     *      )
     * }
     *
     * Опции comparable: ComparableItem<DATA> и mergeable: MergeableItem<DATA> реализовывать НЕ нужно, crud4 компонент
     * их не использует.
     */
    @WorkerThread
    fun map(item: SOURCE_ITEM, actionDelegate: ItemActionDelegate<SOURCE_ITEM, IDENTIFIER>): OUTPUT_ITEM
}