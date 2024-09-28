package ru.tensor.sbis.message_panel.viewModel.livedata

import io.reactivex.Observable

/**
 * Статус числа отображаемых строк текста
 */
enum class LinesCountStatus {

    /**
     * Значение меньше порогового
     */
    THRESHOLD_NOT_EXCEEDED,

    /**
     * Значение достигает порогового, либо превышает его
     */
    THRESHOLD_EXCEEDED,

    /**
     * Значение достигло порогового, но не будет его превышать при увеличении доступной ширины
     */
    AMBIGUOUS
}

/**
 * Интерфейс управляющего воздействия от поля ввода
 *
 * @author vv.chekurda
 * @since 7/23/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelDataControls {
    val newDialogModeEnabled: Observable<Boolean>
    val minLines: Observable<Int>
    /**
     * Статус превышения порогового числа строк. При печати может публиковаться большое количество
     * одинаковых событий - при подписке нужна фильтрация
     */
    val linesCountStatus: Observable<LinesCountStatus>
    val isLandscape: Observable<Boolean>

    fun onCompletelyVisibleLinesCountChanged(linesCount: Int, linesCountWithoutWidthLoss: Int)
    fun newDialogModeEnabled(enabled: Boolean)
    fun setIsLandscape(isLandscape: Boolean)
    fun setMinLines(count: Int)
}