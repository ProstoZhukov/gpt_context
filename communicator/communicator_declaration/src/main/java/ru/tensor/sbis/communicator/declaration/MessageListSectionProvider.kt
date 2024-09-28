package ru.tensor.sbis.communicator.declaration

import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSectionHolder
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.mvp.presenter.DisplayErrorDelegate
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Поставщик секции списка сообщений.
 *
 * @author vv.chekurda
 */
interface MessageListSectionProvider : Feature {

    /**
     * Создает секцию списка сообщений по UUID диалога.
     * @param fragment             фрагмент, куда необходимо добавить секцию.
     * @param dialogUuid           UUID диалога.
     * @param sectionHolder        компонент, который будет содержать в себе секцию списка сообщений.
     * @param displayErrorDelegate делегат для обработки ошибок.
     * @param onEditMessage        callback для события редактирования сообщения, передающий UUID сообщения.
     * @param onReplayMessage      callback для события цитирования сообщения, передающий UUID диалога, UUID сообщения, counterUuid: String, showKeyboard: Boolean.
     * @param containerId          id корневого контейнера для роутинга.
     * @return [ListSection]       message list section.
     */
    fun getMessageListSection(
        fragment: Fragment,
        dialogUuid: UUID,
        sectionHolder: ListSectionHolder,
        displayErrorDelegate: DisplayErrorDelegate,
        onEditMessage: ((UUID) -> Unit)?,
        onReplayMessage: ((UUID, UUID, UUID, Boolean) -> Unit)?,
        containerId: Int = 0,
        listDateViewUpdaterInitializer: (ListDateViewUpdater) -> Unit
    ): ListSection<in ListItem, MessageListController, *>

}
