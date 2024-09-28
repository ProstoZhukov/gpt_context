package ru.tensor.sbis.communicator.communicator_files.mapper

import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileData
import ru.tensor.sbis.communicator.communicator_files.ui.ConversationFileOriginDecoration
import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.ItemOptions
import ru.tensor.sbis.list.view.item.Options

/**
 * Класс `CommunicatorFilesItem` представляет собой элемент данных для использования в RecyclerView,
 * предназначенный для отображения списка вложений.
 *
 * @property data Модель данных, представляющая список вложений, который будет отображаться.
 * @property viewHolderHelper Вспомогательный класс для управления ViewHolder элементами.
 * @property options Опции для настройки отображения элемента, по умолчанию включающие пользовательские отступы и фон.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesItem(
    data: CommunicatorFileData,
    viewHolderHelper: CommunicatorFilesHolderHelper,
    options: ItemOptions = Options(customSidePadding = true, customBackground = true),
    itemDecoration: ConversationFileOriginDecoration
) : Item<CommunicatorFileData, CommunicatorFilesHolder>(
    data,
    viewHolderHelper,
    options = options,
    itemDecoration = itemDecoration
)