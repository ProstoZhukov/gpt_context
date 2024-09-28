package ru.tensor.sbis.design.message_panel.vm

import ru.tensor.sbis.design.message_panel.vm.attachments.AttachmentsDelegate
import ru.tensor.sbis.design.message_panel.vm.draft.DraftDelegate
import ru.tensor.sbis.design.message_panel.vm.keyboard.KeyboardDelegate
import ru.tensor.sbis.design.message_panel.vm.notification.NotificationDelegate
import ru.tensor.sbis.design.message_panel.vm.quote.QuoteDelegate
import ru.tensor.sbis.design.message_panel.vm.recipients.RecipientsDelegate
import ru.tensor.sbis.design.message_panel.vm.state.StateDelegate

/**
 * Внутренний API панели ввода
 *
 * @author ma.kolpakov
 */
internal interface MessagePanelViewModel :
    StateDelegate,
    MessagePanelApi,
    QuoteDelegate,
    DraftDelegate,
    AttachmentsDelegate,
    RecipientsDelegate,
    NotificationDelegate,
    KeyboardDelegate
