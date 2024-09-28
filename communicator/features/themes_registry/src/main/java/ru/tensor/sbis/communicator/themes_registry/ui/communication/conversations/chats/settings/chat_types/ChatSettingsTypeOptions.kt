package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.ChannelType
import ru.tensor.sbis.communicator.generated.ParticipationType

/**
 * Enum опции настроек типа канала.
 *
 * @param titleRes - заголовок.
 * @param discoverabilityTitleRes - подробный текст.
 *
 * @author dv.baranov
 */
internal enum class ChatSettingsTypeOptions(
    @StringRes
    val titleRes: Int,
    @StringRes
    val discoverabilityTitleRes: Int,
) {

    /**
     * Открытый чат.
     */
    OPEN(R.string.communicator_chat_type_open, R.string.communicator_chat_type_open_subtitle),

    /**
     * Приватный чат.
     */
    PRIVATE(R.string.communicator_chat_type_private, R.string.communicator_chat_type_private_subtitle),
}

/**
 * Enum опции настроек типа участия канала.
 *
 * @param titleRes - заголовок.
 *
 * @author dv.baranov
 */
internal enum class ChatSettingsParticipationTypeOptions(
    @StringRes
    val titleRes: Int,
) {

    /**
     * Для всех желающих.
     */
    FOR_ALL(R.string.communicator_chat_participant_type_all),

    /**
     * Только для сотрудников.
     */
    ONLY_EMPLOYEES(R.string.communicator_chat_participant_type_only_employees),
}

/** @SelfDocumented */
internal fun ChannelType.toChatSettingsTypeOptions(): ChatSettingsTypeOptions = when (this) {
    ChannelType.OPEN -> ChatSettingsTypeOptions.OPEN
    ChannelType.PRIVATE -> ChatSettingsTypeOptions.PRIVATE
}

/** @SelfDocumented */
internal fun ParticipationType.toChatSettingsParticipantTypeOptions(): ChatSettingsParticipationTypeOptions = when (this) {
    ParticipationType.FOR_ALL -> ChatSettingsParticipationTypeOptions.FOR_ALL
    ParticipationType.ONLY_EMPLOYEES -> ChatSettingsParticipationTypeOptions.ONLY_EMPLOYEES
}

/** @SelfDocumented */
internal fun ChatSettingsTypeOptions.toChannelType(): ChannelType = when (this) {
    ChatSettingsTypeOptions.OPEN -> ChannelType.OPEN
    ChatSettingsTypeOptions.PRIVATE -> ChannelType.PRIVATE
}

/** @SelfDocumented */
internal fun ChatSettingsParticipationTypeOptions.toParticipationType(): ParticipationType = when (this) {
    ChatSettingsParticipationTypeOptions.FOR_ALL -> ParticipationType.FOR_ALL
    ChatSettingsParticipationTypeOptions.ONLY_EMPLOYEES -> ParticipationType.ONLY_EMPLOYEES
}
