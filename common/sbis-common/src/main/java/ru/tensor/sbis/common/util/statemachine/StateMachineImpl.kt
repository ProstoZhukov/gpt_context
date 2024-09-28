package ru.tensor.sbis.common.util.statemachine

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.rx.scheduler.TensorSchedulers
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * @author Dmitry Subbotenko
 */

/**
 * Интерфейс создания событий для машины состояний. Может быть простым или дата классом, содержать любую логику или хранить данные.
 * Жизненный цикл события короткий, событие не рекомендуется сохранять где либо. При каждом вызове рекомендую создавать новый инстанс объекта.
 */
interface SessionEvent

/**
 * Интерфейс событий установки общих стейтов для машины состояний. Может быть простым или дата классом, содержать любую логику или хранить данные.
 * Жизненный цикл события короткий, событие не рекомендуется сохранять где либо. При каждом вызове рекомендую создавать новый инстанс объекта.
 * @author Subbotenko Dmitry
 */
interface SessionStateEvent

/**
 * Базовый интерфейс машины состояний
 * @author Subbotenko Dmitry
 */
interface StateMachine {
    /**
     * Асинхронный вызов события. Событие всегда летит в текущее состояние.
     */
    fun fire(event: SessionEvent)

    /**
     * Остановка и очистка памяти стейт машины. Объект больше не может быть использован.
     */
    fun stop()
}

/**
 * Интерфейс реализации машины состояний для Котлин делегации.
 * @author Subbotenko Dmitry
 */
interface StateMachineInner : StateMachine {
    /**
     * Включение подробного логирования переходов по состояниям с соответствующим тегом.
     * Только для отладки, не добрасывать в основной код
     */
    @Deprecated("Использовать только для отладки, удалять перед мерджем!", ReplaceWith(" // отладочный лог удален"))
    fun enableLogging(tag: String, logger: (String) -> Unit = { Timber.d("%s %s", tag, it) })

    /**
     * Установка состояния
     */
    fun setState(state: SessionState)

    /**
     * Установка состояния, отложенного на определенное время.
     * Удобно для тайм-аутов. Любая установка стейта прервет ожидание и отменит это состояние.
     */
    fun setState(state: SessionState, time: Long, timeUnit: TimeUnit)

    /**
     * Немедленно выполнить все отложенные состояния.
     */
    fun executePendingNow()

    /**
     * Обзервер, потока состояний.
     */
    val currentStateObservable: Flowable<SessionState>

    /**
     * Получить текущее состояние.
     */
    fun currentState(): SessionState?

    /**
     * Установка реакции на событие, общего для всей машины состояний. Используется в основном для изменения стейтов.
     * Выполняется на потоке выбранном в конструкторе стейт машины.
     * одно и тоже событие невозможно обрабатывать дважды. Последующие обработчики затрут предыдущие.
     */
    fun <SESSION_STATE_EVENT : SessionStateEvent> state(
        clazz: KClass<SESSION_STATE_EVENT>,
        consumer: (t: SESSION_STATE_EVENT) -> Unit
    ): ((t: Any) -> Unit)?

    /**
     * Функция вызова события переключающего текущее состояние
     * Работает синхронно в текущем потоке
     */
    fun <SESSION_STATE_EVENT : SessionStateEvent> fire(event: SESSION_STATE_EVENT)
}

/**
 * Класс-шаблон для описания состояний.
 * @author Subbotenko Dmitry
 */
abstract class SessionState {
    @Volatile
    internal var stateMachine: StateMachineInner? = null
    protected val disposer = CompositeDisposable()
    private val map = ConcurrentHashMap<KClass<out SessionEvent>, (t: Any) -> Unit>()
    private val onSetListeners = CopyOnWriteArrayList<() -> Unit>()
    internal fun <SESSION_EVENT : SessionEvent> invokeEvent(event: SESSION_EVENT) =
        (map[event::class] as? (t: SESSION_EVENT) -> Unit)?.invoke(event)

    /**
     * Установка шаблона вызываемый при установке состояния. Можно устанавливать много раз (например при наследовании).
     * Выполняется на потоке выбранном в конструкторе стейт машины.
     */
    fun addOnSetAction(set: () -> Unit) = onSetListeners.add(set)

    internal fun onAfterSet() {
        onSetListeners.forEach { it.invoke() }
    }

    /**
     * Установка реакции на событие
     * Выполняется на потоке выбранном в конструкторе стейт машины.
     * одно и тоже событие невозможно обрабатывать дважды. Последующие обработчики затрут предыдущие.
     */
    @Suppress("UNCHECKED_CAST")
    fun <SESSION_EVENT : SessionEvent> event(clazz: KClass<SESSION_EVENT>, consumer: (t: SESSION_EVENT) -> Unit) =
        map.put(clazz, consumer as (t: Any) -> Unit)

