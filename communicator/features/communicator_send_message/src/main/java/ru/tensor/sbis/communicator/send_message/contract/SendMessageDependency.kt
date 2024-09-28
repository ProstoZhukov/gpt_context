package ru.tensor.sbis.communicator.send_message.contract

import ru.tensor.sbis.attachments.decl.action.AddAttachmentsUseCase

/**
 * Внешние зависимости модуля отправки сообщений в фоне.
 *
 * @see AddAttachmentsUseCase
 *
 * @author dv.baranov
 */
internal interface SendMessageDependency : AddAttachmentsUseCase
