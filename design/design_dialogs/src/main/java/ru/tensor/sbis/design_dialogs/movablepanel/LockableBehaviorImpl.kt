package ru.tensor.sbis.design_dialogs.movablepanel

import android.animation.Animator
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.SCROLL_AXIS_VERTICAL
import androidx.core.view.ViewCompat.TYPE_NON_TOUCH
import androidx.core.view.ViewCompat.isAttachedToWindow
import androidx.core.view.ViewCompat.isNestedScrollingEnabled
import androidx.core.view.ViewCompat.offsetTopAndBottom
import androidx.core.view.ViewCompat.postOnAnimation
import androidx.core.view.children
import androidx.customview.widget.ViewDragHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import ru.tensor.sbis.design.custom_view_tools.utils.layout
import ru.tensor.sbis.design.design_dialogs.R
import ru.tensor.sbis.design.view_ext.viewpager.ViewPagerFixed
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.Absolute
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.Dimen
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.FitToContent
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.Percent
import java.lang.ref.WeakReference
import kotlin.math.abs

/**
 * Блокируемый bottomSheet - копия с изменениями [BottomSheetBehavior]
 *
 * @author ga.malinskiy
 */
internal class LockableBehaviorImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    Behavior<View>(context, attrs), LockableBehavior {

    override var isBehaviorLocked: Boolean = false
    override var ignoreOpenAnim: Boolean = false
    override var ignoreLock: Boolean = false
    override var animateParentHeightChanges: Boolean = true

    private val topDownDirection: Boolean
    private val topOffset: Int
    private val saveScrollState: Boolean
    private var currentOffset: Int = UNDEFINED
    private var peekHeight: MovablePanelPeekHeight = Percent(0F)
    private var initPeekHeight: MovablePanelPeekHeight = Percent(0F)
    private var skipFirstCallback = false

    private var peekHeightList: List<MovablePanelPeekHeight> = listOf()
    private var offsetToPeekHeightMap: HashMap<Int, MovablePanelPeekHeight> = hashMapOf()
    private var offsetList: ArrayList<Int> = arrayListOf()
    private var peekHeightListChanged = false

    private var parentHeight = 0
    private var childHeight = 0

    private val isLocked: Boolean
        get() = lockPanelNeeded || isBehaviorLocked
    private var lockPanelNeeded = false
    private var ignoreEvents = false
    private var lastNestedScrollDy = 0
    private var nestedScrolled = false
    private var activePointerId = 0
    private var initialY = 0
    private var touchingScrollingChild = false

    private var viewDragHelper: CustomViewDragHelper? = null
    private val dragCallback: CustomViewDragHelper.Callback = ViewDragHelperCallback()
    private var postedRunnable: (() -> Unit)? = null

    /**
     * Ребенок CoordinatorLayout - вью на которой весит behavior
     */
    private var viewRef: WeakReference<View>? = null

    /**
     * Вложенный скролящийся ребенок - вью со скролом
     */
    private var nestedScrollingChildRef: WeakReference<View>? = null
    private var velocityTracker: VelocityTracker? = null

    private var callback: MovablePanelMovingCallback? = null

    private var initPeekHeightCalled = false
    private var changePeekHeightRunning = false
    private var peekHeightChanged = false
    private var childHeightChanged = false
    private var parentHeightChanged = false
    private var isShowAnimationRunning = false
    private var showingAnimationDurationMs: Int = UNDEFINED

    init {
        with(context.theme.obtainStyledAttributes(attrs, R.styleable.MovablePanelStyle, 0, 0)) {
            topDownDirection = getBoolean(R.styleable.MovablePanelStyle_MovablePanel_topDownDirection, false)
            topOffset = getDimensionPixelSize(R.styleable.MovablePanelStyle_MovablePanel_topOffset, 0)
            saveScrollState = getBoolean(R.styleable.MovablePanelStyle_MovablePanel_saveScrollState, false)
            showingAnimationDurationMs = getInt(R.styleable.MovablePanelStyle_MovablePanel_showing_duration, UNDEFINED)
            recycle()
        }
    }

    override fun getPeekHeight(): MovablePanelPeekHeight = peekHeight

