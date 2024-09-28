package ru.tensor.sbis.swipeablelayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.*
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.withStyledAttributes
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.doOnAttach
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.tracing.trace
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeAtMostSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.design.utils.DragDirection
import ru.tensor.sbis.design.utils.DraggableView
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.dpToPx
import ru.tensor.sbis.design.utils.errorSafe
import ru.tensor.sbis.design.utils.extentions.getActivity
import ru.tensor.sbis.design.utils.findViewParent
import ru.tensor.sbis.design.utils.viewdraghelper.ViewDragHelper
import ru.tensor.sbis.design.view_ext.gesture.SimpleOnGestureListenerCompat
import ru.tensor.sbis.swipeable_layout.R
import ru.tensor.sbis.swipeablelayout.api.Closed
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.DismissedWithTimeout
import ru.tensor.sbis.swipeablelayout.api.DismissedWithoutMessage
import ru.tensor.sbis.swipeablelayout.api.Dismissing
import ru.tensor.sbis.swipeablelayout.api.Dragging
import ru.tensor.sbis.swipeablelayout.api.ItemInListChecker
import ru.tensor.sbis.swipeablelayout.api.MenuOpened
import ru.tensor.sbis.swipeablelayout.api.MenuOpening
import ru.tensor.sbis.swipeablelayout.api.SwipeEvent
import ru.tensor.sbis.swipeablelayout.api.SwipeEventListener
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType.CANCELLABLE
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType.DISMISS_IMMEDIATE
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType.DISMISS_WITHOUT_MESSAGE
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType.LOCKED
import ru.tensor.sbis.swipeablelayout.api.SwipeMenuSide
import ru.tensor.sbis.swipeablelayout.api.SwipeableLayoutApi
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.util.*
import ru.tensor.sbis.swipeablelayout.util.swipestate.AdapterPosition
import ru.tensor.sbis.swipeablelayout.util.swipestate.NoId
import ru.tensor.sbis.swipeablelayout.util.swipestate.PositionInParent
import ru.tensor.sbis.swipeablelayout.util.swipestate.SwipeItemId
import ru.tensor.sbis.swipeablelayout.util.swipestate.SwipeListVm
import ru.tensor.sbis.swipeablelayout.util.swipestate.Uuid
import ru.tensor.sbis.swipeablelayout.view.SwipeContainerLayout
import ru.tensor.sbis.swipeablelayout.view.SwipeMenuItemView
import ru.tensor.sbis.swipeablelayout.view.canvas.SwipeDismissMessageLayout
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool
import timber.log.Timber
import java.util.EnumSet
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import ru.tensor.sbis.swipeablelayout.DismissListener as NewDismissListener

private const val LOCKED_SWIPE_PULL_LIMIT_DP = 80
private const val FLING_VELOCITY_THRESHOLD_DP = 100
private const val DISMISS_FLING_VELOCITY_THRESHOLD_DP = 3750
private const val SMALL_SCREEN_DISMISS_FLING_VELOCITY_THRESHOLD_DP = 2000
private const val SMALL_SCREEN_WIDTH_DP = 320
private const val MENU_OVERPULL_AMOUNT_TO_DISMISS_DP = 48
private const val LAST_MENU_ITEM_REST_DURATION = 100L
private const val DISMISS_MESSAGE_APPEAR_ANIM_DURATION = 300L
private const val MAX_SETTLE_ANIMATION_DURATION = 150
private const val SWIPE_STATE_SAVING_EVENT_LISTENER = "SWIPE_STATE_SAVING_EVENT_LISTENER"

/**
 * View с поддержкой отображения свайп-меню и смахивания (swipe-to-dismiss)
 * Если задано меню, то оно отображается поверх содержимого, и при смахивании полностью перекрывает
 * его. В противном случае смахивается непосредственно содержимое
 *
 * Стандарт: <a href="http://axure.tensor.ru/MobileStandart8/#p=%D1%81%D0%B2%D0%B0%D0%B9%D0%BF_%D0%B8_%D1%81%D0%BC%D0%B0%D1%85%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_2_&g=1">Свайп и смахивание]</a>
 *
 * @see [SwipeableLayoutApi]
 * @see [SwipeMenuItemView]
 * @see [SwipeHelper]
 *
 * @author us.bessonov
 */
class SwipeableLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.swipeableLayoutTheme
) : ViewGroup(
    ThemeContextBuilder(context, attrs, defStyleAttr, R.style.DefaultSwipeableLayoutTheme).build(), attrs, defStyleAttr
), SwipeableLayoutApi, DraggableView {

    private var nextListenerId = 0

    /**
     * Состояние свайпа
     */
    companion object State {
        const val MENU_OPENING: Int = 0
        const val MENU_OPEN: Int = 1
        const val CLOSING: Int = 2
        const val CLOSED: Int = 3
        const val DISMISSING: Int = 4
        const val DISMISSED_WITHOUT_MESSAGE: Int = 5
        const val DISMISSED: Int = 6
        const val DRAGGING: Int = 7
        const val DISMISSED_WITH_TIMEOUT: Int = 8
    }

    /**
     * Слушатель события изменения состояния
     */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("ru.tensor.sbis.swipeablelayout.api.SwipeEventListener")
    )
    interface StateChangeListener {
        /**
         * Вызывается при изменении состояния в результате выполнения жестов, либо вызовов [open],
         * [close] или [dismiss] с параметром true
         *
         * @param state новое состояние View
         */
        fun onStateChanged(state: Int)
    }

    /**
     * Слушатель события завершения смахивания элемента
     */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("ru.tensor.sbis.swipeablelayout.api.SwipeEventListener")
    )
    interface DismissListener {
        /**
         * Вызывается по завершении смахивания, т.е. после того как содержимое было полностью скрыто,
         * и отобразилось сообщение об удалении
         */
        fun onDismissed()
    }

    /**
     * Слушатель события скрытия содержимого при смахивании
     */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("ru.tensor.sbis.swipeablelayout.api.SwipeEventListener")
    )
    interface DismissWithoutMessageListener {
        /**
         * Вызывается после того как содержимое было полностью скрыто, но сообщение об удалении
         * ещё не отобразилось
         */
        fun onDismissedWithoutMessage(uuid: String? = null)
    }

    /**
     * Слушатель события начала открытия меню. Это происходит после интенсивного свайпа, отпускания
     * меню, открытого более чем наполовину, либо вызова [open] с параметром true
     */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("ru.tensor.sbis.swipeablelayout.api.SwipeEventListener")
    )
    interface MenuOpeningStartListener {
        /** @SelfDocumented */
        fun onMenuOpeningStart()
    }

    // region Private properties
    private val hasMenu: Boolean
        get() = hasRightMenu || hasLeftMenu
    private val hasLeftMenu: Boolean
        get() = leftMenuContainerNullable?.hasMenu ?: false
    private val hasRightMenu: Boolean
        get() = rightMenuContainer.hasMenu
    private val rightMenuHasRemoveOption: Boolean
        get() = rightMenuContainer.hasRemoveOption
    private val leftMenuHasRemoveOption: Boolean
        get() = leftMenuContainerNullable?.hasRemoveOption ?: false

    private var isForcedDragLock = false
    private var areViewsInitialized = false
    private var isRightMenuDisabled = false
    private var isLeftMenuDisabled = true

    private lateinit var content: View
    private val rightMenuContainer = createMenuContainer(getContext(), attrs)
    private var leftMenuContainerNullable: SwipeContainerLayout? = null
    private val leftMenuContainer: SwipeContainerLayout
        get() = leftMenuContainerNullable ?: createMenuContainer(context, null)
            .also {
                if (areViewsInitialized) addView(it)
                leftMenuContainerNullable = it
            }
    private val rightDismissMessageLayout = createDismissMessageLayout(getContext(), attrs)
    private val leftDismissMessageLayout = createDismissMessageLayout(getContext(), attrs)
    private var parentDrawerLayout: DrawerLayout? = null

    private val cancelDeletionMessage = resources.getString(R.string.swipeable_layout_cancel_deletion_message)

    @ColorInt
    private var menuDismissMessageTextColor = Color.TRANSPARENT

    @ColorInt
    private var contentDismissMessageTextColor = Color.TRANSPARENT

    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop * 3
    private var minDistRequestDisallowParent = 0

    private var isCurrentlyOnLayout = false
    private var isAborted: Boolean = false
    private var isDragged: Boolean = false
    private var isScrolling: Boolean = false
    private var prevX: Float = 0f
    private var lastEventX = 0f

    private var contentDefaultLeft = 0
    private var contentDismissedLeft = 0
    private var contentSwipeLockedLeft = 0

    private var rightMenuClosedLeft = 0
    private var rightMenuOpenLeft = 0
    private var rightMenuAtLastItemLeft = 0
    private var rightMenuDismissedLeft = 0

    private var leftMenuClosedLeft = 0
    private var leftMenuOpenLeft = 0
    private var leftMenuOpenThreshold = 0
    private var leftMenuDismissedLeft = 0

    private var rightMenuOpenThreshold = 0
    private var rightMenuDismissThreshold = 0
    private var contentDismissThreshold = 0
    private val dismissFlingVelocityThreshold = getDismissFlingVelocityThreshold()
    private val flingVelocityThreshold = dpToPx(FLING_VELOCITY_THRESHOLD_DP)

    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val mAlphaInterpolator = AccelerateInterpolator()

    internal val eventListeners = mutableMapOf<String, SwipeEventListener>()

    private val tempRect = Rect()

    private var onAttachedToWindowAction: (() -> Unit)? = null
    private var onDetachedFromWindowAction: (() -> Unit)? = null
    internal var onDismissListener: NewDismissListener? = null

    private var actionAfterNextCloseAnimationEnded: (() -> Unit)? = null

    private var parentRecyclerView: RecyclerView? = null

    private val gestureListener: GestureDetector.OnGestureListener =
        object : SimpleOnGestureListenerCompat {
            private var hasDisallowed = false

            override fun onDown(e: MotionEvent): Boolean {
                isScrolling = false
                hasDisallowed = false
                return true
            }

            override fun onFlingCompat(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                isScrolling = true
                return false
            }

            override fun onScrollCompat(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                isScrolling = true

                if (parent != null) {
                    val shouldDisallow: Boolean

                    if (!hasDisallowed) {
                        shouldDisallow = getDistToClosestEdge() >= minDistRequestDisallowParent
                        if (shouldDisallow) {
                            hasDisallowed = true
                        }
                    } else {
                        shouldDisallow = true
                    }

                    parent.requestDisallowInterceptTouchEvent(shouldDisallow)
                }

                return false
            }

            override fun onSingleTapConfirmed(e: MotionEvent) =
                if (e.isInMenuArea()) {
                    false
                } else {
                    performClick()
                }

            private fun getDistToClosestEdge(): Int = when {
                isRightMenuMoved() -> min(
                    abs(rightMenuContainer.left - rightMenuOpenLeft), abs(rightMenuContainer.left - rightMenuClosedLeft)
                )

                isLeftMenuMoved() -> min(
                    abs(leftMenuContainer.left - leftMenuOpenLeft), abs(leftMenuContainer.left - leftMenuClosedLeft)
                )

                else -> abs(contentDefaultLeft - content.left)
            }

            private fun MotionEvent.isInMenuArea() = hasRightMenu && x > rightMenuContainer.left
                || hasLeftMenu && x < leftMenuContainer.left + leftMenuContainer.measuredWidth
        }

    private val dragHelperCallback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            isAborted = false

            if (cannotDrag()) {
                return false
            }

            updateStateValue(DRAGGING)

            captureViewAccordingToMode(pointerId)

            return false
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            if (!hasMenu && !isSwipeToDismissLocked) {
                changeContentAlpha(getContentOffset(changedView))
            }
            ViewCompat.postInvalidateOnAnimation(this@SwipeableLayout)
        }

        private fun getContentOffset(view: View): Float {
            return abs(content.left - contentDefaultLeft).toFloat() / getHorizontalDragRange(view)
        }

        override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
            if (cannotDrag()) {
                return
            }

            captureViewAccordingToMode(pointerId)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) = when (releasedChild) {
            rightMenuContainer -> onRightMenuReleased(xvel)
            leftMenuContainerNullable -> onLeftMenuReleased(xvel)
            else -> onContentReleased(xvel)
        }

        override fun getViewHorizontalDragRange(child: View) = getHorizontalDragRange(child)

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            val maxLeft = when (child) {
                rightMenuContainer -> rightMenuClosedLeft
                leftMenuContainerNullable -> leftMenuOpenLeft
                else -> contentDefaultLeft
            }
            return if (hasMenu || !shouldSwipeContentToRight) {
                max(min(left, maxLeft), maxLeft - getHorizontalDragRange(child))
            } else {
                max(min(left, maxLeft + getHorizontalDragRange(child)), maxLeft)
            }
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int) = child.top

        override fun onViewDragStateChanged(state: Int) {
            if (state != ViewDragHelper.STATE_IDLE) {
                return
            }

            if (!isRightMenuMoved() && !isLeftMenuMoved()) {
                actionAfterNextCloseAnimationEnded?.invoke()
                actionAfterNextCloseAnimationEnded = null
            }

            if (isAborted) return

            when (dragHelper.capturedView) {
                rightMenuContainer -> when (rightMenuContainer.left) {
                    rightMenuAtLastItemLeft -> dismissDelayed()
                    rightMenuDismissedLeft -> showDismissMessageIfAllowedTo(rightDismissMessageLayout)
                    rightMenuOpenLeft -> updateStateValue(MENU_OPEN)
                    rightMenuClosedLeft -> updateStateValue(CLOSED)
                }

                leftMenuContainerNullable -> when (leftMenuContainer.left) {
                    leftMenuDismissedLeft -> showDismissMessageIfAllowedTo(leftDismissMessageLayout)
                    leftMenuOpenLeft -> updateStateValue(MENU_OPEN, SwipeMenuSide.LEFT)
                    leftMenuClosedLeft -> updateStateValue(CLOSED)
                }

                else -> when (content.left) {
                    contentDismissedLeft -> showDismissMessageIfAllowedTo(rightDismissMessageLayout)
                    contentDefaultLeft -> updateStateValue(CLOSED)
                }
            }
        }

        private fun onRightMenuReleased(xvel: Float) {
            val swipeDistance = calculateSwipeDistance(rightMenuContainer)
            val isSwipedToDismiss =
                xvel <= -dismissFlingVelocityThreshold && swipeDistance >= getRightMenuWidth() / 2
            when {
                rightMenuHasRemoveOption && isSwipedToDismiss && !isSwipeToDismissLocked -> if (state == MENU_OPEN) {
                    slideToLastMenuItemAndThenDismiss()
                } else {
                    dismiss()
                }

                xvel <= -flingVelocityThreshold -> openMenu()
                xvel >= flingVelocityThreshold -> close()
                rightMenuHasRemoveOption && rightMenuContainer.left < rightMenuDismissThreshold -> {
                    slideToLastMenuItemAndThenDismiss()
                }

                rightMenuContainer.left < rightMenuOpenThreshold -> openMenu()
                else -> close()
            }
        }

        private fun onLeftMenuReleased(xvel: Float) {
            val swipeDistance = calculateSwipeDistance(leftMenuContainer)
            val isSwipedToDismiss =
                xvel >= dismissFlingVelocityThreshold && swipeDistance >= getLeftMenuWidth() / 2
            when {
                isLeftDismissalSupported() && isSwipedToDismiss -> dismissLeft()
                xvel <= -flingVelocityThreshold -> close()
                xvel >= flingVelocityThreshold -> openLeftMenu()
                leftMenuContainer.left > leftMenuOpenThreshold -> openLeftMenu()
                else -> close()
            }
        }

        private fun onContentReleased(xvel: Float) {
            val isVelocityThresholdExceeded = if (!shouldSwipeContentToRight) {
                xvel <= -dismissFlingVelocityThreshold
            } else {
                xvel >= dismissFlingVelocityThreshold
            }
            val isPositionThresholdExceeded = if (!shouldSwipeContentToRight) {
                content.left < contentDismissThreshold
            } else {
                content.left > contentDismissThreshold
            }
            when {
                !isSwipeToDismissLocked && (isVelocityThresholdExceeded || isPositionThresholdExceeded) -> dismiss()
                else -> returnContentToDefaultPosition()
            }
        }

        private fun calculateSwipeDistance(releasedView: View): Int =
            abs(content.width - (releasedView.left - contentDefaultLeft))

        private fun getHorizontalDragRange(view: View) = when {
            view == rightMenuContainer -> getRightMenuWidth()
            view == leftMenuContainerNullable -> getLeftMenuWidth()
            !hasMenu && isSwipeToDismissLocked -> abs(contentDefaultLeft - contentSwipeLockedLeft)
            else -> content.width
        }

        private fun captureViewAccordingToMode(pointerId: Int) {
            dragHelper.captureChildView(
                when {
                    hasLeftMenu && shouldCaptureLeftMenu() -> leftMenuContainer
                    hasRightMenu -> rightMenuContainer
                    else -> content
                }, pointerId
            )
        }

        private fun shouldCaptureLeftMenu() = !hasRightMenu || isLeftMenuOpened()
            || (!isRightMenuOpened() && lastEventX < centerOf(left, right)
            && (parentDrawerLayout == null || lastEventX > dragHelper.edgeSize))

    }

    private val gestureDetector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(
            context,
            gestureListener,
            mHandler
        )
    }
    private val dragHelper: ViewDragHelper = ViewDragHelper.create(this, 1.0f, dragHelperCallback).apply {
        setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_RIGHT)
        setMaxSettleDuration(MAX_SETTLE_ANIMATION_DURATION)
    }

    private val onRecyclerViewItemTouchListener =
        ItemTouchListenerForForcedEventHandling(this, onInterceptMissedDownEvent = { downEvent, followingEvent ->
            prevX = downEvent.x
            if (checkTouchSlop(followingEvent)) {
                onInterceptTouchEvent(downEvent)
                onInterceptTouchEvent(followingEvent)
                return@ItemTouchListenerForForcedEventHandling true
            }
            false
        }, onTouchEvent = { onInterceptTouchEvent(it) })
    // endregion

    /** @SelfDocumented */
    internal var state = CLOSED

    /**
     * Время начала отсчёта таймаута до подтверждения удаления элемента, который смахнули
     */
    internal var dismissalTime = 0L

    /** @SelfDocumented */
    internal var swipeListVm: SwipeListVm? = null

    override val supportedDragDirections: EnumSet<DragDirection>
        get() = when {
            isDragLocked -> EnumSet.noneOf(DragDirection::class.java)
            isRightMenuOpened() || hasLeftMenu -> EnumSet.of(DragDirection.RIGHT)
            else -> EnumSet.of(DragDirection.LEFT)
        }

    // region API properties
    override var itemUuid: String? = null

    override var lastEvent: SwipeEvent = Closed(null)
        private set

    override var cornerRadius: BorderRadius? = null
        set(value) {
            field = value
            updateViewOutlineProvider()
        }

    override var isDragLocked = false
        set(value) {
            field = value
            isRightMenuDisabled = false
        }

    override var shouldSwipeContentToRight = false
        set(value) {
            if (field == value) return
            field = value
            if (areViewsInitialized) {
                val lp = content.layoutParams
                val leftMargin: Int
                val rightMargin: Int
                if (lp is MarginLayoutParams) {
                    leftMargin = lp.leftMargin
                    rightMargin = lp.rightMargin
                } else {
                    leftMargin = 0
                    rightMargin = 0
                }
                initSignificantPositions(leftMargin, rightMargin)
                close(animated = false)
            }
        }

    override var itemDismissType = SwipeItemDismissType.NONE
        set(value) {
            field = value
            val hasRemoveOption = itemDismissType != SwipeItemDismissType.NONE
            rightMenuContainer.hasRemoveOption = hasRemoveOption
            leftMenuContainer.hasRemoveOption = hasRemoveOption
            setDefaultCancellableDismissMessageIfNeeded(value)
        }
    // endregion

    // region Deprecated properties
    /** @SelfDocumented */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("addEventListener()")
    )
    var stateChangeListener: StateChangeListener? = null

    /** @SelfDocumented */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("addEventListener()")
    )
    var dismissListener: DismissListener? = null
        set(value) {
            field = value
            onDismissListener = NewDismissListener { field?.onDismissed() }
        }

    /** @SelfDocumented */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("addEventListener()")
    )
    var dismissWithoutMessageListener: DismissWithoutMessageListener? = null

    /** @SelfDocumented */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("addEventListener()")
    )
    var menuOpeningStartListener: MenuOpeningStartListener? = null

    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("SwipeItemDismissType.LOCKED")
    )
    var isSwipeToDismissLocked: Boolean
        get() = itemDismissType == LOCKED
        set(value) {
            if (value) itemDismissType = LOCKED
        }
    // endregion

    init {
        setWillNotDraw(false)
        getContext().withStyledAttributes(attrs, R.styleable.SwipeableLayout) {
            getString(R.styleable.SwipeableLayout_dismissMessage)?.let(::setDismissMessage)
            menuDismissMessageTextColor = getColor(
                R.styleable.SwipeableLayout_SwipeableLayout_menuDismissMessageTextColor, menuDismissMessageTextColor
            )
            contentDismissMessageTextColor = getColor(
                R.styleable.SwipeableLayout_SwipeableLayout_contentDismissMessageTextColor,
                contentDismissMessageTextColor
            )
        }
        updateViewOutlineProvider()
    }

    // region API implementation
    /**
     * Позволяет получать уведомления о событиях конкретного типа (например, только для отслеживания удаления
     * элемента по смахиванию).
     *
     * @param listenerId Идентификатор обработчика. По умолчанию, для всех обработчиков событий одного типа
     * идентификатор одинаковый. То есть, очередной обработчик того же типа перезаписывает предыдущий. Если это не
     * требуется, нужно задать `null`.
     *
     * @return [SwipeEventListener], который фактически будет установлен
     */
    inline fun <reified EVENT : SwipeEvent> addSwipeEventListener(
        listenerId: String? = "${EVENT::class.java.simpleName}_listener",
        crossinline listener: (EVENT) -> Unit
    ): SwipeEventListener {
        val actualListener: SwipeEventListener = { if (it is EVENT) listener(it) }
        addEventListener(listenerId, actualListener)
        return actualListener
    }

    override fun addEventListener(listenerId: String?, listener: SwipeEventListener) {
        val id = listenerId ?: "default_id_${nextListenerId++}"
        eventListeners[id] = listener
    }

    override fun removeEventListener(listener: SwipeEventListener) {
        eventListeners.entries
            .find { it.value == listener }
            ?.let { eventListeners.remove(it.key) }
    }

    override fun removeEventListener(listenerId: String) {
        eventListeners.remove(listenerId)
    }

    override fun setItemInListChecker(isItemInList: ItemInListChecker?) {
        withSwipeListVm {
            setItemInListChecker(isItemInList)
        }
    }

    override fun setDismissMessage(dismissMessage: String) {
        rightDismissMessageLayout.setDismissMessage(dismissMessage)
        leftDismissMessageLayout.setDismissMessage(dismissMessage)
    }

    override fun releaseListeners() {
        stateChangeListener = null
        eventListeners.clear()
        dismissWithoutMessageListener = null
        dismissListener = null
        onDetachedFromWindowAction?.invoke() // потенциальный вызов removeOnPropertyChangedCallback
        onAttachedToWindowAction = null
        onDetachedFromWindowAction = null
        actionAfterNextCloseAnimationEnded = null
    }

    override fun openMenu(animated: Boolean) {
        if (!hasRightMenu) return

        openMenu(SwipeMenuSide.RIGHT, rightMenuContainer, rightMenuOpenLeft, animated)
    }

    override fun openLeftMenu(animated: Boolean) {
        if (!hasLeftMenu) return

        openMenu(SwipeMenuSide.LEFT, leftMenuContainer, leftMenuOpenLeft, animated)
    }

    override fun close(animated: Boolean) {
        isAborted = false
        isForcedDragLock = false

        setDismissMessageVisible(false)

        if (!areViewsInitialized) {
            updateStateValue(CLOSED)
            return
        }

        if (animated) {
            updateStateValue(CLOSING)
            abort()
            performClose { slideTo(it) }
        } else {
            updateStateValue(CLOSED)
            abort()
            if (canAdjustChildPosition()) {
                performClose { moveTo(it) }
            }
            if (!hasMenu) resetContentAlpha()
        }

        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun dismiss(animated: Boolean) {
        performDismiss(rightMenuContainer, rightDismissMessageLayout, rightMenuDismissedLeft, animated)
    }

    override fun dismissLeft(animated: Boolean) {
        leftMenuContainerNullable?.let {
            performDismiss(it, leftDismissMessageLayout, leftMenuDismissedLeft, animated)
        }
    }

    override fun <ITEM : SwipeMenuItem> setMenu(items: List<ITEM>) {
        isRightMenuDisabled = items.isEmpty()
        rightMenuContainer.setMenu(items)
        placeRightDismissMessageLayout(rightMenuContainer, !isRightMenuDisabled)
        getSwipeListVm()?.restoreState(this)
    }

    override fun <ITEM : SwipeMenuItem> setLeftMenu(items: List<ITEM>) {
        isLeftMenuDisabled = items.isEmpty()
        leftMenuContainer.setMenu(items)
        leftMenuContainer.isVisible = !isLeftMenuDisabled
        placeLeftDismissMessageLayout(leftMenuContainer)
        getSwipeListVm()?.restoreState(this)
    }
    // endregion

    // region Deprecated API implementation
    /**
     * @return открыто ли сейчас меню
     */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("lastEvent is MenuOpened")
    )
    fun isMenuOpen() = state == MENU_OPEN

    /**
     * @return выполняется ли сейчас открытие меню
     */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("lastEvent is MenuOpening")
    )
    fun isMenuOpening() = state == MENU_OPENING

    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("SwipeMenuItemsContainer.setMenu()")
    )
    fun <ITEM : MenuItem> setMenu(menu: SwipeMenu<ITEM>) {
        isRightMenuDisabled = menu.items.isEmpty()
        rightMenuContainer.setMenu(menu)
        placeRightDismissMessageLayout(rightMenuContainer, !isRightMenuDisabled)
        getSwipeListVm()?.restoreState(this)
    }

    /**
     * Указывает, присутствует ли среди пунктов меню опция удаления
     */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("itemDismissType")
    )
    fun setHasRemoveOption(hasRemoveOption: Boolean) {
        itemDismissType = DISMISS_IMMEDIATE
    }

    /**
     * Устанавливает обработчик события смахивания элемента.
     *
     * @param itemUuid идентификатор модели удаляемого элемента, доступный в обработчике
     */
    @JvmOverloads
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("addEventListener()")
    )
    fun setDismissListener(itemUuid: String? = null, listener: NewDismissListener) {
        initDismissListener(itemUuid, listener)
    }

    /**
     * Устанавливает обработчик события смахивания элемента, которое может быть отменено.
     * В отличие от [setDismissListener], [listener] будет вызываться не сразу после смахивания, а по истечении
     * [DISMISSAL_TIMEOUT_MS], если к тому времени пользователь не кликнет по элементу, либо не будет явно вызван метод
     * [close].
     * [listener] ОБЯЗАТЕЛЬНО должен учитывать доступный в нём uuid при удалении, т.к. не гарантируется что ему будет
     * передан именно [itemUuid]
     *
     * @param itemUuid идентификатор модели в списке, с которой ассоциируется [SwipeableLayout]
     */
    @Deprecated(
        "Будет удалено по https://dev.sbis.ru/opendoc.html?guid=44db4631-3eac-4032-b251-321cbfcfe7ad&client=3",
        ReplaceWith("addEventListener() и SwipeItemDismissType.CANCELLABLE")
    )
    fun setCancellableDismissListener(itemUuid: String?, listener: NewDismissListener) {
        initDismissListener(itemUuid, listener, true)
        setDismissMessage(cancelDeletionMessage)
    }
    // endregion

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = trace("SwipeableLayout#onMeasure") {
        ensureProperChildViews()

        val lp = content.layoutParams
        val leftMargin: Int
        val topMargin: Int
        val rightMargin: Int
        val bottomMargin: Int
        if (lp is MarginLayoutParams) {
            measureChildWithMargins(content, widthMeasureSpec, 0, heightMeasureSpec, 0)
            leftMargin = lp.leftMargin
            topMargin = lp.topMargin
            rightMargin = lp.rightMargin
            bottomMargin = lp.bottomMargin
        } else {
            measureChild(content, widthMeasureSpec, heightMeasureSpec)
            leftMargin = 0
            topMargin = 0
            rightMargin = 0
            bottomMargin = 0
        }
        val measuredWidth = content.measuredWidth + paddingLeft + paddingRight + leftMargin + rightMargin
        val measuredHeight = content.measuredHeight + paddingTop + paddingBottom + topMargin + bottomMargin
        if (!hasMenu) {
            rightDismissMessageLayout.measure(makeExactlySpec(measuredWidth), makeExactlySpec(measuredHeight))
        }
        measureChild(rightMenuContainer, makeAtMostSpec(measuredWidth), makeExactlySpec(measuredHeight))
        if (hasLeftMenu) {
            measureChild(leftMenuContainer, makeAtMostSpec(measuredWidth), makeExactlySpec(measuredHeight))
        }
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) = trace("SwipeableLayout#onLayout") {
        isCurrentlyOnLayout = true
        isAborted = false

        val lp = content.layoutParams
        val leftMargin: Int
        val topMargin: Int
        val rightMargin: Int
        val bottomMargin: Int
        if (lp is MarginLayoutParams) {
            leftMargin = lp.leftMargin
            topMargin = lp.topMargin
            rightMargin = lp.rightMargin
            bottomMargin = lp.bottomMargin
        } else {
            leftMargin = 0
            topMargin = 0
            rightMargin = 0
            bottomMargin = 0
        }

        val leftPos = paddingLeft + leftMargin
        val topPos = paddingTop + topMargin
        val rightPos = right - left - paddingRight - rightMargin
        val bottomPos = bottom - top - paddingBottom - bottomMargin

        content.layout(leftPos, topPos, rightPos, bottomPos)
        if (hasRightMenu || rightMenuContainer.left != rightPos) {
            rightMenuContainer.layout(
                rightPos + leftMargin,
                topPos,
                rightPos + rightMenuContainer.measuredWidth,
                bottomPos
            )
        }
        if (hasLeftMenu) {
            leftMenuContainer.layout(leftPos - leftMenuContainer.measuredWidth, topPos, leftPos, bottomPos)
        }
        if (!hasMenu) rightDismissMessageLayout.layout(leftPos, topPos)

        initSignificantPositions(leftMargin, rightMargin)
        applyInitialState()

        isCurrentlyOnLayout = false
    }

    override fun onDraw(canvas: Canvas) {
        if (!hasMenu) rightDismissMessageLayout.draw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onAttachedToWindowAction?.invoke()
        parentDrawerLayout = findViewParent(this)

        parentRecyclerView = findParentRecyclerView()?.apply {
            /*
            После прокрутки RecyclerView до конца, его touch event'ы недоступны до скрытия индикатора прокрутки.
            В этом случае применяется RecyclerView.OnItemTouchListener, который получает все события
            */
            addOnItemTouchListener(onRecyclerViewItemTouchListener)
            setSwipeListAdapterChangesObserver(this, cleanUpOnRecyclerDetached = {
                setItemInListChecker(null)
            })
        }

        observeHostOnStopEvent()

        CloseOnOutsideTouchEventInterceptor.attach(this)

        SwipeHelper.onAttached(this)
        initStateSaving()
    }

    override fun onDetachedFromWindow() {
        CloseOnOutsideTouchEventInterceptor.detach(this)
        parentRecyclerView?.removeOnItemTouchListener(onRecyclerViewItemTouchListener)
        parentRecyclerView = null
        super.onDetachedFromWindow()
        onDetachedFromWindowAction?.invoke()
        parentDrawerLayout = null
        SwipeHelper.onDetached(this)
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (shouldIgnoreEvent(event)) return false
        gestureDetector.onTouchEvent(event)
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (shouldIgnoreEvent(ev)) return false
        if (ev.action == MotionEvent.ACTION_DOWN) {
            onRecyclerViewItemTouchListener.onViewDownEventReceived()
        }
        preventDrawerOpeningIfNeeded(ev)
        processTouchEvent(ev)
        if (ev.isMatchingUpEventOutsideWhenMenuOpen()) {
            close()
        }
        return checkTouchSlop(ev)
    }

    override fun setMenuItemViewPool(menuViewPool: SwipeMenuViewPool?) {
        leftMenuContainerNullable?.setMenuItemViewPool(menuViewPool)
        rightMenuContainer.setMenuItemViewPool(menuViewPool)
    }

    /**
     * Задаёт лямбду, выполняемую после присоединения View к окну
     */
    internal fun doOnAttachedToWindow(action: () -> Unit) {
        onAttachedToWindowAction = action
    }

    /**
     * Задаёт лямбду, выполняемую после отсоединения View от окна
     */
    internal fun doOnDetachedFromWindow(action: () -> Unit) {
        onDetachedFromWindowAction = action
    }

    /** @SelfDocumented */
    internal fun doAfterNextCloseAnimationEnded(action: () -> Unit) {
        actionAfterNextCloseAnimationEnded = action
    }

    /**
     * Возвращает доступный идентификатор элемента для сохранения/восстановления состояния
     */
    internal fun getItemId(): SwipeItemId {
        return itemUuid?.let { Uuid(it) } ?: parentRecyclerView?.let(::getRecyclerViewItemId)
        ?: (parent as ViewGroup?)?.indexOfChild(this)?.takeIf { it >= 0 }?.let { PositionInParent(it) } ?: NoId
    }

    /**
     * Устанавливает состояние свайпа в [DISMISSED_WITHOUT_MESSAGE], без анимации
     */
    internal fun setDismissedWithoutMessage() {
        updateStateValue(DISMISSED_WITHOUT_MESSAGE, notifyChanged = false)
        dragHelper.abort()

        isForcedDragLock = true

        moveToDismissedPosition()
    }

    /**
     * Установить состояние, в котором элемент удалён, но может быть восстановлен.
     */
    internal fun setDismissedWithTimeout() {
        updateStateValue(DISMISSED_WITH_TIMEOUT, notifyChanged = false)
        dragHelper.abort()

        isForcedDragLock = true
        setDismissMessageVisible(true)
        updateDismissMessageTextColor()

        moveToDismissedPosition()
    }

    /**
     * @see [SwipeListVm.forceDismissItemsWithTimeout]
     */
    internal fun forceDismissItemsWithTimeout() = getSwipeListVm()?.forceDismissItemsWithTimeout()

    // region Private methods
    private fun moveToDismissedPosition() {
        when {
            hasRightMenu -> rightMenuContainer.moveTo(rightMenuDismissedLeft)
            !hasMenu && canAdjustChildPosition() -> content.moveTo(contentDismissedLeft)
        }
    }

    /**
     * Прерывает текущую анимацию движения
     */
    private fun abort() {
        isAborted = true
        dragHelper.abort()
    }

    private fun getSwipeListVm(): SwipeListVm? =
        swipeListVm ?: (getSwipeListVm(this) ?: parentRecyclerView?.let { getSwipeListVm(it) })?.also {
            swipeListVm = it
        }

    private fun withSwipeListVm(action: SwipeListVm.() -> Unit) = doOnAttach { getSwipeListVm()?.action() }

    private fun initStateSaving() {
        val vm = getSwipeListVm() ?: return
        vm.restoreState(this)
        addEventListener(SWIPE_STATE_SAVING_EVENT_LISTENER) {
            vm.onSwipeEvent(this)
        }
    }

    private fun createMenuContainer(context: Context, attrs: AttributeSet?) =
        SwipeContainerLayout(context, attrs).apply {
            setPadding(0, 0, 0, 0)
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            setContentOverlayClickListener {
                if (itemDismissType == CANCELLABLE && (state == DISMISSED_WITH_TIMEOUT || state == DISMISSED_WITHOUT_MESSAGE)) {
                    cancelDismissalTimeout()
                }
            }
        }

    private fun createDismissMessageLayout(context: Context, attrs: AttributeSet?) =
        SwipeDismissMessageLayout(context, attrs)


    private fun initSignificantPositions(leftMargin: Int, rightMargin: Int) {
        contentDefaultLeft = content.left
        if (!shouldSwipeContentToRight) {
            contentDismissedLeft = contentDefaultLeft - content.width + rightMargin
            contentSwipeLockedLeft = contentDefaultLeft - dpToPx(LOCKED_SWIPE_PULL_LIMIT_DP)
        } else {
            contentDismissedLeft = contentDefaultLeft + content.width - leftMargin
            contentSwipeLockedLeft = contentDefaultLeft + dpToPx(LOCKED_SWIPE_PULL_LIMIT_DP)
        }
        contentDismissThreshold = (contentDismissedLeft + contentDefaultLeft) / 2

        if (!hasMenu) return

        rightMenuClosedLeft = content.right + rightMargin
        rightMenuOpenLeft = rightMenuClosedLeft - rightMenuContainer.menuItemsLayout.right
        rightMenuAtLastItemLeft = getRightMenuItemRemove()?.let { contentDefaultLeft - it.left } ?: 0
        rightMenuDismissedLeft = -rightMenuContainer.menuItemsLayout.right + paddingLeft

        val leftMenuWidth = leftMenuContainerNullable?.menuItemsLayout?.width ?: 0
        val leftContainerWidth = leftMenuContainerNullable?.width ?: 0
        leftMenuClosedLeft = content.left - leftContainerWidth
        leftMenuOpenLeft = leftMenuClosedLeft + leftMenuWidth
        leftMenuDismissedLeft = content.left

        rightMenuOpenThreshold = centerOf(rightMenuClosedLeft, rightMenuOpenLeft)
        leftMenuOpenThreshold = centerOf(leftMenuClosedLeft, leftMenuOpenLeft)
        rightMenuDismissThreshold = rightMenuOpenLeft - dpToPx(MENU_OVERPULL_AMOUNT_TO_DISMISS_DP)
    }

    private fun applyInitialState() {
        when (state) {
            MENU_OPENING, MENU_OPEN -> {
                val side = (lastEvent as? MenuOpened)?.side
                    ?: (lastEvent as? MenuOpening)?.side
                    ?: SwipeMenuSide.LEFT
                updateStateValue(MENU_OPEN, side)
                when (side) {
                    SwipeMenuSide.LEFT -> openLeftMenu(false)
                    SwipeMenuSide.RIGHT -> openMenu(false)
                }
            }

            CLOSING, CLOSED, DRAGGING -> {
                updateStateValue(CLOSED)
                close(animated = false)
            }

            DISMISSED -> {
                setDismissStateWithoutAnimation()
            }

            DISMISSED_WITHOUT_MESSAGE -> {
                updateStateValue(DISMISSED_WITHOUT_MESSAGE)
                setDismissedWithoutMessage()
            }

            DISMISSED_WITH_TIMEOUT -> {
                setDismissedWithTimeout()
            }

            else -> if (itemDismissType != DISMISS_WITHOUT_MESSAGE) {
                setDismissStateWithoutAnimation()
            }
        }
    }

    private fun setDismissStateWithoutAnimation() {
        updateStateValue(DISMISSED)
        dismiss(animated = false)
    }

    private fun ensureProperChildViews() {
        if (!areViewsInitialized) {
            if (childCount != 1) {
                throw IllegalStateException("У View должен быть объявлен ровно один дочерний элемент")
            }
            content = getChildAt(0)
            addView(rightMenuContainer)
            leftMenuContainerNullable?.let(::addView)
            areViewsInitialized = true
        } else {
            var hasContent = false
            var hasMenu = false

            (0 until childCount).map { getChildAt(it) }.forEach {
                if (it == content) hasContent = true
                if (it == rightMenuContainer || it == leftMenuContainerNullable) hasMenu = true
            }
            if (!hasContent || !hasMenu) {
                errorSafe("Структура View некорректна")
            }
        }
    }

    private fun preventDrawerOpeningIfNeeded(ev: MotionEvent) {
        if (ev.x <= dragHelper.edgeSize && hasRightMenu && rightMenuContainer.left <= rightMenuOpenLeft
            // При наличии закрытого левого меню, разрешаем открытие аккордеона только от самого края.
            || ev.x > dragHelper.edgeSize && hasLeftMenu && leftMenuContainer.left <= leftMenuOpenLeft) {
            parentDrawerLayout?.requestDisallowInterceptTouchEvent(true)
        }
    }

    private fun checkTouchSlop(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                prevX = ev.x
                isDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragged) {
                    return true
                }
                val eventX = ev.x
                val xDiff = abs(eventX - prevX)

                if (xDiff > touchSlop) {
                    isDragged = true
                }
            }
        }
        return isDragged
    }

    private fun processTouchEvent(ev: MotionEvent): Boolean {
        lastEventX = ev.x
        dragHelper.processTouchEvent(ev)
        gestureDetector.onTouchEvent(ev)

        val settling = dragHelper.viewDragState == ViewDragHelper.STATE_SETTLING
        val idleAfterScrolled = dragHelper.viewDragState == ViewDragHelper.STATE_IDLE && isScrolling

        return settling || idleAfterScrolled
    }

    private fun cannotDrag() = isDragLocked || isForcedDragLock || (isRightMenuDisabled && isLeftMenuDisabled)

    @SuppressLint("ClickableViewAccessibility")
    private fun updateStateValue(
        state: Int,
        side: SwipeMenuSide = SwipeMenuSide.RIGHT,
        notifyChanged: Boolean = state != this.state && !isAborted,
        itemUuid: String? = this.itemUuid
    ) {
        if (state == MENU_OPEN) {
            initCloseMenuOnScrollListener()
        }
        this.state = state

        if (notifyChanged) {
            stateChangeListener?.onStateChanged(state)
            val nextEvent = getNextEvent(state, side)
            if (nextEvent != lastEvent) {
                lastEvent = nextEvent
                eventListeners.forEach { it.value.invoke(nextEvent) }
            }
            when (state) {
                MENU_OPENING -> menuOpeningStartListener?.onMenuOpeningStart()
                DISMISSED_WITHOUT_MESSAGE -> dismissWithoutMessageListener?.onDismissedWithoutMessage(itemUuid)
                DISMISSED -> onDismissListener?.onDismissed(itemUuid)
            }
        }
    }

    private fun getNextEvent(nextState: Int, side: SwipeMenuSide) = when (nextState) {
        MENU_OPENING -> MenuOpening(itemUuid, side)
        MENU_OPEN -> MenuOpened(itemUuid, side)
        CLOSING, CLOSED -> Closed(itemUuid)
        DRAGGING -> Dragging(itemUuid)
        DISMISSING -> Dismissing(itemUuid)
        DISMISSED -> Dismissed(itemUuid)
        DISMISSED_WITHOUT_MESSAGE -> DismissedWithoutMessage(itemUuid)
        DISMISSED_WITH_TIMEOUT -> DismissedWithTimeout(itemUuid)
        else -> error("Unexpected state")
    }

    private fun placeRightDismissMessageLayout(container: SwipeContainerLayout, hasMenu: Boolean) {
        if (hasMenu) {
            container.attachDismissMessageLayout(rightDismissMessageLayout, SwipeMenuSide.RIGHT)
        } else {
            container.detachDismissMessageLayout()
            rightDismissMessageLayout.attachToParent(this)
        }
    }

    private fun placeLeftDismissMessageLayout(container: SwipeContainerLayout) {
        container.attachDismissMessageLayout(leftDismissMessageLayout, SwipeMenuSide.LEFT)
    }

    private fun isLeftDismissalSupported() =
        leftMenuHasRemoveOption && itemDismissType != LOCKED && itemDismissType != SwipeItemDismissType.NONE

    /**
     * Инициализация обработчика событий смахивания элемента.
     *
     * @param itemUuid идентификатор модели удаляемого элемента, доступный в обработчике.
     * @param listener слушатель события завершения смахивания элемента.
     * @param isCancellable true, если удаление смахиванием может быть отменено.
     */
    private fun initDismissListener(
        itemUuid: String? = null, listener: NewDismissListener, isCancellable: Boolean = false
    ) {
        itemUuid?.let { this.itemUuid = it }
        onDismissListener = listener
        itemDismissType = if (isCancellable) {
            CANCELLABLE
        } else {
            DISMISS_IMMEDIATE
        }
    }

    private fun slideToLastMenuItemAndThenDismiss() {
        updateStateValue(DISMISSING)

        isForcedDragLock = true
        rightMenuContainer.slideTo(rightMenuAtLastItemLeft)

        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun returnContentToDefaultPosition() {
        content.slideTo(contentDefaultLeft)

        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun showDismissMessageIfAllowedTo(panel: SwipeDismissMessageLayout) {
        if (isAborted) return

        if (itemDismissType != DISMISS_WITHOUT_MESSAGE || this@SwipeableLayout.state == DISMISSED_WITHOUT_MESSAGE) {
            updateStateValue(DISMISSED_WITHOUT_MESSAGE)
            showDismissMessage(panel)
        } else {
            updateStateValue(DISMISSED_WITHOUT_MESSAGE)
        }
    }

    private fun showDismissMessage(panel: SwipeDismissMessageLayout) {
        updateStateValue(DISMISSED_WITHOUT_MESSAGE)

        updateDismissMessageTextColor()
        panel.showDismissMessage(DISMISS_MESSAGE_APPEAR_ANIM_DURATION)

        mHandler.postDelayed({
            if (itemDismissType == CANCELLABLE) {
                dismissalTime = System.currentTimeMillis()
                updateStateValue(DISMISSED_WITH_TIMEOUT)
            } else {
                updateStateValue(DISMISSED)
            }
        }, DISMISS_MESSAGE_APPEAR_ANIM_DURATION)
    }

    private fun dismissDelayed() {
        updateStateValue(DISMISSING)

        mHandler.postDelayed({
            dismiss()
        }, LAST_MENU_ITEM_REST_DURATION)
    }

    private fun cancelDismissalTimeout() {
        getSwipeListVm()?.onItemDismissCancelled(this)
        close()
    }

    private fun setDismissMessageVisible(isVisible: Boolean) {
        rightDismissMessageLayout.changeTextVisibility(isVisible)
        leftDismissMessageLayout.changeTextVisibility(isVisible)
    }

    private fun updateDismissMessageTextColor() {
        rightDismissMessageLayout.setDismissMessageTextColor(
            if (hasMenu) menuDismissMessageTextColor else contentDismissMessageTextColor
        )
        leftDismissMessageLayout.setDismissMessageTextColor(menuDismissMessageTextColor)
    }

    private fun changeContentAlpha(offset: Float) {
        val alpha = 1 - mAlphaInterpolator.getInterpolation(offset)
        content.alpha = alpha
    }

    private fun resetContentAlpha() {
        content.alpha = 1f
    }

    private fun openMenu(side: SwipeMenuSide, container: SwipeContainerLayout, menuOpenLeft: Int, animated: Boolean) {
        isAborted = false
        isForcedDragLock = false
        setDismissMessageVisible(false)

        if (!areViewsInitialized) {
            updateStateValue(MENU_OPEN, side)
            return
        }

        if (animated) {
            updateStateValue(MENU_OPENING, side)
            container.slideTo(menuOpenLeft)
        } else {
            updateStateValue(MENU_OPEN, side)
            abort()
            if (canAdjustChildPosition()) {
                container.moveTo(menuOpenLeft)
            }
        }

        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun performDismiss(
        menuContainer: View,
        dismissLayout: SwipeDismissMessageLayout,
        menuDismissedLeft: Int, animated: Boolean
    ) {
        isAborted = false
        isForcedDragLock = true
        setDismissMessageVisible(!animated)

        if (!areViewsInitialized) {
            updateStateValue(DISMISSED, notifyChanged = false)
            return
        }

        if (animated) {
            if (state == DISMISSED_WITHOUT_MESSAGE) {
                showDismissMessage(dismissLayout)
            } else {
                updateStateValue(DISMISSING)
                if (hasMenu) {
                    menuContainer.slideTo(menuDismissedLeft)
                } else {
                    content.slideTo(contentDismissedLeft)
                }
            }
        } else {
            updateStateValue(DISMISSED, notifyChanged = false)
            dragHelper.abort()
            if (hasMenu) {
                menuContainer.moveTo(menuDismissedLeft)
            } else if (canAdjustChildPosition()) {
                content.moveTo(contentDismissedLeft)
            }
        }

        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun performClose(move: View.(Int) -> Unit) {
        when {
            isRightMenuMoved() -> rightMenuContainer.move(rightMenuClosedLeft)
            isLeftMenuMoved() -> leftMenuContainer.move(leftMenuClosedLeft)
            !hasMenu -> content.move(contentDefaultLeft)
        }
    }

    private fun isRightMenuMoved() = hasRightMenu && rightMenuContainer.left != rightMenuClosedLeft

    private fun isLeftMenuMoved() = hasLeftMenu && leftMenuContainer.left != leftMenuClosedLeft

    private fun isRightMenuOpened() = hasRightMenu && rightMenuContainer.left == rightMenuOpenLeft

    private fun isLeftMenuOpened() = hasLeftMenu && leftMenuContainer.left == leftMenuOpenLeft

    private fun View.slideTo(targetLeft: Int) {
        dragHelper.smoothSlideViewTo(this, targetLeft, top)
    }

    private fun View.moveTo(newLeft: Int) {
        layout(newLeft, top, newLeft + width, bottom)
    }

    private fun getRightMenuItemRemove() = rightMenuContainer.children.findLast { it.isVisible }

    private fun getRightMenuWidth() = rightMenuContainer.menuItemsLayout.width

    private fun getLeftMenuWidth() = leftMenuContainer.menuItemsLayout.width

    private fun getDismissFlingVelocityThreshold(): Int {
        return dpToPx(
            if (resources.configuration.screenWidthDp > SMALL_SCREEN_WIDTH_DP) {
                DISMISS_FLING_VELOCITY_THRESHOLD_DP
            } else {
                SMALL_SCREEN_DISMISS_FLING_VELOCITY_THRESHOLD_DP
            }
        )
    }

    private fun setDefaultCancellableDismissMessageIfNeeded(dismissType: SwipeItemDismissType) {
        if (dismissType == CANCELLABLE && !rightDismissMessageLayout.hasCustomText) {
            setDismissMessage(cancelDeletionMessage)
        }
    }

    private fun findParentRecyclerView() = findViewParent<RecyclerView>(this)

    private fun canAdjustChildPosition() = !isLayoutRequested || isCurrentlyOnLayout

    private fun initCloseMenuOnScrollListener() {
        parentRecyclerView?.let { recycler ->
            val listener =
                recycler.getTag(R.id.swipeable_layout_close_menu_on_scroll_listener) as CloseMenuOnScrollListener?
                    ?: CloseMenuOnScrollListener().also {
                        recycler.setTag(R.id.swipeable_layout_close_menu_on_scroll_listener, it)
                        recycler.addOnScrollListener(it)
                    }
            listener.openedSwipeableLayout = this
        }
    }

    /**
     *  Вызов abort() эквивалентен событию ACTION_CANCEL, поэтому дальнейшие события текущего жеста игнорируются
     */
    private fun shouldIgnoreEvent(ev: MotionEvent) = isAborted && ev.action != MotionEvent.ACTION_DOWN

    private fun getRecyclerViewItemId(recyclerView: RecyclerView): SwipeItemId {
        if (layoutParams !is RecyclerView.LayoutParams) return NoId
        return recyclerView.getChildAdapterPosition(this).takeIf { it >= 0 }?.let { AdapterPosition(it) } ?: NoId
    }

    /**
     * Установить observer для lifecycle экрана хоста меню, который по событию `onStop` (если это не в ходе смены
     * конфигурации) принудительно оповещает об удалении элементов с неистекшим таймаутом.
     */
    private fun observeHostOnStopEvent() = parentRecyclerView?.apply {
        val tag = R.id.swipeable_layout_is_host_lifecycle_observer_set
        if (getTag(tag) != null) return@apply
        setTag(tag, true)
        val lifecycleOwner = this.findViewTreeLifecycleOwner() ?: return@apply
        lifecycleOwner.lifecycle.addObserver(getHostLifecycleObserver())
    }

    private fun getHostLifecycleObserver() = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            try {
                if (!getActivity().isChangingConfigurations) {
                    forceDismissItemsWithTimeout()
                }
            } catch (e: Exception) {
                Timber.w(
                    e,
                    "Cannot run pending item deletions, if they are present: Activity is not accessible from SwipeableLayout"
                )
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            owner.lifecycle.removeObserver(this)
        }
    }

    private fun updateViewOutlineProvider() {
        cornerRadius?.let {
            outlineProvider = RoundedRectWithPaddingsOutlineProvider(it.getDimen(context))
            clipToOutline = true
        } ?: run {
            outlineProvider = ViewOutlineProvider.BOUNDS
            clipToOutline = false
        }
    }

    private fun centerOf(left: Int, right: Int) = (left + right) / 2

    private fun MotionEvent.isMatchingUpEventOutsideWhenMenuOpen() =
        isLeftMenuMoved() && leftMenuContainer.isMatchingUpEventOutsideWhenMenuOpen(this)
            || rightMenuContainer.isMatchingUpEventOutsideWhenMenuOpen(this)

    private fun View.isMatchingUpEventOutsideWhenMenuOpen(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && lastEvent is MenuOpened) {
            tempRect.set(left, top, right, bottom)
            return !tempRect.contains(event.x.toInt(), event.y.toInt())
        }
        return false
    }
    // endregion
}

private fun View.dpToPx(dp: Int) = dpToPx(context, dp)