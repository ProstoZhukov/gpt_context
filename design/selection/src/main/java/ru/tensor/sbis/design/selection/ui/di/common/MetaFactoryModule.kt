package ru.tensor.sbis.design.selection.ui.di.common

import dagger.Binds
import dagger.Module
import ru.tensor.sbis.design.selection.ui.di.SelectionHostScope
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactoryImpl

/**
 * Модуль для предоставления реализации [ItemMetaFactory]
 *
 * @author ma.kolpakov
 */
@Module
internal interface MetaFactoryModule {

    @Binds
    @SelectionHostScope
    fun bind(impl: ItemMetaFactoryImpl): ItemMetaFactory
}