    override fun calculateSlideOffsetByPeekHeight(peekHeight: MovablePanelPeekHeight, resources: Resources): Float =
        calculateSlideOffset(peekHeight.mapToOffset(resources) - topOffset)

    override fun setPeekHeightList(
        peekHeightList: List<MovablePanelPeekHeight>,
        initPeekHeight: MovablePanelPeekHeight
    ) {
        require(peekHeightList.size in 2..4) { "Должно быть задано от 2 до 4 высот" }
        peekHeightListChanged = this.peekHeightList != peekHeightList
        this.peekHeightList = ArrayList(peekHeightList).apply {
            if (any { it.isEqual(FitToContent()) }) add(removeAt(indexOfFirst { it.isEqual(FitToContent()) }))
        }
        this.initPeekHeight = initPeekHeight
        skipFirstCallback = initPeekHeight.isEqual(Percent(0F))
        if (initPeekHeightCalled) setPeekHeight(initPeekHeight)
    }

    override fun setPeekHeight(peekHeight: MovablePanelPeekHeight) {
        peekHeightChanged = this.peekHeight.isNotEqual(peekHeight)
        this.peekHeight = peekHeight
        if (initPeekHeightCalled.not()) {
            initPeekHeight = peekHeight
            skipFirstCallback = initPeekHeight.isEqual(Percent(0F))
        }
        if (isShowAnimationRunning) return
        if (changePeekHeightRunning.not() || peekHeightChanged || childHeightChanged || parentHeightChanged) {
            if (changePeekHeightRunning) postedRunnable = null
            changePeekHeightRunning = true
            changePeekHeight(peekHeight)
        }
    }

    override fun setMovingCallback(callback: MovablePanelMovingCallback) {
        this.callback = callback
    }

    override fun invalidateScrollingChildView() {
        val manualProvidedScrollable = callback?.getScrollableView()
        val scrollingChild = manualProvidedScrollable ?: findScrollingChild(viewRef?.get())
        nestedScrollingChildRef = scrollingChild?.let { WeakReference(it) }

        scrollingChild?.apply { scrollChildToStartIfNeeded(this) }
    }

    override fun startShowingAnimation(peekHeight: MovablePanelPeekHeight) {
        val child = viewRef?.get()
        if (child == null || showingAnimationDurationMs < 0) {
            setPeekHeight(peekHeight)
            return
        }

        val durationMs = showingAnimationDurationMs.coerceAtLeast(MIN_ANIMATION_DURATION)
        var targetOffset = 0
        var diff = 0

        fun updateTargetOffset() {
            targetOffset = calcByDirection(peekHeight.mapToOffset(child.resources))
            diff = child.top - targetOffset
        }

        isShowAnimationRunning = true
        setPeekHeight(peekHeight)
        updateTargetOffset()

        child.animate()
            .setDuration(durationMs.toLong())
            .setInterpolator(DecelerateInterpolator())
            .setUpdateListener {
                if (childHeightChanged) {
                    updateTargetOffset()
                    childHeightChanged = false
                }
                val topOffset = targetOffset + ((1 - it.animatedFraction) * diff).toInt()
                child.top = topOffset
            }
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) = Unit
                override fun onAnimationStart(animation: Animator) = Unit
                override fun onAnimationCancel(animation: Animator) {
                    onAnimationEnd(animation)
                }

