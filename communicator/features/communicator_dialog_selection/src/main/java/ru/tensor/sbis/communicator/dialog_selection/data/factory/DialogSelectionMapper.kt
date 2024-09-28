package ru.tensor.sbis.communicator.dialog_selection.data.factory

import android.text.SpannableString
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.map
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.dialog_selection.data.DialogSelectionServiceResult
import ru.tensor.sbis.communicator.dialog_selection.data.mapper.asNative
import ru.tensor.sbis.communicator.dialog_selection.data.mapper.isEmpty
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultDepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultPersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.DefaultTitleItemModel
import ru.tensor.sbis.design.selection.ui.model.share.TitleItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DefaultDialogSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DialogSelectorItemModel
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.communicator.generated.RecipientFolder
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.profiles.generated.EmployeeProfile
import javax.inject.Inject
import kotlin.collections.ArrayList
import ru.tensor.sbis.communicator.design.R as RCommunicator

/**
 * Реализация маппера моделей результатов сервисов получателей и диалогов в модели компонента селектора
 * @property resourceProvider провайдер ресурсов
 * @property attachmentMapper маппер для вложений
 *
 * @author vv.chekurda
 */
internal class DialogSelectionMapper @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val attachmentMapper: AttachmentRegisterModelMapper
) : ListMapper<DialogSelectionServiceResult, SelectorItemModel> {

    /**
     * Заголовок для секции выбора получателей
     */
    private val recentContactsTitleItemModel: TitleItemModel by lazy {
        DefaultTitleItemModel(
            id = "000",
            resourceProvider.getString(RCommunicator.string.communicator_dialog_selection_recent_contacts)
        )
    }

    /**
     * Заголовок для секции выбора диалога
     */
    private val recentDialogsTitleItemModel: TitleItemModel by lazy {
        DefaultTitleItemModel(
            id = "001",
            resourceProvider.getString(RCommunicator.string.communicator_dialog_selection_recent_dialogs)
        )
    }

    override fun invoke(result: DialogSelectionServiceResult): List<SelectorItemModel> = result.run {
        ArrayList<SelectorItemModel>()
            .doIf(recipientsResult.isEmpty.not()) {
                add(recentContactsTitleItemModel)
            }
            .plus(recipientsResult.profiles.map { it.mapToPersonSelectorItemModel() })
            .plus(recipientsResult.folders.map { it.mapToDepartmentSelectorItemModel() })
            .doIf(dialogsResult.isNotEmpty()) {
                asArrayList().add(recentDialogsTitleItemModel)
            }
            .plus(dialogsResult.map { it.mapToDialogSelectorItemModel() })
    }

    private fun EmployeeProfile.mapToPersonSelectorItemModel(): PersonSelectorItemModel {
        val initials = person.photoDecoration?.run { InitialsStubData(initials, backgroundColorHex) }
        return DefaultPersonSelectorItemModel(
            id = person.uuid.toString(),
            title = "${person.name.last} ${person.name.first}",
            subtitle = companyOrDepartment,
            personData = PersonData(person.uuid, person.photoUrl, initials),
            personName = PersonName(person.name.first, person.name.last, person.name.patronymic)
        )
    }

    private fun RecipientFolder.mapToDepartmentSelectorItemModel(): DepartmentSelectorItemModel =
        DefaultDepartmentSelectorItemModel(
            id = uuid.toString(),
            title = name,
            subtitle = "%s (%s)".format(chief ?: StringUtils.EMPTY, count).trim(),
            membersCount = count,
        )

    private fun ConversationModel.mapToDialogSelectorItemModel(): DialogSelectorItemModel =
        DefaultDialogSelectorItemModel(
            title = messagePersonName!!,
            subtitle = StringUtils.EMPTY,
            dialogTitle = dialogTitle,
            id = uuid.toString(),
            timestamp = timestamp,
            syncStatus = syncStatus.asNative,
            participantsCollage = participantsCollage,
            participantsCount = participantsCount,
            messageUuid = messageUuid,
            messageType = messageType.asNative,
            messagePersonCompany = messagePersonCompany,
            messageText = messageText ?: SpannableString(StringUtils.EMPTY),
            isOutgoing = isOutgoing,
            isRead = isRead,
            isReadByMe = isReadByMe,
            isForMe = isForMe,
            serviceText = serviceText,
            unreadCount = unreadCount,
            documentUuid = documentUuid,
            documentType = documentType?.asNative,
            externalEntityTitle = externalEntityTitle,
            attachments = attachments?.map { attachmentMapper.map(it.fileInfoViewModel) }?.asArrayList(),
            attachmentCount = attachmentCount,
            isChatForOperations = isChatForOperations,
            isPrivateChat = isPrivateChat,
            isSocnetEvent = isSocnetEvent,
            searchHighlights = searchHighlights,
            nameHighlights = nameHighlights,
            docsHighlights = docsHighlights,
            dialogNameHighlights = dialogNameHighlights
        )
}