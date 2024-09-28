package ru.tensor.sbis.design.message_panel.di.vm

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModelImpl

/**
 * Модуль поставляет [MessagePanelViewModel] из [ViewModelProvider]
 *
 * @author ma.kolpakov
 */
@Module
internal class MessagePanelViewStoreModule {

    @Provides
    fun provideViewModel(view: View, factory: ViewModelProvider.Factory): MessagePanelViewModel {
        val storeOwner = view.findViewTreeViewModelStoreOwner()!!
        return ViewModelProvider(storeOwner, factory)[MessagePanelViewModelImpl::class.java]
    }
}
