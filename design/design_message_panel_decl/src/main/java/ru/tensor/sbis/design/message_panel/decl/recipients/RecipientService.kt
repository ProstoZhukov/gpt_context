package ru.tensor.sbis.design.message_panel.decl.recipients

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.design.message_panel.decl.MessagePanelUseCase
import ru.tensor.sbis.persons.IContactVM

/**
 * Интерфейс взаимодействия с сервисом для выбора получателей
 *
 * @see RecipientServiceHelper
 *
 * @author ma.kolpakov
 */
interface RecipientService<out RECIPIENT : IContactVM> {

    val recipients: Flow<List<RECIPIENT>>

    suspend fun launchSelection(useCase: MessagePanelUseCase, context: Context)
}