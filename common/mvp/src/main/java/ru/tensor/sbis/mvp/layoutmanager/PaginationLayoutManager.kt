package ru.tensor.sbis.mvp.layoutmanager

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_BOTTOM_PADDING
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_EMPTY
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_PROGRESS
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.design.scroll_to_top.AbstractScrollToTopHelper
import timber.log.Timber

/**
 * LayoutManager с типами вью для прогресса
 * и пустой вью из [BaseTwoWayPaginationAdapter]
 * по умолчаню.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
open class PaginationLayoutManager(
    context: Context,
    protected val scrollHelper: ScrollHelper? = null,
    protected val scrollToTopHelper: AbstractScrollToTopHelper? = null,
    private val progressViewType: Int = HOLDER_PROGRESS,
    private val emptyViewType: Int = HOLDER_EMPTY
) : LinearLayoutManager(context) {

    /** @SelfDocumented */
    var paginationEnabled = true

    @JvmField
    protected var recyclerView: RecyclerView? = null

    private val recyclerVisibleRectBuffer = Rect()
    private val childLocationBuffer = IntArray(2)

    private val scrollWatcher by lazy(LazyThreadSafetyMode.NONE) {
        PaginationLinearScrollWatcher(
            this,
            scrollHelper,
            progressViewType,
            emptyViewType,
            HOLDER_BOTTOM_PADDING,
            scrollToTopHelper?.let { helper ->
                object : ScrollToTopMediator {
                    override fun isAvailable(view: RecyclerView): Boolean {
                        return isScrollToTopAvailable(view.context)
                    }

                    override fun disableScrollToTop() {
                        helper.disableScrollToTop()
                    }

                    override fun enableScrollToTop() {
                        helper.enableScrollToTop()
                    }
                }
            }
        )
    }

    //region временные конструкторы, необходимые для вызовов из Java-кода
    constructor(context: Context) :
        this(context, null)

    constructor(context: Context, scrollHelper: ScrollHelper?) :
        this(context, scrollHelper, null, HOLDER_PROGRESS, HOLDER_EMPTY)

    @Deprecated("ScrollToTopHelper")
    constructor(context: Context, scrollHelper: ScrollHelper?, scrollToTopHelper: AbstractScrollToTopHelper?) :
        this(context, scrollHelper, scrollToTopHelper, HOLDER_PROGRESS, HOLDER_EMPTY)
    //endregion

    protected fun getRecycler(): RecyclerView? {
        return recyclerView
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        scrollWatcher.onAttachedToRecyclerView(view)
        recyclerView = view
    }

    override fun onDetachedFromWindow(
        view: RecyclerView?,
        recycler: RecyclerView.Recycler?
    ) {
        super.onDetachedFromWindow(view, recycler)
        scrollWatcher.onDetachedFromRecyclerView(view)
        recyclerView = null
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (paginationEnabled && scrollWatcher.preventOverScroll(dy, recycler, state)) {
            return 0
        }
        return try {
            super.scrollVerticallyBy(dy, recycler, state)
        } catch (e: Exception) {
            CommonUtils.handleException(e)
            0
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Timber.e(e)
            recycler?.clear()
        }
    }

    override fun onItemsChanged(recyclerView: RecyclerView) {
        super.onItemsChanged(recyclerView)
        // количество элементов могло уменьшиться после обновления списка
        // (например, по событию или после принудительного обновления)
        scrollWatcher.onItemsChanged()
    }

    override fun onItemsRemoved(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount)
        scrollWatcher.onItemsRemoved(positionStart, itemCount)
    }

    override fun onItemsUpdated(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount)
        scrollWatcher.onItemsUpdated(positionStart, itemCount)
    }

    override fun onItemsAdded(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
        super.onItemsAdded(recyclerView, positionStart, itemCount)
        scrollWatcher.onItemsAdded(positionStart, itemCount)
    }

    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     * See: https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position
     */
    override fun supportsPredictiveItemAnimations(): Boolean = false

    /**
     * Если RecyclerView находится внутри AppBarLayout,
     * то при наличии SCROLL_FLAG LayoutParams происходит некорректный
     * подсчет по причине увеличения высоты RecyclerView
     */
    override fun findLastCompletelyVisibleItemPosition(): Int {
        if (scrollToTopHelper?.isScrollToTopEnabled() == true) {
            val item = findVisibleItem(childCount - 1, -1, true)
            return item?.let { getPosition(it) } ?: RecyclerView.NO_POSITION
        }
        return super.findLastCompletelyVisibleItemPosition()
    }

    private fun findVisibleItem(fromIndex: Int, toIndex: Int, isCompletely: Boolean): View? {
        val next = if (toIndex > fromIndex) 1 else -1
        var i = fromIndex
        if (i != toIndex && recyclerView?.getGlobalVisibleRect(recyclerVisibleRectBuffer) == true) {
            while (i != toIndex) {
                val child = getChildAt(i)
                if (child != null && childIsVisible(child, recyclerVisibleRectBuffer, isCompletely)) {
                    return child
                }
                i += next
            }
        }
        return null
    }

    private fun childIsVisible(child: View, parentRect: Rect, isCompletely: Boolean): Boolean {
        child.getLocationOnScreen(childLocationBuffer)
        return if (orientation == HORIZONTAL) {
            val childLeft = childLocationBuffer[0]
            val childRight = childLeft + child.width + getRightDecorationWidth(child)
            if (isCompletely) {
                childLeft >= parentRect.left && childRight <= parentRect.right
            } else {
                childLeft <= parentRect.right && childRight >= parentRect.left
            }
        } else {
            val childTop = childLocationBuffer[1]
            val childBottom = childTop + child.height + getBottomDecorationHeight(child)
            if (isCompletely) {
                childTop >= parentRect.top && childBottom <= parentRect.bottom
            } else {
                childTop <= parentRect.bottom && childBottom >= parentRect.top
            }
        }
    }

    /**
     * Проверить, доступен ли ScrollToTop. По умолчанию
     * на планшетах ScrollToTop недоступен.
     */
    protected open fun isScrollToTopAvailable(context: Context): Boolean {
        return scrollToTopHelper != null && !DeviceConfigurationUtils.isTablet(context)
    }

}