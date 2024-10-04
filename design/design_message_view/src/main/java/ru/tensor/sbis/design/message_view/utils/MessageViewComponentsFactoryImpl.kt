package ru.tensor.sbis.design.message_view.utils

import ru.tensor.sbis.design.message_view.contact.MessageViewComponentsFactory
import ru.tensor.sbis.design.message_view.contact.MessageViewDataMapperFactory
import ru.tensor.sbis.design.message_view.contact.MessageViewPoolFactory
import ru.tensor.sbis.design.message_view.mapper.MessageViewDataMapper

/**
 * Реализация фабрики [MessageViewComponentsFactory].
 *
 * @author vv.chekurda
 */
internal object MessageViewComponentsFactoryImpl :
    MessageViewComponentsFactory,
    MessageViewPoolFactory by MessageViewPool.Companion,
    MessageViewDataMapperFactory by MessageViewDataMapper.Companion