package ru.tensor.sbis.common.util.scroll

import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Helper that emits scroll events to subscribers can update UI if needed.
 */
interface ScrollHelper : Feature {
    val scrollEventObservable: Observable<ScrollEvent>

    /**
     * Handles user scroll events and emits these events if they passed some conditions.
     * @param dy - The amount of vertical scroll.
     * @param computeVerticalScrollOffset - Computed vertical scroll offset to hide [ru.tensor.sbis.design.view.input.searchinput.SearchInput].
     */
    fun onScroll(dy: Int, computeVerticalScrollOffset: Int)

    /**
     * Emulates user scroll actions to subscribers can update UI if needed.
     * Used, for example, when user selects some drawer menu item, show/hide
     * folders panel or enable/disable mass operations mode.
     * @param event - Event to send.
     */
    fun sendFakeScrollEvent(event: ScrollEvent)

    /**
     * Reset state of helper.
     * Actually send [ScrollEvent.SCROLL_UP_FAKE]
     * event.
     */
    fun resetState()

    /**
     * Предоставляет последнее полученное событие
     */
    val latestEvent: ScrollEvent?
}