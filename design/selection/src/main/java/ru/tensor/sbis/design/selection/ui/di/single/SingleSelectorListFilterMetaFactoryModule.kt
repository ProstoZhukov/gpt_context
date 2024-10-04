package ru.tensor.sbis.design.selection.ui.di.single

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorListFilterMetaFactory
import ru.tensor.sbis.design.selection.ui.list.filter.SingleSelectorListFilterMetaFactory

/**
 * @author us.bessonov
 */
@Module
internal class SingleSelectorListFilterMetaFactoryModule {

    @Provides
    @SelectionListScreenScope
    fun provideSelectorListFilterMetaFactory(): SelectorListFilterMetaFactory<Any> =
        SingleSelectorListFilterMetaFactory()
}
