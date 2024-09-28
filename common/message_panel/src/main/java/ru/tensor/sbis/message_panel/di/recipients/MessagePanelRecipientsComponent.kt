package ru.tensor.sbis.message_panel.di.recipients

import dagger.Subcomponent
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor

/**
 * @author vv.chekurda
 */
@Subcomponent(modules = [MessagePanelRecipientsModule::class])
interface MessagePanelRecipientsComponent {

    val recipientsInteractor: MessagePanelRecipientsInteractor?
}