package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.asNative
import ru.tensor.sbis.communicator.generated.ThemeParticipantListItem as ControllerThemeParticipantListItem

/**
 * Маппер, преобразующий модель контроллера [ControllerThemeParticipantListItem] в UI модель [ThemeParticipantListItem].
 *
 * @author dv.baranov
 */
internal class ThemeParticipantListItemMapper(context: Context) :
    BaseModelMapper<ControllerThemeParticipantListItem, ThemeParticipantListItem>(context) {

    override fun apply(rawData: ControllerThemeParticipantListItem): ThemeParticipantListItem = rawData.asNative
}
