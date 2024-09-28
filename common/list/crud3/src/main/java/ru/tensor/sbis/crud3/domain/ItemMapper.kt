package ru.tensor.sbis.crud3.domain

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import ru.tensor.sbis.crud3.ComponentViewModel

/**
 * Маппер элемента коллекции контроллер в элемент списочного компонента.
 * Описание аргументов см в [ComponentViewModel].
 */
@AnyThread
interface ItemMapper<SOURCE_ITEM, OUTPUT_ITEM> {

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
     * Опции comparable: ComparableItem<DATA> и mergeable: MergeableItem<DATA> реализовывать НЕ нужно, CRUD3 компонент
     * их не использует.
     */
    @WorkerThread
    fun map(item: SOURCE_ITEM, defaultClickAction: (SOURCE_ITEM) -> Unit): OUTPUT_ITEM
}