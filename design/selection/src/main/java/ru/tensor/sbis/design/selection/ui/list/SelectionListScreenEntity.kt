package ru.tensor.sbis.design.selection.ui.list

import android.os.Looper
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchMode
import ru.tensor.sbis.design.selection.ui.contract.list.ResultMapper
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorFilterCreator
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.stub.StubContentProviderAdapter
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.FixedButtonViewModel
import ru.tensor.sbis.design.utils.checkSafe
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Plain

/**
 * @author ma.kolpakov
 */
internal class SelectionListScreenEntity<SERVICE_RESULT : Any, FILTER, ANCHOR>(
    private val mapper: ResultMapper<SERVICE_RESULT>,
    private val filterCreator: SelectorFilterCreator<FILTER, ANCHOR>,
    private val prefetchCheckFunction: PrefetchCheckFunction<SelectorItemModel>,
    private val stubContentProvider: StubContentProviderAdapter<SERVICE_RESULT>,
    private val pagingEntity: PagingEntity<ANCHOR, SERVICE_RESULT, FILTER>,
    private val workerThreadCheck: () -> Unit = {
        checkSafe(Looper.myLooper() != Looper.getMainLooper()) { "Illegal cache initialisation on main thread" }
    }
) : PagingListScreenEntity<FILTER> by pagingEntity, FilterProvider<FILTER> {

    /**
     * Кэш данных, которые готовы для отображения. Возникла необходимость, т.к. в сценарии с [FixedButtonViewModel]
     * требуется на этапе проверки [isStub] понимать, с какими именно данными имеем дело
     */
    @Volatile
    private var cache: Lazy<Plain> = newCache()

    /**
     * Отметка о том, что данные ещё не загружались. В таком случае не нужно показывать заглушку
     */
    @Volatile
    private var isEmpty = true

    /**
     * Проверяет, нужно ли подгрузить данные на страницу. Направление загрузки нужно определить с использованием методов
     * [hasNext] и [hasPrevious]
     */
    fun needToPrefetch(): PrefetchMode? =
        // используется то же самое хранилище, что и для фабрики фильтров (состав данных один)
        prefetchCheckFunction.needToPrefetch(filterCreator.selection, filterCreator.availableItems)

    @WorkerThread
    fun update(page: Int, result: SERVICE_RESULT) {
        pagingEntity.update(page, result)
        cache = newCache().initialize()
        isEmpty = false
    }

    @AnyThread
    fun setSelection(selection: List<SelectorItemModel>) {
        mapper.selection = selection
        filterCreator.selection = selection
        cache = newCache()
    }

    override fun toListData(): ListData = cache.value

    @AnyThread
    override fun cleanPagesData() {
        isEmpty = true
        pagingEntity.cleanPagesData()
        cache = newCache()
    }

    override fun isStub(): Boolean {
        stubContentProvider.allItemsSelected = false
        return when {
            isEmpty -> false
            checkSelectedItemFound() -> false
            checkChooseAllItem() -> true
            checkAllItemsSelected() -> {
                stubContentProvider.allItemsSelected = true
                true
            }
            /*
            Если ранее были выбраны все доступные элементы, pagingEntity может быть пуста, но заглушка в этом случае
            отображаться не должна
            */
            else -> pagingEntity.isStub() && (mapper.searchQuery.isNotEmpty() || !mapper.hasRecentlySelectedItems())
        }
    }

    override fun provideFilterForNextPage(): FilterAndPageProvider<FILTER> {
        return pagingEntity.filterForNext(filterCreator.createForNextPage)
    }

    override fun provideFilterForPreviousPage(): FilterAndPageProvider<FILTER> {
        return pagingEntity.filterForPrevious(filterCreator.createForPreviousPage)
    }

    /**
     * Проверяет наличие результатов поиска следи выбранных элементов. В механиках с использованием exlude list
     * выбранные элементы могут не приходить в общем списке данных
     */
    private fun checkSelectedItemFound(): Boolean =
        mapper.filteredSelection.isNotEmpty() && mapper.searchQuery.isNotEmpty()

    private fun checkChooseAllItem(): Boolean {
        val item = cache.value.data.singleOrNull()?.data as SelectorItemModel? ?: return false
        return item.meta.handleStrategy == ClickHandleStrategy.COMPLETE_SELECTION && !pagingEntity.hasNext()
    }

    private fun checkAllItemsSelected(): Boolean =
        cache.value.data.isEmpty() && mapper.filteredSelection.isNotEmpty()

    @AnyThread
    private fun newCache(): Lazy<Plain> = lazy(LazyThreadSafetyMode.NONE) {
        mapper.searchQuery = filterCreator.searchQuery
        val listData = pagingEntity.toListData() as Plain
        val availableItems = listData.data
        filterCreator.availableItems = availableItems.map { it.data as SelectorItemModel }
        listData
    }

    @WorkerThread
    private fun <T> Lazy<T>.initialize(): Lazy<T> = this.apply {
        workerThreadCheck.invoke()
        // запрос значения для заполнения кэша в фоновом потоке
        value
    }
}