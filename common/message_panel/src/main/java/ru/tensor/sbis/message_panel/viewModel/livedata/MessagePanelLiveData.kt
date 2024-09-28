package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.Observable
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.message_panel.viewModel.livedata.attachments.MessagePanelAttachmentsControls
import ru.tensor.sbis.message_panel.viewModel.livedata.attachments.MessagePanelAttachmentsData
import ru.tensor.sbis.message_panel.viewModel.livedata.hint.MessagePanelHint
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEventMediator
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.CustomRecipientSelectionMediator
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MessagePanelRecipientsData
import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MessagePanelRecipientsView

/**
 * TODO: 6/20/2019 https://online.sbis.ru/opendoc.html?guid=eca87b87-9341-4501-a7b3-60be676ef4c8
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelLiveData :
    MessagePanelData,
    MessagePanelDataControls,
    MessagePanelAttachmentsData,
    MessagePanelAttachmentsControls,
    MessagePanelQuoteData,
    MessagePanelRecipientsData,
    MessagePanelRecipientsView,
    MessagePanelAvailableSpaceForContent,
    MessagePanelNotifications,
    MessagePanelEditData,
    MessagePanelHint,
    KeyboardEventMediator,
    CustomRecipientSelectionMediator {

    val messagePanelEnabled: Observable<Boolean>
    val sendControlActivated: Observable<out RxContainer<Boolean>>
    val sendControlEnabled: Observable<Boolean>
    val sendControlInvisible: Observable<Boolean>
    val sendControlClickable: Observable<Boolean>
    val panelMaxHeight: Observable<Int>
    val recorderDecorData: Observable<RecorderDecorData>

    fun ordinalCancelClickListener()
    fun setSendControlInvisible(isInvisible: Boolean)
    fun setSendCoreRestrictions(restricted: Boolean)
    fun setSendControlClickable(isClickable: Boolean)

    fun setSendControlEnabled(isEnabled: Boolean)
    fun setPanelMaxHeight(height: Int)
    fun setIsTextRequired(isRequired: Boolean)
}