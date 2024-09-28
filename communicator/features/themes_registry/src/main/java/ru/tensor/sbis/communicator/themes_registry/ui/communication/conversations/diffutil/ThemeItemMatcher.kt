package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.diffutil

import ru.tensor.sbis.base_components.autoscroll.BaseAutoScroller
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.data.theme.FoldersConversationRegistryItem
import ru.tensor.sbis.communicator.common.data.theme.StubConversationRegistryItem
import ru.tensor.sbis.persons.ContactVM
/**
 * Реализация сравнения элементов списка реестра диалогов.
 *
 * @author rv.krohalev
 */
internal class ThemeItemMatcher : BaseAutoScroller.Matcher {

    override fun areItemsTheSame(item1: Any?, item2: Any?): Boolean {
        return (item1 is ContactVM && item2 is ContactVM && item1.uuid == item2.uuid)
                || (item1 is ConversationModel && item2 is ConversationModel && item1.uuid == item2.uuid)
                || (item1 is FoldersConversationRegistryItem && item2 is FoldersConversationRegistryItem)
                || (item1 is StubConversationRegistryItem && item2 is StubConversationRegistryItem)
    }
}