package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import ru.tensor.sbis.communicator.common.data.theme.StubConversationRegistryItem
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Модель заглушки для реестра диалогов
 */
internal data class DialogsStubModel(val content: StubViewContent? = null) : StubConversationRegistryItem()
