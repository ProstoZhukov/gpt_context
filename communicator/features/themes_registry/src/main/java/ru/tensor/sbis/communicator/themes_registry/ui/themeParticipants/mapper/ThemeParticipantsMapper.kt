package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.asNative
import ru.tensor.sbis.communicator.generated.ThemeParticipant as ControllerThemeParticipant

/**
 * Маппер, преобразующий модель контроллера [ControllerThemeParticipant] в UI модель [ThemeParticipant].
 */
internal class ThemeParticipantsMapper(context: Context) :
    BaseModelMapper<ControllerThemeParticipant, ThemeParticipant>(context) {

    override fun apply(rawData: ControllerThemeParticipant): ThemeParticipant = rawData.asNative
}
