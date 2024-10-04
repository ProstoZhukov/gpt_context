package ru.tensor.sbis.design.message_panel.vm.keyboard

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.rx.scheduler.TensorSchedulers
import ru.tensor.sbis.design.message_panel.view.layout.MessagePanelEditText
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Реализация делегата [KeyboardDelegate] для работы с клавиаутрой.
 *
 * @author vv.chekurda
 */
internal class KeyboardDelegateImpl @Inject constructor(): KeyboardDelegate {

    private lateinit var scope: CoroutineScope
    private lateinit var isEnabled: StateFlow<Boolean>
    private lateinit var disposer: CompositeDisposable
    private val observeOn: Scheduler = TensorSchedulers.androidUiScheduler

    private val keyboardSubject: Subject<Boolean> = PublishSubject.create()
    private var ignoreAdjustHelperEvents: Boolean = false
    private val keyboardState = BehaviorSubject.create<KeyboardEvent>()

    override val keyboardHeight = MutableStateFlow(0)

    override fun showKeyboard() {
        postKeyboardEvent(OpenedByRequest)
    }

    override fun hideKeyboard() {
        postKeyboardEvent(ClosedByRequest)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        postKeyboardEvent(OpenedByAdjustHelper(keyboardHeight))
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        postKeyboardEvent(ClosedByAdjustHelper(0))
        return true
    }

    override fun onBottomOffsetChanged(offset: Int) {
        keyboardHeight.value = offset
    }

    override fun init(scope: CoroutineScope, isEnabled: StateFlow<Boolean>) {
        this.scope = scope
        this.isEnabled = isEnabled
    }

    override fun attachInputView(inputView: MessagePanelEditText) {
        disposer = CompositeDisposable()
        inputView.setOnKeyPreImeListener { keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                postKeyboardEvent(ClosedByRequest)
                true
            } else {
                false
            }
        }
        inputView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            postKeyboardEvent(if (hasFocus) OpenedByFocus else ClosedByFocus)
        }

        // обработка запросов на подъём клавиатуры
        disposer += keyboardSubject
            .filter { it }
            .doOnNext { ignoreAdjustHelperEvents = true }
            .toFocusRequestObservable(inputView)
            /*
            Важно запрашивать клавиатуру в mainThread так как опубликованный из фонового потока запрос может быть
            исполнен уже после поворота экрана (когда это неактуально)
             */
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable {
                inputView.showKeyboard()
                    .doOnComplete { ignoreAdjustHelperEvents = false }
            }
            .subscribe()
        // обработка запросов на скрытие клавиатуры
        disposer += keyboardSubject
            .filter { !it }
            .flatMapCompletable { inputView.hideKeyboard() }
            .subscribe()

        val enableStateObservable = BehaviorSubject.createDefault(isEnabled.value)
        scope.launch {
            isEnabled.collect { enableStateObservable.onNext(it) }
        }
        disposer += Observable.combineLatest(
            keyboardState,
            enableStateObservable
        ) { state, enabled -> state to enabled }
            .skipOpenedByRequestIfDisabled()
            .subscribe {
                when (it) {
                    OpenedByRequest -> keyboardSubject.onNext(true)
                    ClosedByRequest -> keyboardSubject.onNext(false)
                    is OpenedByAdjustHelper -> keyboardHeight.value = it.height
                    is ClosedByAdjustHelper -> keyboardHeight.value = it.height
                    else -> Unit
                }
            }

        disposer += keyboardState
            .filter {
                it == ClosedByRequest || it is ClosedByAdjustHelper
                        && !ignoreAdjustHelperEvents
                        && inputView.isFocusedButNotActiveInput
            }
            .observeOn(observeOn)
            .subscribe({ inputView.clearFocus() }, Timber::e)
    }

    override fun detachInputView() {
        disposer.dispose()
    }

    private fun postKeyboardEvent(event: KeyboardEvent) {
        keyboardState.onNext(event)
    }
}

