package ru.tensor.sbis.design.message_view.contact

import ru.tensor.sbis.design.message_view.ui.MessageView
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания вспомогательных компонентов для [MessageView].
 * @see MessageViewPoolFactory
 * @see MessageViewDataMapperFactory
 *
 * @author vv.chekurda
 */
interface MessageViewComponentsFactory :
    Feature,
    MessageViewPoolFactory,
    MessageViewDataMapperFactory