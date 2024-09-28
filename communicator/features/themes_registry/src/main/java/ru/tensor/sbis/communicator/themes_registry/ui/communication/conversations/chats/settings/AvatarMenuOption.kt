package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Enum опций меню на лонгклик по автару в настройках чата.
 *
 * @param iconRes иконка.
 * @param textRes ресурс текста.
 * @param destructive выделение красным цветом.
 *
 * @author dv.baranov
 */
internal enum class AvatarMenuOption(
    val iconRes: SbisMobileIcon.Icon,
    @StringRes val textRes: Int,
    val destructive: Boolean = false
) {

    /**
     * Заменить аватар.
     */
    REPLACE(SbisMobileIcon.Icon.smi_arrowsCW, R.string.communicator_channel_replace_label),

    /**
     * Удалить аватар.
     */
    DELETE(SbisMobileIcon.Icon.smi_delete, R.string.communicator_channel_remove_label, true),
}

/** @SelfDocumented */
internal fun getAllOptions() = listOf(AvatarMenuOption.REPLACE, AvatarMenuOption.DELETE)