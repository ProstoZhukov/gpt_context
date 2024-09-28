package ru.tensor.sbis.recipient_selection.profile.di.profile_component

import android.content.Context
import ru.tensor.sbis.recipient_selection.profile.data.RecipientsSearchFilter
import ru.tensor.sbis.recipient_selection.profile.ui.getRecipientSelectionComponent
import java.io.Serializable

/**
 * Делегат для получения di компонента выбора получателей
 *
 * @author vv.chekurda
 */
internal interface RecipientSelectionComponentDelegate {

    fun getComponent(context: Context): RecipientSelectionComponent
}

/**
 * Вспомогательный класс для делегирования создания и храненения di компонента выбора получателей
 *
 * @property filter поисковый фильтр с настройками для работы выбора получателей
 */
internal class RecipientSelectionComponentHelper(
    private val filter: RecipientsSearchFilter
) : RecipientSelectionComponentDelegate, Serializable {

    @Transient
    @Volatile
    private var component: RecipientSelectionComponent? = null

    override fun getComponent(context: Context): RecipientSelectionComponent =
        component ?: context.getRecipientSelectionComponent(filter)
            .also { component = it }
}