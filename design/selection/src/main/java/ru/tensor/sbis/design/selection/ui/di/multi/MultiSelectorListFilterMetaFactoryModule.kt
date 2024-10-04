package ru.tensor.sbis.design.selection.ui.di.multi

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.list.filter.MultiSelectorListFilterMetaFactory
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorListFilterMetaFactory

/**
 * @author us.bessonov
 */
@Module
internal class MultiSelectorListFilterMetaFactoryModule {

    @Provides
    @SelectionListScreenScope
    fun provideSelectorListFilterMetaFactory(): SelectorListFilterMetaFactory<Any> =
        MultiSelectorListFilterMetaFactory()
}
