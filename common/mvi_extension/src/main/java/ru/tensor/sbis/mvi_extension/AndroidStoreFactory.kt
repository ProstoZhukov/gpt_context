package ru.tensor.sbis.mvi_extension

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import ru.tensor.sbis.mvi_extension.internal.AndroidStore

/**
 * Реализация [StoreFactory] для стандартного использования в Activity и Fragment.
 * Оборачивает исходный [StoreFactory] для корректной работы в режимах с жизненным циклом.
 * По умолчанию устанавливается рекомендуемая стратегия буферизации лейблов [LabelBufferStrategy.BeforeInit].
 *
 * @param delegate [StoreFactory], который будет использоваться напрямую
 * @param labelBufferStrategy стратегия буферизации лейблов для исключения их пропуска во View
 *
 * Created by Aleksey Boldinov on 31.05.2023.
 */
class AndroidStoreFactory(
    private val delegate: StoreFactory,
    private val labelBufferStrategy: LabelBufferStrategy = LabelBufferStrategy.BeforeInit
) : StoreFactory {

    companion object {

        @JvmStatic
        fun default(): StoreFactory {
            return AndroidStoreFactory(DefaultStoreFactory())
        }

        @JvmStatic
        fun timeTravel(): StoreFactory {
            return AndroidStoreFactory(TimeTravelStoreFactory())
        }
    }

    override fun <Intent : Any, Action : Any, Message : Any, State : Any, Label : Any> create(
        name: String?,
        autoInit: Boolean,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Message, Label>,
        reducer: Reducer<State, Message>
    ): Store<Intent, State, Label> {
        val delegateStore = delegate.create(
            name = name,
            autoInit = false,
            initialState = initialState,
            bootstrapper = bootstrapper,
            executorFactory = executorFactory,
            reducer = reducer
        )
        return AndroidStore(delegateStore, labelBufferStrategy).apply {
            if (autoInit) {
                init()
            }
        }
    }
}

/**
 * Стратегия буферизации лейблов для исключения их пропуска во View.
 */
sealed class LabelBufferStrategy {

    /**
     * Без буферизации/
     */
    object NoBuffer : LabelBufferStrategy()

    /**
     * Все лейблы, которые испускаются до инициализации [Store],
     * поступят на обработку сразу после [Store.init] в потоке вызова.
     * Необходимо использовать в случаях когда [Bootstrapper] может испускать события синхронно
     * или есть риск потерять события при подписке на [Store.labels], привязанной к ЖЦ экрана.
     */
    object BeforeInit : LabelBufferStrategy()

    /**
     * Буферизация лейблов происходит всегда при отсутствии наблюдателей за [Store.labels].
     *
     * @param capacity размер буфера для хранения пропущенных лейблов.
     * При переполнении буфера старые лейблы будут удаляться.
     */
    class Buffer(val capacity: Int = Int.MAX_VALUE) : LabelBufferStrategy()
}