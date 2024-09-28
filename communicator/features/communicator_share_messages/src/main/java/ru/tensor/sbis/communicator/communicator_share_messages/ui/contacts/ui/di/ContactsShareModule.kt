package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.di

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelperImpl
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.live_data.ContactsShareLiveData
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ShareMessageServiceDependency
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.ContactsShareViewModel
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.ContactsShareViewModelFactory
import ru.tensor.sbis.communicator.communicator_share_messages.utils.ContactsInfoUtil
import ru.tensor.sbis.communicator.communicator_share_messages.utils.OfflineLinksUtil
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter.SelectedItemsAdapter
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter.SelectedItemsAdapterFactory
import ru.tensor.sbis.message_panel.delegate.MessagePanelInitializerDelegate

/**
 * DI-модуль раздела шаринга в контакты.
 *
 * @author vv.chekurda
 */
@Module
internal class ContactsShareModule {

    @Provides
    @ContactsShareScope
    fun provideContactsShareViewModel(
        fragment: BaseFragment,
        factory: ContactsShareViewModelFactory
    ): ContactsShareViewModel =
        ViewModelProvider(fragment, factory)[ContactsShareViewModel::class.java]

    @Provides
    @ContactsShareScope
    fun provideSelectedItemsAdapter(
        viewModel: ContactsShareViewModel
    ): SelectedItemsAdapter =
        SelectedItemsAdapterFactory(viewModel).create()

    @Provides
    @ContactsShareScope
    fun provideMessagePanelInitializerDelegate(
        fragment: BaseFragment,
        messageServiceDependency: ShareMessageServiceDependency
    ): MessagePanelInitializerDelegate<Any, Any, Any> =
        MessagePanelInitializerDelegate(
            context = fragment.requireContext(),
            fragment = fragment,
            withAudioMessage = false,
            messageServiceDependency = messageServiceDependency
        )

    @Provides
    @ContactsShareScope
    fun provideContactsShareLiveData(): ContactsShareLiveData =
        ContactsShareLiveData()

    @Provides
    @ContactsShareScope
    fun provideShareMessageServiceDependency(): ShareMessageServiceDependency =
        ShareMessageServiceDependency()

    @Provides
    @ContactsShareScope
    fun provideQuickShareHelper(fragment: BaseFragment): QuickShareHelper =
        QuickShareHelperImpl(fragment.requireContext().applicationContext)

    @Provides
    @ContactsShareScope
    fun provideContactsInfoUtil(fragment: BaseFragment): ContactsInfoUtil =
        ContactsInfoUtil(fragment.requireContext().applicationContext)

    @Provides
    @ContactsShareScope
    fun provideOfflineLinksUtil(fragment: BaseFragment): OfflineLinksUtil =
        OfflineLinksUtil(fragment.requireContext().applicationContext)
}

internal const val QUICK_SHARE_KEY_NAME = "QUICK_SHARE_KEY_NAME"