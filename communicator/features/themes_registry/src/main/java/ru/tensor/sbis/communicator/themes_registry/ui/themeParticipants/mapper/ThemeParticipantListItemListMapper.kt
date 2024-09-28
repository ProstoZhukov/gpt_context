package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantListItemMapOfStringString
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.asNative
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Маппер, преобразующий модель контроллера [ListResultOfThemeParticipantListItemMapOfStringString] в UI модель [ThemeParticipantListItem].
 *
 * @author dv.baranov
 */
internal class ThemeParticipantListItemListMapper(context: Context) :
    BaseModelMapper<ListResultOfThemeParticipantListItemMapOfStringString, PagedListResult<ThemeParticipantListItem>>(context) {

    override fun apply(rawList: ListResultOfThemeParticipantListItemMapOfStringString): PagedListResult<ThemeParticipantListItem> =
        PagedListResult(
            rawList.result.map { it.asNative },
            rawList.haveMore,
        )
}
