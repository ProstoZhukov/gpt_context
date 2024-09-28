package ru.tensor.sbis.recipient_selection.profile.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.recipient_selection.profile.ui.resultmanager.RecipientSelectionResultManager
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.recipient_selection.profile.mapper.FolderAndGroupItemMapper
import ru.tensor.sbis.recipient_selection.profile.mapper.ProfileAndContactItemMapper
import ru.tensor.sbis.recipient_selection.profile.contract.RecipientSelectionDependency
import ru.tensor.sbis.recipient_selection.profile.mapper.ContactItemMapper
import javax.inject.Named


/**
 * DI компонент для внедрения общих зависимостей в компоненте выбора получателей
 */
@PerApp
@Component(
        modules = [RecipientSelectionSingletonModule::class],
        dependencies = [CommonSingletonComponent::class]
)
abstract class RecipientSelectionSingletonComponent {

    internal abstract fun getNetworkUtils(): NetworkUtils

    internal abstract fun getContext(): Context

    internal abstract fun getProfileAndContactItemMapper(): ProfileAndContactItemMapper
    internal abstract fun getFolderAndGroupItemMapper(): FolderAndGroupItemMapper
    internal abstract fun getContactItemMapper(): ContactItemMapper

    // нельзя пометить internal, так как используется в java классах - получаем длинное нечитаемое наименование метода
    abstract val dependency: RecipientSelectionDependency

    internal abstract fun getRecipientSelectionResultManager(): RecipientSelectionResultManager

    @Named(CONTACT_SELECTION_RESULT_MANAGER_FOR_REPOST)
    internal abstract fun getContactsSelectionResultManagerForRepost(): RecipientSelectionResultManager

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance dependency: RecipientSelectionDependency,
            commonComponent: CommonSingletonComponent
        ): RecipientSelectionSingletonComponent
    }
}