                override fun onAnimationEnd(animation: Animator) {
                    isShowAnimationRunning = false
                    setOffsetInternal(targetOffset)
                }
            })
            .start()
    }

    private fun scrollChildToStartIfNeeded(scrollingChild: View) {
        if (isBehaviorMoving() || ignoreLock) return
        val needScroll = saveScrollState.not() && currentOffset != offsetList.first()
        val canScroll = canScroll(scrollingChild)
        if (needScroll && canScroll) {
            if (scrollingChild is RecyclerView) scrollingChild.scrollToPosition(0)
            else scrollingChild.scrollTo(0, 0)
        }
    }

    private fun canScroll(scrollingView: View?) =
        // Проверяем что список отображен не на последней позиции
        if (topDownDirection) scrollingView?.canScrollVertically(1) == true
        // Проверяем что список отображен не на 0й позиции
        else scrollingView?.canScrollVertically(-1) == true

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        super.onAttachedToLayoutParams(params)
        viewRef = null
        viewDragHelper = null
    }

    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
        viewRef = null
        viewDragHelper = null
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        viewRef = WeakReference(child)
        viewDragHelper = viewDragHelper ?: CustomViewDragHelper.create(parent, dragCallback)

        val savedTop = child.top
        parent.onLayoutChild(child, layoutDirection)
        val newParentHeight = parent.height
        parentHeightChanged = newParentHeight != parentHeight
        val newChildHeight = calculateChildHeight(child)
        childHeightChanged = newChildHeight != childHeight
        val invalidateOffsetNeeded = peekHeightListChanged || parentHeightChanged || childHeightChanged

        if (invalidateOffsetNeeded) {
            // Если высота родителя или ребенка изменились, необходимо пересчитать офсеты для корректного отображения
            offsetList.clear()
            offsetToPeekHeightMap.clear()

            parentHeight = newParentHeight
            childHeight = newChildHeight

            peekHeightListChanged = false
        }

        if (ignoreOpenAnim.not() && currentOffset == UNDEFINED) currentOffset = parentHeight

        if (offsetList.isEmpty()) {
            peekHeightList.forEach {
                // offset - отступ от верхней границы родителя при отображении одной страницы контента без счетчика
                val offset = it.mapToOffset(parent.resources)

                // При инициализации выставлем оффсет равным переданному значению initPeekHeight
                if (ignoreOpenAnim && currentOffset == UNDEFINED && it == initPeekHeight) currentOffset = offset

                val alreadyHasOffset = offsetToPeekHeightMap.containsKey(offset)
                val hasCollision = it.isEqual(FitToContent()) && alreadyHasOffset
                if (hasCollision.not()) {
                    offsetToPeekHeightMap[offset] = it
                    offsetList.add(offset)
                }
            }
            offsetList.sort()
        }

        val nextOffset = when {
            isBehaviorMoving() -> savedTop - child.top
            currentOffset != UNDEFINED -> calcByDirection(currentOffset)
            else -> null
        }

        if (nextOffset != null) offsetTopAndBottom(child, nextOffset)
        if (initPeekHeightCalled) {
            val newPeekHeight: MovablePanelPeekHeight? = when {
                needSwitchHeightToFit() -> {
                    peekHeightList.find { it is FitToContent }
                }
                peekHeightChanged || childHeightChanged || parentHeightChanged -> {
                    peekHeight
                }
                else -> {
                    null
                }
            }

            newPeekHeight?.also(::setPeekHeight)
        } else {
            // Метод changePeekHeight не сработает, если будет вызван до инициализации viewRef, поэтому зовем повторно
            initPeekHeightCalled = true
            if (initPeekHeight.isNotEqual(Percent(0F))) {
                if (showingAnimationDurationMs > 0) {
                    startShowingAnimation(initPeekHeight)
                } else {
                    setPeekHeight(initPeekHeight)
                }
            }
        }

        invalidateScrollingChildView()
        return true
    }

    private fun needSwitchHeightToFit(): Boolean =
        peekHeightList.any { it is FitToContent } &&
            !isLocked &&
            childHeightChanged &&
            childHeight < parentHeight - currentOffset &&
            peekHeight != getHiddenPeekHeight()

    private fun getHiddenPeekHeight(): MovablePanelPeekHeight? =
        if (topDownDirection) {
            offsetToPeekHeightMap.entries.minByOrNull { it.key }?.value
        } else {
            offsetToPeekHeightMap.entries.maxByOrNull { it.key }?.value
        }

    private fun calculateChildHeight(child: View): Int {
        return if (peekHeightList.any { it is FitToContent }) {
            // Для высоты панели FitToContent необходимо посчитать высоту контейнера в котором лежит контент и он WrapContent
            // Его фактическое местоположение через один внутренний контейнер, который используется для фона,
            // если его убрать при изменении высоты контента будет видно то, что находится под шторкой
            (child as? LinearLayout)?.run {
                val insetViewHeight = getChildAt(if (topDownDirection) 1 else 0).height
                // Контейнер с фоном, он MatchParent
                val innerContainerChildPosition = if (topDownDirection) 0 else 1
                val innerContainer = getChildAt(innerContainerChildPosition) as? RelativeLayout
                // Наш искомый контейнер с контентом, он WrapContent и именно его высота изменяется
                val innerContainerChild = innerContainer?.getChildAt(0)
                val childHeight = innerContainerChild?.height ?: 0
                insetViewHeight + childHeight
            } ?: child.height
        } else child.height
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, event: MotionEvent): Boolean {
        if (ignoreLock.not()) lockPanelNeeded = canScroll(nestedScrollingChildRef?.get())

        if ((topDownDirection.not() && saveScrollState.not() && isLocked) || child.isShown.not()) {
            ignoreEvents = true
            return false
        }

        val action = event.actionMasked
        if (action == ACTION_DOWN) reset()
        requireVelocityTracker().addMovement(event)

        when (action) {
            ACTION_DOWN -> {
                initialY = event.y.toInt()
                val initialX = event.x.toInt()
                if (currentOffset != OFFSET_SETTLING) {
                    nestedScrollingChildRef?.get()?.let {
                        if (parent.isPointInChildBounds(it, initialX, initialY)) {
                            activePointerId = event.getPointerId(event.actionIndex)
                            touchingScrollingChild = true
                        }
                    }
                }
                ignoreEvents = activePointerId == UNDEFINED &&
                    parent.isPointInChildBounds(child, initialX, initialY).not()
            }

            ACTION_UP, ACTION_CANCEL -> {
                touchingScrollingChild = false
                activePointerId = UNDEFINED
                if (ignoreEvents) {
                    ignoreEvents = false
                    return false
                }
            }

            ACTION_MOVE -> Unit
        }
        if (ignoreEvents.not() && viewDragHelper?.shouldInterceptTouchEvent(event) == true) return true

        val scrollView = nestedScrollingChildRef?.get()
        return action == ACTION_MOVE && ignoreEvents.not() &&
            currentOffset != OFFSET_DRAGGING &&
            scrollView != null && parent.isPointInChildBounds(scrollView, event.x.toInt(), event.y.toInt()).not() &&
            viewDragHelper?.let { abs(initialY - event.y) > it.touchSlop } == true
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: View, event: MotionEvent): Boolean {
        if (child.isShown.not() || isLocked) return false

        val action = event.actionMasked
        if (currentOffset == OFFSET_DRAGGING && action == ACTION_DOWN) return true

        viewDragHelper?.processTouchEvent(event)

        if (action == ACTION_DOWN) reset()
        requireVelocityTracker().addMovement(event)

        if (action == ACTION_MOVE && ignoreEvents.not()) {
            val actionIndex = event.actionIndex
            if (viewDragHelper?.let { abs(initialY - event.y) > it.touchSlop } == true && actionIndex >= 0) {
                viewDragHelper?.captureChildView(child, event.getPointerId(actionIndex))
            }
        }
        return ignoreEvents.not()
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        lastNestedScrollDy = 0
        nestedScrolled = false
        // Произошло ли движение вложенного элемента по вертикальной оси
        return (nestedScrollAxes and SCROLL_AXIS_VERTICAL) != 0 && isLocked.not()
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout, child, target, dxConsumed, dyConsumed,
            dxUnconsumed, dyUnconsumed, type, consumed
        )
        stopNestedScrollIfNeeded(dyUnconsumed, target, type)
    }

    private fun stopNestedScrollIfNeeded(dy: Int, target: View, type: Int) {
        if (type == TYPE_NON_TOUCH) {
            val canScroll = dy < 0 && target.canScrollVertically(1) || dy > 0 && target.canScrollVertically(-1)
            if (currentOffset == 0 && canScroll) {
                ViewCompat.stopNestedScroll(target, TYPE_NON_TOUCH)
            }
        }
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (type == TYPE_NON_TOUCH || target !== nestedScrollingChildRef?.get()) {
            stopNestedScrollIfNeeded(dy, target, type)
            return
        }
        val currentTop = child.top
        val newTop = currentTop - dy
        val consumedValue: Int
        val offset: Int

        if (dy > 0 && (canScroll(target).not() || ignoreLock)) { // Список вверх
            val hiddenOffset = offsetList.last() * -1
            val expandedOffset = offsetList.first()
            val dragging = if (topDownDirection) newTop > hiddenOffset else newTop > expandedOffset
            if (dragging) {
                offset = OFFSET_DRAGGING
                consumedValue = dy
            } else {
                offset = if (topDownDirection) hiddenOffset else expandedOffset
                consumedValue = currentTop - offset
            }
            consumed[1] = consumedValue
            offsetTopAndBottom(child, -consumedValue)
            val convertOffset = if (isBehaviorMoving(offset)) offset else calcByDirection(offset)
            setOffsetInternal(convertOffset)
        } else if (dy < 0 && canScroll(target).not()) { // Список вниз
            if (canScroll(target)) return
            val hiddenOffset = offsetList.last()
            val expandedOffset = offsetList.first() * -1
            val dragging = if (topDownDirection) newTop < expandedOffset else newTop < hiddenOffset
            if (dragging) {
                offset = OFFSET_DRAGGING
                consumedValue = dy
            } else {
                offset = if (topDownDirection) expandedOffset else hiddenOffset
                consumedValue = currentTop - offset
            }
            consumed[1] = consumedValue
            offsetTopAndBottom(child, -consumedValue)
            val convertOffset = if (isBehaviorMoving(offset)) offset else calcByDirection(offset)
            setOffsetInternal(convertOffset)
        }
        dispatchOnSlide(abs(child.top))
        lastNestedScrollDy = dy
        nestedScrolled = true
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        if (target !== nestedScrollingChildRef?.get() || nestedScrolled.not()) return

        val top: Int
        val currentTop = abs(child.top)

        top = offsetList.run {
            when {
                lastNestedScrollDy > 0 -> {
                    // Шторка двигается вверх, нужно найти следующий офсет, что меньше нынешнего значения шторки
                    if (topDownDirection) find { currentTop <= it } ?: last()
                    else findLast { currentTop > it } ?: first()
                }

                lastNestedScrollDy == 0 -> currentOffset
                else -> {
                    if (topDownDirection) findLast { currentTop > it } ?: first()
                    else find { currentTop <= it } ?: last()
                }
            }
        }

        offsetToPeekHeightMap[top]?.let { peekHeight = it }
        startSettlingAnimation(child, top, false)
        nestedScrolled = false
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val nestedScrollingChild = nestedScrollingChildRef?.get()
        return if (nestedScrollingChild != null) {
            val isNotExpanded = currentOffset != offsetList.first()
            val onNestedPreFling = super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
            val velocityCorrect = if (topDownDirection) velocityY > 0 else velocityY < 0
            // Разрешаем обычный скрол списку, если шторка раскрыта не полностью,
            // при этом есть куда скролить и движение именно в эту сторону
            val acceptFling = (isNotExpanded && velocityCorrect && canScroll(nestedScrollingChild)).not()
            target === nestedScrollingChild && (isNotExpanded || onNestedPreFling) && acceptFling
        } else false
    }

    private fun changePeekHeight(peekHeight: MovablePanelPeekHeight) {
        val child = viewRef?.get() ?: return
        val parent = child.parent
        if (parent != null && parent.isLayoutRequested && isAttachedToWindow(child)) {
            val height = peekHeight
            postedRunnable = { settleToPeekHeight(child, height) }
            child.post { postedRunnable?.invoke() }
        } else {
            settleToPeekHeight(child, peekHeight)
        }
    }

    private fun reset() {
        activePointerId = UNDEFINED
        if (velocityTracker != null) {
            velocityTracker?.recycle()
            velocityTracker = null
        }
    }

    private fun requireVelocityTracker(): VelocityTracker {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        return velocityTracker!!
    }

    private fun findScrollingChild(view: View?): View? {
        var scrollingChild: View? = null
        when {
            view == null -> scrollingChild = null
            isNestedScrollingEnabled(view) -> scrollingChild = view
            else -> {
                when (view) {
                    is ViewPagerFixed -> {
                        val currentViewPagerChild = callback?.getPagerCurrentView()
                        scrollingChild = findScrollingChild(currentViewPagerChild)
                    }

                    is ViewPager2 -> {
                        val currentViewPagerChild = callback?.getPagerCurrentView()
                        scrollingChild = findScrollingChild(currentViewPagerChild)
                    }

                    is ViewGroup -> {
                        var innerScrollingChild: View? = null
                        view.children.forEach { child ->
                            if (innerScrollingChild == null) {
                                innerScrollingChild = findScrollingChild(child)
                                if (innerScrollingChild != null) return@forEach
                            } else return@forEach
                        }
                        return innerScrollingChild
                    }
                }
            }
        }
        return scrollingChild
    }

    // Значение slideOffset изменяется в диапозоне от 0 (панель скрыта) до 1 (панель раскрыта на максимальную высоту)
    private fun calculateSlideOffset(offset: Int): Float {
        val topOffset = if (offset == topOffset) topOffset else 0
        val currentTop = abs(offset) - topOffset
        val slideOffset = currentTop.toFloat() / parentHeight.toFloat()
        return if (slideOffset.isNaN()) 0F else 1 - slideOffset
    }

    private inner class ViewDragHelperCallback : CustomViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return when {
                isLocked -> false
                currentOffset == OFFSET_DRAGGING -> false
                touchingScrollingChild -> false
                else -> {
                    val expandedOffset = if (topDownDirection) offsetList.last() else offsetList.first()
                    if (currentOffset == expandedOffset && activePointerId == pointerId) false
                    else viewRef != null && viewRef?.get() === child
                }
            }
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            val peekHeightAssociated = offsetToPeekHeightMap[top]
            val verticalSlideContinuous = dx == 0 && dy != 0 && peekHeightAssociated == null
            val canDispatch = verticalSlideContinuous || peekHeightAssociated != null
            if (canDispatch) dispatchOnSlide(abs(top))
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == STATE_DRAGGING) setOffsetInternal(OFFSET_DRAGGING)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val top: Int
            val currentTop = abs(releasedChild.top)

            // Если yvel > 0 - скорость положительная, значит движение шторки вниз
            // Когда двигаем шторку вниз, отпустив, попадаем в ближайший больший оффсет
            // Когда двигаем шторку вверх, отпустив, попадаем в ближайший меньший оффсет
            top = offsetList.run {
                // Движение вверх в зависимости от значения topDownDirection считается по разному,
                // дабы правильно находить следующие оффсеты
                val moveUp = if (topDownDirection) yvel > 0f else yvel < 0f
                if (moveUp) findLast { currentTop > it } ?: first()
                else if (yvel == 0f || abs(xvel) > abs(yvel)) {
                    val nearestOffsetUp = findLast { currentTop > it } ?: first()
                    val nearestOffsetDown = find { currentTop <= it } ?: last()
                    if (abs(currentTop - nearestOffsetUp) < abs(currentTop - nearestOffsetDown))
                        nearestOffsetDown
                    else nearestOffsetUp
                } else find { currentTop <= it } ?: last()
            }

            offsetToPeekHeightMap[top]?.let { peekHeight = it }
            startSettlingAnimation(releasedChild, top, true)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val minOffset = offsetList.run {
                // Если список оффсетов пустой, то минимальным значением считаем полное:
                // if (topDownDirection) скрытие, это отрицательное значение высоты родителя
                // else открытие, это значение выставленное в topOffset: отступ от верхней границы
                if (isEmpty()) if (topDownDirection) -parentHeight else topOffset
                // Иначе
                // if (topDownDirection) наибольший заданный оффсет в отрицательном виде
                // else наименьший оффсет
                else if (topDownDirection) last() * -1 else first()
            }
            val maxOffset = offsetList.run {
                // Если список оффсетов пустой, то максимальным значением считаем полное:
                // if (topDownDirection) открытие, это отрицательное значение topOffset
                // else скрытие, это значение высоты родителя
                if (isEmpty()) if (topDownDirection) -topOffset else parentHeight
                // Иначе
                // if (topDownDirection) наименьший оффсет в отрицательном виде
                // else наибольший заданный оффсет
                else if (topDownDirection) first() * -1 else last()
            }
            return MathUtils.clamp(top, minOffset, maxOffset)
        }

        override fun getViewVerticalDragRange(child: View): Int = (offsetList.lastOrNull() ?: topOffset) - topOffset
    }

    private fun settleToPeekHeight(child: View, peekHeight: MovablePanelPeekHeight) {
        val offset = peekHeight.mapToOffset(child.resources)
        if (!animateParentHeightChanges && parentHeightChanged && currentOffset != OFFSET_SETTLING) {
            settleWithoutAnimation(child, offset)
        } else {
            startSettlingAnimation(child, offset, false)
        }
        parentHeightChanged = false
    }

    private var settleRunnable: SettleRunnable? = null

    private fun startSettlingAnimation(child: View, offset: Int, settleFromViewDragHelper: Boolean) {
        val startedSettling = viewDragHelper?.run {
            val correctOffset = calcByDirection(offset)
            when {
                isShowAnimationRunning -> false
                settleFromViewDragHelper -> settleCapturedViewAt(child.left, correctOffset)
                else -> smoothSlideViewTo(child, child.left, correctOffset)
            }
        } ?: false

        if (startedSettling) {
            setOffsetInternal(OFFSET_SETTLING)
            settleRunnable = settleRunnable ?: SettleRunnable(child, offset)
            settleRunnable?.apply {
                if (posted) targetOffset = offset
                else {
                    targetOffset = offset
                    postOnAnimation(child, this)
                    posted = true
                }
            }
        } else {
            setOffsetInternal(offset)
        }
    }

    private fun settleWithoutAnimation(child: View, offset: Int) {
        if (currentOffset == offset) return
        setOffsetInternal(offset)
        changePeekHeightRunning = false
        child.layout(child.left, offset)
    }

    private fun setOffsetInternal(offset: Int) {
        this.currentOffset = offset
        val bottomSheet = viewRef?.get()
        val peekHeightAssociated = offsetToPeekHeightMap[offset]
        dispatchOnSlide(offset)
        nestedScrollingChildRef?.get()?.let { scrollChildToStartIfNeeded(it) }
        if (bottomSheet != null && peekHeightAssociated != null && skipFirstCallback.not()) {
            peekHeight = peekHeightAssociated
            callback?.onHeightChanged(bottomSheet, peekHeightAssociated)
        } else if (skipFirstCallback) skipFirstCallback = false
    }

    private fun dispatchOnSlide(offset: Int) {
        if (isBehaviorMoving(offset)) return
        viewRef?.get()?.apply { callback?.onSlide(this, calculateSlideOffset(offset)) }
    }

    private fun isBehaviorMoving(offset: Int = currentOffset) = offset == OFFSET_DRAGGING || offset == OFFSET_SETTLING

    private fun calcByDirection(offset: Int): Int = offset * if (topDownDirection) -1 else 1

    private fun MovablePanelPeekHeight.mapToOffset(resources: Resources): Int = when (this) {
        is Dimen -> (parentHeight - resources.getDimensionPixelSize(value)).coerceAtLeast(0)
        is Percent -> (parentHeight * (1 - value)).toInt()
        is Absolute -> (parentHeight - value)
        is FitToContent -> (parentHeight - childHeight).coerceAtLeast(0)
    }.let {
        // Когда offset меньше topOffset, панель раскрывается до верхней границы родителя с учетом topOffset,
        // можно задать дополнительный отступ для этого положения - R.styleable.MovablePanelStyle_MovablePanel_topOffset
        if (it <= topOffset) topOffset else it
    }

    private inner class SettleRunnable(private val view: View, offset: Int) : Runnable {
        var posted = false
        var targetOffset = offset
        override fun run() {
            if (viewDragHelper?.continueSettling(true) == true) {
                postOnAnimation(view, this)
            } else {
                setOffsetInternal(targetOffset)
                changePeekHeightRunning = false
            }
            posted = false
        }
    }

    companion object {
        private const val OFFSET_DRAGGING = 1111111
        private const val OFFSET_SETTLING = 2222222
        private const val UNDEFINED = -1

        /**
         * Минимальная продолжительность анимации показа,
         * тк нужен следующий run loop для игнорирования системной анимации [ViewDragHelper].
         */
        private const val MIN_ANIMATION_DURATION = 1
    }
}
