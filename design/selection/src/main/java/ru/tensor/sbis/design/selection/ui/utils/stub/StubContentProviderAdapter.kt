package ru.tensor.sbis.design.selection.ui.utils.stub

import ru.tensor.sbis.design.selection.ui.contract.AllItemsSelected
import ru.tensor.sbis.design.selection.ui.contract.Data
import ru.tensor.sbis.design.selection.ui.contract.NoData
import ru.tensor.sbis.design.selection.ui.contract.SelectorStubContentProvider
import ru.tensor.sbis.list.base.presentation.StubViewContentFactory
import ru.tensor.sbis.list.base.utils.stub.StubContentProvider

/**
 * Адаптер для работы [SelectorStubContentProvider] в качестве [StubContentProvider]. Предоставляет дополнительную
 * информацию для заглушек специфичную для компонента выбора
 *
 * @author ma.kolpakov
 */
internal class StubContentProviderAdapter<in SERVICE_RESULT>(
    private val selectorStubContentProvider: SelectorStubContentProvider<SERVICE_RESULT>
) : StubContentProvider<SERVICE_RESULT> {

    /**
     * Отметка о выборе всех элементов
     */
    var allItemsSelected = false

    override fun provideStubViewContentFactory(result: SERVICE_RESULT?): StubViewContentFactory = when {
        allItemsSelected -> selectorStubContentProvider.provideStubViewContentFactory(AllItemsSelected)
        result != null -> selectorStubContentProvider.provideStubViewContentFactory(Data(result))
        else -> selectorStubContentProvider.provideStubViewContentFactory(NoData)
    }
}