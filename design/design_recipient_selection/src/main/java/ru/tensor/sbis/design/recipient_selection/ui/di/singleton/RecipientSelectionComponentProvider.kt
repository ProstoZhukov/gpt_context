package ru.tensor.sbis.design.recipient_selection.ui.di.singleton

import android.content.Context
import ru.tensor.sbis.design.recipient_selection.RecipientSelectionPlugin
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.design.recipient_selection.ui.di.screen.DaggerRecipientSelectionComponent
import ru.tensor.sbis.design.recipient_selection.ui.di.screen.RecipientSelectionComponent

/**
 * Поставщик di-компонента выбора получателей.
 *
 * @author vv.chekurda
 */
internal object RecipientSelectionComponentProvider {

    fun getRecipientSelectionComponent(
        context: Context,
        config: RecipientSelectionConfig
    ): RecipientSelectionComponent =
        DaggerRecipientSelectionComponent.factory()
            .create(
                getRecipientSelectionSingletonComponent(context),
                config
            )

    @JvmStatic
    fun getRecipientSelectionSingletonComponent(context: Context): RecipientSelectionSingletonComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return RecipientSelectionPlugin.singletonComponent
    }
}
