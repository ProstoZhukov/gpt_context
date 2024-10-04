package ru.tensor.sbis.design.message_panel.vm.recipients

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientService
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientServiceHelper
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView.RecipientsViewData
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.design.message_panel.vm.usecase.UseCaseDelegate
import ru.tensor.sbis.persons.IContactVM
import java.util.*
import javax.inject.Inject

/**
 * @author ma.kolpakov
 */
internal class RecipientsDelegateImpl @Inject constructor(
    private val service: RecipientService<IContactVM>,
    private val serviceHelper: RecipientServiceHelper<IContactVM>,
    private val useCaseApi: UseCaseDelegate
) : RecipientsDelegate {

    private lateinit var viewModelScope: CoroutineScope
    override val recipients = MutableStateFlow(RecipientsViewData())
    override lateinit var recipientsUuid: StateFlow<List<UUID>>
    override val recipientsHint = MutableStateFlow(EMPTY)
    override val recipientsAllChosenText = MutableStateFlow(EMPTY)
    override val recipientsVisible = MutableStateFlow(true)

    override fun attachRecipientsScope(scope: CoroutineScope) {
        viewModelScope = scope
        recipientsUuid = recipients.map { data ->
            data.recipients
                .filterIsInstance(RecipientPersonItem::class.java)
                .map { it.uuid }
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        viewModelScope.launch {
            service.recipients.collect { contacts ->
                recipients.emit(
                    recipients.value.copy(
                        recipients = contacts.map(::RecipientPersonItem)
                    )
                )
            }
        }
    }

    override fun onRecipientSelectionClicked(context: Context) {
        viewModelScope.launch {
            service.launchSelection(useCaseApi.useCase.value, context)
        }
    }

    override fun setRecipients(newRecipients: List<UUID>, isSelectedByUser: Boolean) {
        //TODO("Not yet implemented")
    }

    override fun onRecipientsClearClicked() =
        clearRecipients()

    override fun clearRecipients() {
        recipients.value = recipients.value.copy(recipients = emptyList())
    }

    override fun changeRecipientsVisibility(isVisible: Boolean) {
        recipientsVisible.value = isVisible
    }

    override fun setRecipientsHint(hint: String) {
        recipientsHint.value = hint
    }

    override fun setRecipientsAllChosenText(text: String) {
        recipientsAllChosenText.value = text
    }
}
