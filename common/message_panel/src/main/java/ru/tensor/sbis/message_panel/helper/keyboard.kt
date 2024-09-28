/**
 * Инструменты для управления подъёмом клавиатуры
 *
 * @author vv.chekurda
 * @since 1/15/2020
 */
package ru.tensor.sbis.message_panel.helper

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.annotations.SchedulerSupport
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEvent
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.OpenedByRequest
import java.util.concurrent.TimeUnit

/**
 * Количество итераций ожидания фокуса на view
 */
internal const val FOCUS_CHECK_COUNT = 20L
/**
 * Интервал между проверками фокуса на view
 */
internal const val FOCUS_CHECK_PERIOD = 100L

/**
 * Запрос фокуса для [view] с ожиданием его получения. Если [view] уже в фокусе, результат будет доставлен немедленно
 *
 * @receiver источник события, по которому нужно запускать запрос фокуса
 * @param view объект, для которого нужно запросить фокус
 * @param checkFocusSampler источник событий, по которым нужно проверять наличие фокуса, если он не был получен
 * немедленно
 */
@SchedulerSupport(SchedulerSupport.COMPUTATION)
internal fun Observable<*>.toFocusRequestObservable(
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
internal fun View.showKeyboard(): Completable = choseKeyboardChangeStrategy(
    createDefaultShowKeyboardAction(),
    showKeyboardFallback()
)

/**
 * Запрос скрытия клавиатуры для компонента в фокусе
 */
internal fun View.hideKeyboard() = Completable.fromAction {
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
internal fun View.choseKeyboardChangeStrategy(
    action: Completable,
    fallbackKeyboardHandler: Completable
): Completable = when {
    isActiveInput                   -> action
    else                            -> fallbackKeyboardHandler
}

/**
 * Возвращает [Observable] с [KeyboardEvent], который игнорирует событие [OpenedByRequest] если поле ввода отключено
 */
internal fun Observable<Pair<KeyboardEvent, Boolean>>.skipOpenedByRequestIfDisabled() =
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

private val View.isActiveInput
    get() = (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).isActive(this)

@SchedulerSupport(SchedulerSupport.COMPUTATION)
private fun createDefaultFocusCheckSampler(): Observable<*> =
    Observable.intervalRange(0L, FOCUS_CHECK_COUNT, 0L, FOCUS_CHECK_PERIOD, TimeUnit.MILLISECONDS)

