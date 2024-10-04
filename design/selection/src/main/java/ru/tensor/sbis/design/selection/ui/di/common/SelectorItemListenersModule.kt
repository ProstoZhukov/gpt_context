package ru.tensor.sbis.design.selection.ui.di.common

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectorItemListeners
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.selectorItemListeners

/**
 * Модуль для предоставления прикладных подписчиков нажатий по элементам [SelectorItemListeners]
 *
 * @author ma.kolpakov
 */
@Module
internal class SelectorItemListenersModule {

    @Provides
    @SelectionListScreenScope
    fun provideSelectorItemListeners(arguments: Bundle): SelectorItemListeners<SelectorItemModel, FragmentActivity> =
        arguments.selectorItemListeners

    @Provides
    @SelectionListScreenScope
    fun provideIconClickListener(
        listeners: SelectorItemListeners<SelectorItemModel, FragmentActivity>
    ): ItemClickListener<SelectorItemModel, FragmentActivity>? =
        listeners.iconClickListener
}
