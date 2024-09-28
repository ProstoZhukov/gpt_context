package ru.tensor.sbis.messages_events_wrapper

import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Общий код для прослушивания ошибок отправки сообщения со стороны контроллера.
 * Используется в модулях, не связанных явно с модулем коммуникатор.
 */
interface MessagesEventsWrapper : Feature {

    /**
     * Ожидаемый идентификатор сообщения.
     * Нужно задавать после отправки сообщения чтобы получать события.
     */
    var expectedMessageUuid: UUID?

    /**
     * Излучает новый элемент если возникла ошибка "непривязанный номер телефона"
     */
    val unattachedPhoneNumberError: Observable<Unit>

    /**
     * Начинает прослушку событий.
     */
    fun start()

    /**
     * Останавливает прослушку событий.
     */
    fun stop()

    /**
     * Реализация [MessagesEventsWrapper], которая ничего не делает (заглушка).
     * Может использоваться в тех приложениях без контроллера коммуникатора.
     */
    object Stub : MessagesEventsWrapper {

        override var expectedMessageUuid: UUID? = null

        override val unattachedPhoneNumberError: Observable<Unit>
            get() = Observable.empty()

        override fun start() = Unit

        override fun stop() = Unit
    }
}