package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListMapper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper.item.ReadStatusItemFactory
import ru.tensor.sbis.list.base.domain.entity.Mapper
import ru.tensor.sbis.list.view.section.ForceBackgroundColor
import ru.tensor.sbis.list.view.section.Options
import ru.tensor.sbis.list.view.section.Section
import ru.tensor.sbis.list.view.section.SectionsHolder
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Sections
import javax.inject.Inject

/**
 * Маппер моделей микросервиса списка статусов прочитанности сообщения в модели компонента
 * @see [Mapper]
 *
 * @property itemFactory фабрика элементов списка
 *
 * @author vv.chekurda
 */
internal class ReadStatusListMapperImpl @Inject constructor(
    private val itemFactory: ReadStatusItemFactory
) : ReadStatusListMapper {

    override fun map(from: List<ReadStatusListResult>): ListData =
        Sections(
            info = SectionsHolder(
                sections = listOf(mapSection(from)),
                forcedBackgroundColor = ForceBackgroundColor.DARK
            )
        )

    private fun mapSection(from: List<ReadStatusListResult>): Section =
        Section(
            items = from.map(itemFactory::createItemList).flatten(),
            options = Options(
                hasDividers = false,
                hasTopMargin = false
            )
        )
}