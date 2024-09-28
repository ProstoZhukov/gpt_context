package ru.tensor.sbis.communicator.base.conversation.data.model

import androidx.core.view.isVisible
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.communicator.base.conversation.utils.ConversationSubtitleExtension
import ru.tensor.sbis.communicator.design.icon
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.persons.ContactVM

/**
 * Модель с данными для отображения шапки в реестре сообщений.
 *
 * @property photoDataList список фотографий для коллажа или аватарки чата.
 * @property participantsData данные об участниках диалога/канала.
 * @property title заголовок для шапки, это может быть как [conversationName], так и перечисление имен.
 * @property conversationName опциональное название темы диалога или канала.
 * @property subtitle подзаголовок.
 * @property showOnlyTitle показывать только заголовок.
 * @property consultationPhoto иконка чата или фото оператора для отображения в шапке.
 * @property editingState состояние отображения поля ввода заголовка.
 * @author vv.chekurda
 */
data class ToolbarData @JvmOverloads constructor(
    val photoDataList: List<PhotoData> = emptyList(),
    val participantsData: ParticipantsData = ParticipantsData(),
    val title: String = EMPTY,
    val conversationName: String? = null,
    val subtitle: String = EMPTY,
    val showOnlyTitle: Boolean = false,
    val consultationPhoto: String? = null,
    val isChat: Boolean = false,
    val editingState: ToolbarTitleEditingState = ToolbarTitleEditingState.DISABLED
) {
    /**
     * Признак диалога/канала 1 на 1.
     */
    val isSingleParticipant: Boolean
        get() = participantsData.participants.size == 1

    /**
     * Признак пустой модели.
     */
    val isEmpty: Boolean
        get() = photoDataList.isEmpty() &&
            consultationPhoto.isNullOrEmpty() &&
            title.isEmpty() &&
            conversationName.isNullOrEmpty() &&
            subtitle.isEmpty()
}

/**
 * Состояние редакции темы в шапке.
 */
enum class ToolbarTitleEditingState {
    /**
     * Редакция недоступна.
     */
    DISABLED,
    /**
     * Редакция доступна.
     */
    ENABLED,
    /**
     * Редакция подтверждена, но не завершена.
     */
    COMPLETED
}

/**
 * Установить данные для отображения шапки в [SbisTopNavigationView].
 */
fun SbisTopNavigationView.setToolbarData(data: ToolbarData, prevData: ToolbarData? = null) {
    val needDrawIcon = !data.consultationPhoto.isNullOrEmpty() && !data.consultationPhoto.contains(URL_PREFIX)
    if (!needDrawIcon) {
        personView?.also {
            if (prevData?.photoDataList != data.photoDataList) {
                it.setDataList(data.photoDataList)
            }
            it.isVisible = true
        }
        leftIconView?.isVisible = false
    } else {
        personView?.isVisible = false
        leftIconView?.isVisible = true
        leftIconView?.text = data.consultationPhoto?.icon?.character?.toString()
    }

    if (data.editingState == ToolbarTitleEditingState.DISABLED) {
        if (titleView?.value != data.title) {
            titleView?.value = data.title
        }
        subtitleView?.isVisible = !data.showOnlyTitle
    }

    subtitleView?.getExtension<ConversationSubtitleExtension>()
        ?.apply { text = data.subtitle }
        ?: apply { subtitleView?.text = data.subtitle }
}

/**
 * Модель с данными об участниках диалога/канала.
 *
 * @property participants список моделей участников диалога/канала.
 * @property names список имен участников.
 * @property hiddenParticipantCount количество скрытых участников для +N в заголовке.
 */
data class ParticipantsData @JvmOverloads constructor(
    val participants: List<ContactVM> = emptyList(),
    val names: List<String> = emptyList(),
    val hiddenParticipantCount: Int = 0
) {
    val hasData: Boolean
        get() = participants.isNotEmpty()
}

private const val URL_PREFIX = "https://"