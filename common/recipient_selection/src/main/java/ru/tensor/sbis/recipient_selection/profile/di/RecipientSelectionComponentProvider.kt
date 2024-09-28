package ru.tensor.sbis.recipient_selection.profile.di

import android.content.Context
import ru.tensor.sbis.recipient_selection.profile.RecipientSelectionPlugin
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.di.profile_component.DaggerRecipientSelectionComponent
import ru.tensor.sbis.recipient_selection.profile.di.profile_component.RecipientSelectionComponent

/**
 * Предоставляет доступ к [RecipientSelectionComponent] и [RecipientSelectionSingletonComponent]
 **/
internal object RecipientSelectionComponentProvider {

    fun getRecipientSelectionComponent(context: Context, filter: RecipientsSearchFilter): RecipientSelectionComponent =
        DaggerRecipientSelectionComponent.builder()
            .recipientSelectionSingletonComponent(getRecipientSelectionSingletonComponent(context))
            .recipientsFilter(filter)
            .build()

    @JvmStatic
    fun getRecipientSelectionSingletonComponent(context: Context): RecipientSelectionSingletonComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return RecipientSelectionPlugin.singletonComponent
    }
}
