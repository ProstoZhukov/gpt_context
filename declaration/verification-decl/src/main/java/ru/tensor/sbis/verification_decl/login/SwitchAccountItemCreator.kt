package ru.tensor.sbis.verification_decl.login

import android.content.Context
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.settings_screen_decl.Item
import ru.tensor.sbis.settings_screen_decl.type.NameFormattingType
import ru.tensor.sbis.settings_screen_decl.type.SubtitleType

/**
 * Реализация интерфейса создает элемент меню настроек для отображения и смены текущего акаунта пользователя.
 */
interface SwitchAccountItemCreator : Feature {

    /**
     * Пункт для отображения текущего профиля с возможностью его смены при нажатии.
     */
    fun createSwitchAccountItem(
        context: Context,
        canSwitchAccount: Boolean = true,
        subtitleType: SubtitleType = SubtitleType.DEFAULT,
        nameFormattingType: NameFormattingType = NameFormattingType.SURNAME_NAME
    ): Item
}