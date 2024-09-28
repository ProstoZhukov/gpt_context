package ru.tensor.sbis.design.recipient_selection.ui.di.singleton

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.design.recipient_selection.contract.RecipientSelectionDependency


/**
 * Singleton DI-компонент модуля выбора получателей.
 *
 * @author vv.chekurda
 */
@PerApp
@Component(
    modules = [RecipientSelectionSingletonModule::class],
    dependencies = [CommonSingletonComponent::class]
)
interface RecipientSelectionSingletonComponent {

    val context: Context

    val dependency: RecipientSelectionDependency

    val recipientSelectionManager: RecipientSelectionResultManager

    val recipientSelectionResultDelegate: RecipientSelectionResultDelegate

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance dependency: RecipientSelectionDependency,
            commonComponent: CommonSingletonComponent
        ): RecipientSelectionSingletonComponent
    }
}