    /**
     * Ожидание события с тайм-аутом. Если событие приходит в течении установленного времени, обрабатывается как обычно, таймер останавливается
     * В противном случае вызывается  onTimeout.
     * одно и тоже событие невозможно обрабатывать дважды. Последующие обработчики затрут предыдущие (включая установленные методом event).
     */
    fun <SESSION_EVENT : SessionEvent> timeout(
        time: Long,
        timeUnit: TimeUnit,
        event: KClass<SESSION_EVENT>,
        onEvent: ((t: SESSION_EVENT) -> Unit)? = null,
        onTimeout: (() -> Unit)? = null
    ) {
        val timer = BehaviorSubject.createDefault(time)
        event(event) {
            timer.onNext(time)
            onEvent?.invoke(it)
        }

        disposer += timer
            .switchMap { Observable.timer(it, timeUnit) }
            .doOnNext { onTimeout?.invoke() }
            .subscribe()
    }

    open fun stop() {
        disposer.dispose()
        stateMachine = null
//        AppWatcher.objectWatcher.watch(this) todo вернуть после https://online.sbis.ru/opendoc.html?guid=ec29b0fe-9966-4836-bf2c-1fb0bed48d9c
    }

    /**
     * Асинхронный вызов события. Событие всегда летит в текущее состояние.
     */
    fun fire(event: SessionEvent) = stateMachine?.fire(event)
        ?: throw RuntimeException("This state is not active in state machine")

    /**
     * Функция вызова события переключающего текущее состояние
     * Работает синхронно в текущем потоке
     */
    fun <SESSION_STATE_EVENT : SessionStateEvent> fire(event: SESSION_STATE_EVENT) = stateMachine?.fire(event)
}

/**
 * Реализация машины состояний.
 * @author Subbotenko Dmitry
 * @param statesScheduler поток на котором выполняется установка стейтов. По умолчанию mainThread()
 * @param eventsScheduler поток на котором выполняется обработка событий. По умолчанию io()
 */
class StateMachineImpl(
    private val statesScheduler: Scheduler = TensorSchedulers.androidUiScheduler,
    private val eventsScheduler: Scheduler = Schedulers.io()
) :
    StateMachineInner {

    private val disposer = CompositeDisposable()
    private val events = PublishSubject.create<SessionEvent>()
    private val states = BehaviorProcessor.create<SessionState>()
    override val currentStateObservable = states
    private var tag: String? = null
    private var logger: (String) -> Unit = {}
    private val pendingExecutor = PublishSubject.create<Long>()
    private val timeoutDisposable = CompositeDisposable()
    private val map = ConcurrentHashMap<KClass<out SessionStateEvent>, (t: Any) -> Unit>()

    init {
        disposer += events.observeOn(eventsScheduler).subscribe(::invokeEvent)
    }

    private fun <SESSION_EVENT : SessionEvent> invokeEvent(event: SESSION_EVENT) {
        logger(
            "Event ${event.javaClass.simpleName} called for state ${states.value?.javaClass?.simpleName} " +
                    "${System.identityHashCode(this)} "
        )
        states.value?.invokeEvent(event)
    }

    @Suppress("OverridingDeprecatedMember")
    override fun enableLogging(tag: String, logger: (String) -> Unit) {
        this.tag = tag
        this.logger = logger
    }

    override fun stop() {
        logger("stop() called")
        disposer.dispose()
        states.value?.stop()
        events.onComplete()
        states.onComplete()
        //AppWatcher.objectWatcher.watch(this) todo вернуть после https://online.sbis.ru/opendoc.html?guid=ec29b0fe-9966-4836-bf2c-1fb0bed48d9c
    }

    @Suppress("UNCHECKED_CAST")
    override fun <SESSION_STATE_EVENT : SessionStateEvent> state(
        clazz: KClass<SESSION_STATE_EVENT>,
        consumer: (t: SESSION_STATE_EVENT) -> Unit
    ) = map.put(clazz, consumer as (t: Any) -> Unit)

    override fun setState(state: SessionState) {
        logger("State ${state.javaClass.simpleName} set ${System.identityHashCode(this)}")
        states.value?.stop()
        state.stateMachine = this
        states.onNext(state)

        //FIXME: https://online.sbis.ru/opendoc.html?guid=df2295a0-9cb0-4856-a549-49f502857a03
        val blocking = Observable.just(state)
            .observeOn(statesScheduler)
            .map { it.onAfterSet() }
            .blockingFirst()
        logger("State ${state.javaClass.simpleName} set finished ${System.identityHashCode(this)}")

        timeoutDisposable.clear()
    }

    override fun currentState() = states.value

    override fun setState(state: SessionState, time: Long, timeUnit: TimeUnit) {
        timeoutDisposable += Observable.timer(time, timeUnit)
            .mergeWith(pendingExecutor)
            .firstElement()
            .subscribe { setState(state) }
    }

    override fun executePendingNow() = pendingExecutor.onNext(0)

    override fun <SESSION_STATE_EVENT : SessionStateEvent> fire(event: SESSION_STATE_EVENT) {
        (map[event::class] as? (t: SESSION_STATE_EVENT) -> Unit)?.invoke(event)
    }

    override fun fire(event: SessionEvent) {
        logger("fire ${event.javaClass.simpleName}")
        events.onNext(event)
    }
}