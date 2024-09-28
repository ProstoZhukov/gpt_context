package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ShareMessagePanelCoreFactory
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.ContactsShareViewModel
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter.SelectedItemsAdapter
import ru.tensor.sbis.message_panel.delegate.MessagePanelInitializerDelegate
import ru.tensor.sbis.toolbox_decl.share.ShareData
import javax.inject.Named
import javax.inject.Scope

/**
 * DI-компонент раздела шаринга в контакты.
 *
 * @author vv.chekurda
 */
@ContactsShareScope
@Component(modules = [ContactsShareModule::class])
internal interface ContactsShareComponent {

    /**
     * Вью-модель экрана.
     */
    val viewModel: ContactsShareViewModel

    /**
     * Адаптер списка выбраных получателей.
     */
    val selectedItemsAdapter: SelectedItemsAdapter

    /**
     * Инициализатор панели сообщений.
     */
    val messagePanelInitializer: MessagePanelInitializerDelegate<Any, Any, Any>

    /**
     * Фабрика корневой информации для работы панели сообщений.
     */
    val coreFactory: ShareMessagePanelCoreFactory

    /**
     * Данные, которыми делится пользователь.
     */
    val shareData: ShareData

    /**
     * Ключ для быстрого шаринга.
     */
    @get:Named(QUICK_SHARE_KEY_NAME)
    val quickShareKey: String?

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance fragment: BaseFragment,
            @BindsInstance shareData: ShareData,
            @BindsInstance @Named(QUICK_SHARE_KEY_NAME) quickShareKey: String?
        ): ContactsShareComponent
    }
}

@Scope
@Retention
internal annotation class ContactsShareScope