package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.contract.MessageInformationFeature
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.contract.MessageInformationFragmentFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.contract.MessageInformationIntentFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation.MessageInformationActivity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.presentation.MessageInformationFragment

/**
 * Реализация фичи экрана информации о сообщении [MessageInformationFeature]
 * @see [MessageInformationFragment]
 * @see [MessageInformationActivity]
 *
 * @author vv.chekurda
 */
internal object MessageInformationFeatureFacade :
    MessageInformationFeature,
    MessageInformationFragmentFactory by MessageInformationFragment.Companion,
    MessageInformationIntentFactory by MessageInformationActivity.Companion