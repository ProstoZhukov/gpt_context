package ru.tensor.sbis.communicator.common.util.result_mediator

import java.util.UUID

/**
 * Класс для передачи UUID результатов между фрагментами через FragmentManager.
 *
 * Этот класс является конкретной реализацией `FragmentResultMediator`, которая
 * использует UUID в качестве типа данных результата.
 */
class MessageUuidMediator : FragmentResultMediator<UUID>() {

    /**
     * Ключ для идентификации результата в FragmentManager.
     */
    override val key: String
        get() = "CommunicatorMessageUuidKey"
}