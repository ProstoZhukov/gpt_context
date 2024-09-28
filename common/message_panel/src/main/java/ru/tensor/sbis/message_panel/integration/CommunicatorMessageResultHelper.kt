package ru.tensor.sbis.message_panel.integration

import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.model.mapper.ContactVMAndProfileMapper
import ru.tensor.sbis.persons.IPersonModel
import java.util.UUID

/**
 * Реализация [MessageResultHelper] для работы с контейнерами сообщений [MessageResult] и отправки [SendMessageResult]
 * микросервиса сообщений
 *
 * @author vv.chekurda
 */
internal class CommunicatorMessageResultHelper : MessageResultHelper<MessageResult, SendMessageResult> {

    /**
     * Проверка на ошибку результата.
     *
     * На микросервисе сообщений код ошибки [ErrorCode.WARNING] является предупреждением
     * при успешной операции/отправке сообщения. Обработка этого кода в ключе ошибки
     * приводит к сценариям, в которых ui и микросервис некорректно отрабатывают алгоритмы запросов подписи
     * Пример: https://online.sbis.ru/opendoc.html?guid=f5811ea6-4264-4097-bb60-19a8db77ca60
     */
    private val ErrorCode.isResultError: Boolean
        get() = !(this == ErrorCode.SUCCESS || this == ErrorCode.WARNING)

    override fun isResultError(message: MessageResult): Boolean =
        message.status.errorCode.isResultError

    override fun isSentResultError(message: SendMessageResult): Boolean =
        message.status.errorCode.isResultError

    override fun getSentMessageUuid(message: SendMessageResult): UUID? =
        message.messageUuid

    override fun getResultError(message: MessageResult): String =
        message.status.errorMessage

    override fun getSentResultError(message: SendMessageResult): String =
        message.status.errorMessage

    override fun getSender(message: MessageResult): IPersonModel =
        message.data!!.sender.run(ContactVMAndProfileMapper::modelFromProfile)
}