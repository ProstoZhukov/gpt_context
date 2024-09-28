package ru.tensor.sbis.common.util.scroll

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ScrollHelperImpl(private val thresholdTopOffset: Float) : ScrollHelper {
    private val mSubject: PublishSubject<ScrollEvent> = PublishSubject.create()
    override var latestEvent: ScrollEvent? = null
        private set

    /**
     * Custom comparator for [Observable.distinctUntilChanged]
     * allows to duplicate [ScrollEvent.SCROLL_UP_FAKE_SOFT] event.
     */
    override val scrollEventObservable: Observable<ScrollEvent>
        get() = mSubject.distinctUntilChanged { previous: ScrollEvent, next: ScrollEvent ->
            (!(previous == ScrollEvent.SCROLL_UP_FAKE_SOFT && next == ScrollEvent.SCROLL_UP_FAKE_SOFT)
                    && previous == next)
        }

    override fun onScroll(dy: Int, computeVerticalScrollOffset: Int) {
        if (dy > HIDE_THRESHOLD) {
            if (computeVerticalScrollOffset > thresholdTopOffset) {
                setEventTypeInternal(ScrollEvent.SCROLL_DOWN)
            }
        } else if (dy < -HIDE_THRESHOLD) {
            setEventTypeInternal(ScrollEvent.SCROLL_UP)
        }
    }

    override fun sendFakeScrollEvent(event: ScrollEvent) {
        setEventTypeInternal(event)
    }

    override fun resetState() {
        setEventTypeInternal(ScrollEvent.SCROLL_UP_FAKE)
    }

    private fun setEventTypeInternal(scrollEvent: ScrollEvent) {
        latestEvent = scrollEvent
        mSubject.onNext(scrollEvent)
    }

    companion object {
        private const val HIDE_THRESHOLD = 1
    }
}