package ru.tensor.sbis.onboarding.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import ru.tensor.sbis.design.view_ext.viewpager.ViewPagerFixed
import kotlin.math.abs

private const val NEXT_ACTION_THROTTLE_DURATION = 350

internal class OnboardingViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPagerFixed(context, attrs) {

    private var downX: Float = 0F
    private var lockScroll: Boolean = false
    private val pagingSlop: Int = ViewConfiguration.get(context)
        .scaledPagingTouchSlop
    private var isForbiddenBackDirection = false
    private var swipeListener: OnSwipeListener? = null
    private var lastNextAction: Long = 0
    private var deferredNextAction: Boolean = false

    fun setOnSwipeListener(listener: OnSwipeListener) {
        swipeListener = listener
    }

    fun preventSwipeBack(forbidden: Boolean) {
        isForbiddenBackDirection = forbidden
    }

    fun moveBackIfCan(): Boolean {
        val previousItem = currentItem - 1
        return (!isForbiddenBackDirection && previousItem >= 0).also {
            if (it) {
                setCurrentItem(previousItem, true)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        var interrupted = false
        ev.takeIf { swipeListener != null || ev != null }
            ?.run {
                when (action) {
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        lockScroll = false
                        downX = 0F
                    }
                    MotionEvent.ACTION_DOWN   -> {
                        downX = x
                    }
                    MotionEvent.ACTION_MOVE   -> {
                        if (lockScroll) {
                            interrupted = true
                        } else {
                            val xDiff: Float = x - downX
                            val isScrolling = abs(xDiff) > pagingSlop
                            if (isScrolling) {
                                if (downX < x) {
                                    interrupted = processPreviousPage()
                                } else if (downX > x) {
                                    interrupted = processNextPage()
                                }
                            }
                        }
                    }
                    else                      -> Unit
                }
            }
        if (interrupted) {
            lockScroll = true
        }
        return if (interrupted) false else super.onTouchEvent(ev)
    }

    private fun processPreviousPage(): Boolean {
        val onSwipeIntercept =
            adapter?.instantiateItem(this@OnboardingViewPager, currentItem) as? OnSwipeIntercept
        var interrupted = onSwipeIntercept?.onSwipeBackIntercept() ?: false
        return swipeListener?.run {
            when {
                interrupted              -> Unit
                isForbiddenBackDirection -> interrupted = true
                currentItem == 0         -> swipeListener?.onSwipeOutAtStart()
                else                     -> swipeListener?.onSwipeBack()
            }
            interrupted
        } ?: interrupted
    }

    private fun processNextPage(): Boolean {
        val previousNextAction = lastNextAction
        lastNextAction = System.currentTimeMillis()
        if (deferredNextAction && System.currentTimeMillis() - previousNextAction < NEXT_ACTION_THROTTLE_DURATION) {
            return true
        }
        deferredNextAction = false
        val onSwipeIntercept =
            adapter?.instantiateItem(this@OnboardingViewPager, currentItem) as? OnSwipeIntercept
        var interrupted = onSwipeIntercept?.onSwipeForwardIntercept() ?: false
        if (interrupted) {
            return true
        }
        return swipeListener?.run {
            val pageCount = adapter?.let { it.count - 1 }
            if (currentItem == pageCount) {
                onSwipeOutAtEnd {}
            } else {
                interrupted = onSwipeForward(currentItem) {
                    setCurrentItem(currentItem + 1, true)
                }.also { deferredNextAction = it }
            }
            interrupted
        } ?: false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        swipeListener = null
    }
}

