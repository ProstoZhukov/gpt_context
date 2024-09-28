package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.mapper

import android.content.Context
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.communicator.generated.ListResultOfThemeParticipantMapOfStringString
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.asNative
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Маппер, преобразующий модель контроллера [ListResultOfThemeParticipant] в UI модель [ThemeParticipant].
 */
@Suppress("KDocUnresolvedReference")
internal class ThemeParticipantsListMapper(context: Context) :
    BaseModelMapper<ListResultOfThemeParticipantMapOfStringString, PagedListResult<ThemeParticipant>>(context) {

    override fun apply(rawList: ListResultOfThemeParticipantMapOfStringString): PagedListResult<ThemeParticipant> =
        PagedListResult(
            rawList.result.map { it.asNative },
            rawList.haveMore,
        )
}
