package ru.tensor.sbis.design.selection.ui.di.common

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.ui.di.SelectionHostScope
import ru.tensor.sbis.design.selection.ui.utils.MultiSelectionFilterFunction

/**
 * @author us.bessonov
 */
@Module
internal class FilterFunctionModule {

    @Provides
    @SelectionHostScope
    fun providesFilterFunction(): FilterFunction = MultiSelectionFilterFunction()
}