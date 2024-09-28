package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.list

import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen.ReadStatusScreenEntity
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.presentation.ListScreenVM

/**
 * Интерфейс вью-модели секции списка статусов прочитанности сообщения
 *
 * @author vv.chekurda
 */
internal interface ReadStatusListScreenVM :
    ListScreenVM,
    View<ReadStatusScreenEntity>