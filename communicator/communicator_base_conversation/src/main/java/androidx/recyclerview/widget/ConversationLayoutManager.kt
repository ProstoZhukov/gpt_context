package androidx.recyclerview.widget

import android.content.Context
import android.graphics.Rect
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.ConversationLayoutManager.LaidOutItemsListener
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_BOTTOM_PADDING
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_EMPTY
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter.HOLDER_PROGRESS
import ru.tensor.sbis.common.util.CommonUtils
import timber.log.Timber

/**
 * Реализация [LinearLayoutManager].
 * Менеджер адаптирован для пагинации,
 * раздает колбэки о текущих видимых элементах слушателям [LaidOutItemsListener],
 * также умеет задавать позицию якоря для первичного отображения списка.
 *
 * @author vv.chekurda
 */
class ConversationLayoutManager(
    context: Context,
    private val laidOutItemsListener: LaidOutItemsListener,
    private val progressViewType: Int = HOLDER_PROGRESS,
    private val emptyViewType: Int = HOLDER_EMPTY
) : LinearLayoutManager(context) {

    private val rect = Rect()
    private var hasPadding = false
    private var onScrollToBottomCalled = false

    var initialPendingPosition: Int? = null
        private set

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        view?.let { hasPadding = it.paddingTop > 0 }
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        preventOverscroll(dy)
        val toStart = isScrollDirectionToStart(dy)
        if (toStart) {
            val position = findFirstCompletelyVisibleItemPosition()
            if (position != RecyclerView.NO_POSITION) {
                val firstCompleteItemType = getItemViewType(findViewByPosition(position)!!)
                if (firstCompleteItemType == progressViewType || firstCompleteItemType == emptyViewType) {
                    // Чтобы случайно не ограничить скролл вверх в случае, когда скролл вверх произошел сразу после
                    // попытки проскроллить список ниже с сопутствующей загрузкой следующей страницы,
                    // а на экране присутствует только один элемент данных, отображенный не целиком,
                    // проверяем, действительно-ли найденный холдер заглушки в списке располагается на верхней границе текущей страницы.
                    val contentLastItemPosition = getContentLastItemPosition(mRecyclerView?.adapter)
                    // Индекс последнего элемента данных больше, чем индекс видимой заглушки
                    // - заглушка располагается на границе текущей страницы сверху. Предотвращаем дальнейший скролл
                    if (contentLastItemPosition > position) {
                        return 0
                    }
                    // в ином случае найденная заглушка расположена на нижней границе страницы и ограничивать скролл вверх нельзя.
                }
            }
        }
        return try {
            super.scrollVerticallyBy(dy, recycler, state)
        } catch (e: Exception) {
            CommonUtils.handleException(e)
            0
        }.also { scrolledBy ->
            if (scrolledBy != 0) updateLaidOutPositions()
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        laidOutItemsListener.onScrollStateChanged(state)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
            updateLaidOutPositions()
            initialPendingPosition = null
        } catch (ex: IndexOutOfBoundsException) {
            Timber.e(ex)
            recycler?.clear()
        }
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        try {
            updateLaidOutPositions()
        } catch (ex: IndexOutOfBoundsException) {
            Timber.e(ex)
        }
    }

    override fun getPosition(view: View): Int =
        super.getPosition(view).coerceAtLeast(0)

    fun updateLaidOutPositions() {
        val reversed = reverseLayout
        val lastVisibleItemAdapterPosition: Int = getLastVisibleItemPosition(reversed)
        val firstVisibleItemAdapterPosition: Int = getFirstVisibleItemPosition(reversed)
        val topmostItemPosition = if (reversed) lastVisibleItemAdapterPosition else firstVisibleItemAdapterPosition
        val bottommostItemPosition = if (reversed) firstVisibleItemAdapterPosition else lastVisibleItemAdapterPosition
        if (onScrollToBottomCalled && bottommostItemPosition != 0) {
            onScrollToBottomCalled = false
            return
        }
        if (topmostItemPosition >= 0) {
            laidOutItemsListener.onItemsLaidOut(topmostItemPosition, bottommostItemPosition)
            val firstItemShown = bottommostItemPosition == 0
            val bottomostView = findViewByPosition(bottommostItemPosition)
            bottomostView!!.getGlobalVisibleRect(rect)
            val bottomostItemBottom = rect.bottom
            var bottomPosDifference = 0
            mRecyclerView?.let {
                it.getGlobalVisibleRect(rect)
                bottomPosDifference = bottomostItemBottom - (rect.bottom - it.paddingBottom)
            }
            val atBottomOfItem = firstItemShown && bottomPosDifference < BOTTOM_ITEM_THRESHOLD
            laidOutItemsListener.onFirstItemShownStateChanged(firstItemShown, atBottomOfItem)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = super.onSaveInstanceState() as SavedState
        if (childCount > 0) {
            val refChild = getChildAt(0) ?: return state
            ensureLayoutState()
            state.apply {
                mAnchorLayoutFromEnd = true
                mAnchorOffset = mOrientationHelper.let { it.endAfterPadding - it.getDecoratedEnd(refChild) }
                mAnchorPosition = getPosition(refChild)
            }
        }
        return state
    }

    /**
     * Вызван скролл в самый низ списка.
     */
    fun onScrollToBottom() {
        onScrollToBottomCalled = true
    }

    /**
     * Задать позицию якорного элемента списка для первичного отображения
     *
     * @param position позиция для первичного отображения
     */
    fun setInitialAnchorPosition(position: Int) {
        mPendingScrollPosition = position
        initialPendingPosition = mPendingScrollPosition
    }

    private fun getFirstVisibleItemPosition(reversed: Boolean): Int {
        var firstVisibleItemAdapterPosition = super.findFirstVisibleItemPosition()
        if (hasPadding && !clipToPadding && !reversed) {
            var view = findViewByPosition(firstVisibleItemAdapterPosition)
            while (firstVisibleItemAdapterPosition > 0 && (view == null || view.top > 0)) {
                firstVisibleItemAdapterPosition -= 1
                view = findViewByPosition(firstVisibleItemAdapterPosition)
            }
        }
        return firstVisibleItemAdapterPosition
    }

    private fun getLastVisibleItemPosition(reversed: Boolean): Int {
        var lastVisibleItemAdapterPosition = super.findLastVisibleItemPosition()
        if (lastVisibleItemAdapterPosition == RecyclerView.NO_POSITION) {
            return lastVisibleItemAdapterPosition
        }
        if (hasPadding && !clipToPadding && reversed) {
            var view = findViewByPosition(lastVisibleItemAdapterPosition)
            while (view != null && view.top > 0) {
                lastVisibleItemAdapterPosition += 1
                view = findViewByPosition(lastVisibleItemAdapterPosition)
            }
        }
        return lastVisibleItemAdapterPosition
    }

    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     * See: https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position
     */
    override fun supportsPredictiveItemAnimations(): Boolean = false

    /**
     * Метод для получения индекса последнего элемента в адаптере.
     * Поскольку на многих экранах используется пустая ячейка с высотой
     * нижней панели навигации (чтобы избежать некоторых проблем), а также
     * последней ячейкой может быть прогресс при пагинации, то мы
     * учитываем это вычитаем эту ячейку из расчетов видимости контента.
     */
    private fun getContentLastItemPosition(adapter: RecyclerView.Adapter<*>?): Int {
        return when {
            adapter == null -> 0
            adapter.itemCount == 0 -> 0
            (
                adapter.getItemViewType(adapter.itemCount - 1) == HOLDER_BOTTOM_PADDING ||
                    adapter.getItemViewType(adapter.itemCount - 1) == progressViewType
                ) -> adapter.itemCount - 1
            else -> adapter.itemCount
        }
    }

    private fun isScrollDirectionToStart(dy: Int): Boolean =
        if (reverseLayout) dy > 0 else dy < 0

    /**
     * Цель костыля - предотвратить дальнейший скролл контента после того, как мы упёрлись в естественную
     * или искуственную границу, добавили данных в список и вызвали notifyItemRangeInserted/notifyItemRangeChanged.
     * Либо лагает экран из-за переизбытка анимации.
     */
    private fun preventOverscroll(dy: Int) {
        val toStart = isScrollDirectionToStart(dy)
        if (toStart) {
            val firstVisiblePosition = findFirstVisibleItemPosition()
            if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                val firstItemType = getItemViewType(findViewByPosition(firstVisiblePosition)!!)
                if (firstItemType == progressViewType || firstItemType == emptyViewType) {
                    mRecyclerView?.apply {
                        stopScroll()
                    }
                }
            }
        } else {
            val lastVisiblePosition = findLastVisibleItemPosition()
            if (lastVisiblePosition != RecyclerView.NO_POSITION) {
                val lastItemType = getItemViewType(findViewByPosition(lastVisiblePosition)!!)
                if (lastItemType == progressViewType) {
                    mRecyclerView?.apply {
                        stopScroll()
                    }
                }
            }
        }
    }

    /**
     * Слушатель колбэков о текущих видимых элементах слушателям.
     */
    interface LaidOutItemsListener {
        /** @SelfDocumented **/
        fun onFirstItemShownStateChanged(shown: Boolean, atBottomOfItem: Boolean)

        /** @SelfDocumented **/
        fun onItemsLaidOut(topmostItemPosition: Int, bottommostItemPosition: Int)

        /** @SelfDocumented **/
        fun onScrollStateChanged(state: Int)
    }
}

private const val BOTTOM_ITEM_THRESHOLD = 10