/**
 * Количество итераций ожидания фокуса на view
 */
private const val FOCUS_CHECK_COUNT = 20L
/**
 * Интервал между проверками фокуса на view
 */
private const val FOCUS_CHECK_PERIOD = 100L

private val EditText.isFocusedButNotActiveInput: Boolean
    get() = hasFocus() && !isActiveInput

private val View.isActiveInput
    get() = (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).isActive(this)

/**
 * Запрос фокуса для [view] с ожиданием его получения. Если [view] уже в фокусе, результат будет доставлен немедленно
 *
 * @receiver источник события, по которому нужно запускать запрос фокуса
 * @param view объект, для которого нужно запросить фокус
 * @param checkFocusSampler источник событий, по которым нужно проверять наличие фокуса, если он не был получен
 * немедленно
 */
@SchedulerSupport(SchedulerSupport.COMPUTATION)
private fun Observable<*>.toFocusRequestObservable(
    view: View,
    checkFocusSampler: Observable<*> = createDefaultFocusCheckSampler()
): Observable<Boolean> {
    val focusRequest = Completable.fromAction { view.requestFocus() }
    val inputTargetCheck = Observable.fromCallable(view::isActiveInput)
    val inputTargetCheckInterval = checkFocusSampler.flatMap { inputTargetCheck }
    return switchMap {
        /*
        Запросим фокус и сразу проверим его получение.
        Если фокус не был получен немедленно, будем проверять его наличие с периодичностью
         */
        focusRequest.andThen(inputTargetCheck.concatWith(inputTargetCheckInterval))
            // ожидаем пока поле ввода не станет активным
            .takeUntil { it }
            /*
            Последнее событие будет положительным результатом.
            Если фокуса так и не дождались, спустим по потоку информацию об отсутствии фокуса
             */
            .last(false)
            .toObservable()
    }
}

/**
 * Запрос подъёма клавиатуры
 */
private fun View.showKeyboard(): Completable = choseKeyboardChangeStrategy(
    createDefaultShowKeyboardAction(),
    showKeyboardFallback()
)

/**
 * Запрос скрытия клавиатуры для компонента в фокусе
 */
private fun View.hideKeyboard() = Completable.fromAction {
    if (isActiveInput) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // флаги не установлены, скрываем независимо от способа подъёма
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}

/**
 * Изменение состояния клавиатуры в зависимости от фокуса [View]
 *
 * @param action действие для переключения состояния клавиатуры для состояния в фокусе (ожидаемый сценарий)
 * @param fallbackKeyboardHandler действие для изменения состояния клавиатуры, когда фокуса нет (принудительный сценарий)
 */
private fun View.choseKeyboardChangeStrategy(
    action: Completable,
    fallbackKeyboardHandler: Completable
): Completable = when {
    isActiveInput                   -> action
    else                            -> fallbackKeyboardHandler
}

/**
 * Возвращает [Observable] с [KeyboardEvent], который игнорирует событие [OpenedByRequest] если поле ввода отключено
 */
private fun Observable<Pair<KeyboardEvent, Boolean>>.skipOpenedByRequestIfDisabled() =
    filter { (event, enabled) -> enabled || event != OpenedByRequest }
        .map { it.first }

private fun View.createDefaultShowKeyboardAction(): Completable {
    return Completable.fromAction {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // поле ввода активно. Можно безопасно запрашивать клавиатуру для него
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

private fun View.showKeyboardFallback() = Completable.fromAction {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    /*
    TODO: 1/15/2020 Дождаться подъёма и переключиться в обычный режим
     https://online.sbis.ru/opendoc.html?guid=a1e218a8-345a-4ee2-90d4-599834d3519f
     */
}

@SchedulerSupport(SchedulerSupport.COMPUTATION)
private fun createDefaultFocusCheckSampler(): Observable<*> =
    Observable.intervalRange(0L, FOCUS_CHECK_COUNT, 0L, FOCUS_CHECK_PERIOD, TimeUnit.MILLISECONDS)