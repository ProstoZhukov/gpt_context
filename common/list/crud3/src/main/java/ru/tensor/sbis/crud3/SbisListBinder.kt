package ru.tensor.sbis.crud3

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.crud3.view.FirstItemHiddenWhenEmpty
import ru.tensor.sbis.crud3.view.datachange.DataChange
import ru.tensor.sbis.crud3.view.datachange.ItemChanged
import ru.tensor.sbis.crud3.view.datachange.ItemInserted
import ru.tensor.sbis.crud3.view.datachange.ItemMoved
import ru.tensor.sbis.crud3.view.datachange.ItemRemoved
import ru.tensor.sbis.crud3.view.datachange.SetItems
import ru.tensor.sbis.list.view.SbisList
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.section.ForceBackgroundColor
import ru.tensor.sbis.list.view.section.Section
import ru.tensor.sbis.list.view.section.SectionOptions
import ru.tensor.sbis.list.view.section.SectionsHolder
import ru.tensor.sbis.list.view.utils.Sections
import timber.log.Timber

/**
 * Подписка списочного компонента на круд вьюмодель.
 * @param viewModel вьюмодель на которую подписывается список.
 * @param config настройки отображения индикаторов загрузки страниц внутри списка.
 */
fun SbisList.bindWithViewModel(
    viewModel: CollectionViewModel<*, *>,
    config: ListLoadingIndicatorConfig = object : ListLoadingIndicatorConfig {
        override var suppressLoadNextIndicator: Boolean = false
        override var suppressLoadPrevIndicator: Boolean = false
    }
) {
    val viewTreeLifecycleOwner: LifecycleOwner =
        findViewTreeLifecycleOwner() ?: kotlin.run {
            Timber.e("ViewTreeLifecycleOwner not found")
            return
        }
    viewModel.loadNextAvailable.observe(viewTreeLifecycleOwner) {
        Timber.d("loadNextAvailable - $it")
        loadNextAvailability(it)
    }
    viewModel.loadPreviousAvailable.observe(viewTreeLifecycleOwner) {
        Timber.d("loadPreviousAvailable - $it")
        loadPreviousAvailability(it)
    }
    viewModel.scrollScrollToZeroPosition.observe(viewTreeLifecycleOwner) {
        smoothScrollToPosition(0)
    }
    viewModel.loadNextThrobberIsVisible.observe(viewTreeLifecycleOwner) {
        if (config.suppressLoadNextIndicator) return@observe
        Timber.d("loadNextThrobberIsVisible - $it")
        loadNextProgressIsVisible(it)
    }
    viewModel.loadPreviousThrobberIsVisible.observe(viewTreeLifecycleOwner) {
        if (config.suppressLoadPrevIndicator) return@observe
        Timber.d("loadPreviousThrobberIsVisible - $it")
        loadPreviousProgressIsVisible(it)
    }
    setDataChangedObserver(viewModel)
    setLoadMoreCallback(viewModel)
}

fun <ITEM : ItemWithSection<AnyItem>> SbisList.subscribeToDataChange(
    dataChange: Observable<DataChange<ITEM>>,
    firstItem: ItemWithSection<out AnyItem>?,
    forceBackgroundColor: ForceBackgroundColor = ForceBackgroundColor.NONE,
    onSetItemsCompleted: (hasItems: Boolean) -> Unit = {},
    forceScrollToInitialPosition: Boolean = false,
    priorityBackgroundColor: Int? = null
): Disposable =
    subscribeToDataChange(
        dataChange = dataChange,
        firstItems = firstItem?.let { listOf(it) } ?: emptyList(),
        forceBackgroundColor = forceBackgroundColor,
        onSetItemsCompleted = onSetItemsCompleted,
        forceScrollToInitialPosition = forceScrollToInitialPosition,
        priorityBackgroundColor = priorityBackgroundColor
    )

fun <ITEM : ItemWithSection<AnyItem>> SbisList.subscribeToDataChange(
    dataChange: Observable<DataChange<ITEM>>,
    firstItems: List<ItemWithSection<out AnyItem>>,
    forceBackgroundColor: ForceBackgroundColor = ForceBackgroundColor.NONE,
    onSetItemsCompleted: (hasItems: Boolean) -> Unit = {},
    forceScrollToInitialPosition: Boolean = false,
    priorityBackgroundColor: Int? = null
): Disposable =
    dataChange
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { result ->
            val firstItemOffset = firstItems.size
            val data = Sections(
                SectionsHolder(
                    result.allItems.toSections(firstItems),
                    forceBackgroundColor,
                    priorityBackgroundColor = priorityBackgroundColor
                ),
                forceScrollToInitialPosition = if (result is SetItems) forceScrollToInitialPosition else false
            )

            when (result) {
                is ItemInserted -> {
                    result
                        .toGroupsOfConsecutiveElements(firstItemOffset)
                        .forEach { (positionStart, itemCount) ->
                            notifyItemRangeInserted(positionStart = positionStart, itemCount = itemCount, data = data)
                        }
                }

                is ItemMoved -> {
                    result.indexPairs.forEach {
                        notifyItemChanged(it.first.toInt() + firstItemOffset, data)
                        notifyItemChanged(it.second.toInt() + firstItemOffset, data)
                    }
                }

                is ItemRemoved -> {
                    result.indexes.forEach { notifyItemRemoved(it.toInt() + firstItemOffset, data) }
                }

                is ItemChanged -> {
                    result.indexItemList.forEach { notifyItemChanged(it.first.toInt() + firstItemOffset, data) }
                }

                is SetItems -> {
                    setListData(data)
                    onSetItemsCompleted(result.allItems.isNotEmpty())
                }
            }
        }

private fun List<ItemWithSection<AnyItem>>.toSections(firstItems: List<ItemWithSection<out AnyItem>>): List<Section> {
    val result = linkedMapOf<SectionOptions, MutableList<AnyItem>>()

    firstItems.forEach { firstItem ->
        if (firstItemVisible(firstItem)) {
            result[firstItem.sectionOption] = mutableListOf(firstItem.item)
        }
    }

    this.forEach {
        result.getOrPut(it.sectionOption) { mutableListOf() }.add(it.item)
    }

    return result.map { Section(it.value, it.key) }
}

private fun List<ItemWithSection<AnyItem>>.firstItemVisible(firstItem: ItemWithSection<out AnyItem>): Boolean {
    return if (firstItem.item is FirstItemHiddenWhenEmpty) !isEmpty() else true
}