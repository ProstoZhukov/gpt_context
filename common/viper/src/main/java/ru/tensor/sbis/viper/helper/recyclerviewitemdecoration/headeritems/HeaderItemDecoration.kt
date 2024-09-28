package ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.headeritems

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

/**
 * ItemDecoration для отрисовки stickyHeaders ("шапок")
 *
 * @param parent - RecyclerView, к которому добавляется декоратор
 * @param isHeader - является ли элемент этой позиции "шапкой"
 * @param shadow - опциональный Drawable для отрисовки тени "шапки"
 */
open class HeaderItemDecoration(
    val parent: RecyclerView,
    private val isHeader: (itemPosition: Int) -> Boolean,
    private val shadow: Drawable? = null
) : RecyclerView.ItemDecoration() {

    /**
     * Стоит учитывать, что изменение параметра будет отображено только после вызова метода onDrawOver
     */
    var shadowEnabled = true
    var currentHeader: Pair<Int, RecyclerView.ViewHolder>? = null

    /**
     * При аттаче шапки гарантированно вызовется onHeaderAttached
     * и если переиспользуем - onCachedHeaderAttached, иначе onNewHeaderAttached
     */
    private val onHeaderAttachedListeners = mutableListOf<(itemPosition: Int) -> Unit>()
    private val onCachedHeaderAttachedListeners = mutableListOf<(itemPosition: Int) -> Unit>()
    private val onNewHeaderAttachedListeners = mutableListOf<(itemPosition: Int) -> Unit>()

    private val onHeaderDetachedListeners = mutableListOf<(itemPosition: Int) -> Unit>()
    private val onHeaderMoveListeners = mutableListOf<(itemPosition: Int) -> Unit>()

    init {
        parent.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                clearHeader()
            }
        })

        parent.doOnEachNextLayout {
            clearHeader()
        }
    }

    /**
     * При аттаче шапки гарантированно вызовется onHeaderAttached
     */
    fun addOnHeaderAttachedListener(listener: (itemPosition: Int) -> Unit) {
        onHeaderAttachedListeners.add(listener)
    }

    /**
     * При аттаче шапки гарантированно вызовется onHeaderAttached
     * и если переиспользуем - onCachedHeaderAttached
     */
    @Suppress("unused")
    fun addOnCachedHeaderAttachedListener(listener: (itemPosition: Int) -> Unit) {
        onCachedHeaderAttachedListeners.add(listener)
    }

    /**
     * При аттаче шапки гарантированно вызовется onHeaderAttached
     * и если НЕ переиспользуем - onNewHeaderAttached
     */
    fun addOnNewHeaderAttachedListener(listener: (itemPosition: Int) -> Unit) {
        onNewHeaderAttachedListeners.add(listener)
    }

    /**@SelfDocumented */
    fun addOnHeaderDetachedListener(listener: (itemPosition: Int) -> Unit) {
        onHeaderDetachedListeners.add(listener)
    }

    /**@SelfDocumented */
    @Suppress("unused")
    fun addOnHeaderMoveListener(listener: (itemPosition: Int) -> Unit) {
        onHeaderMoveListeners.add(listener)
    }

    /**@SelfDocumented */
    fun clearHeader() {
        currentHeader?.let { onHeaderDetached(it.first) }
        currentHeader = null
    }

    /**@SelfDocumented */
    @CallSuper
    protected open fun onHeaderAttached(itemPosition: Int) {
        onHeaderAttachedListeners.forEach { it(itemPosition) }
    }

    /**@SelfDocumented */
    @CallSuper
    protected open fun onCachedHeaderAttached(itemPosition: Int) {
        onCachedHeaderAttachedListeners.forEach { it(itemPosition) }
    }

    /**@SelfDocumented */
    @CallSuper
    protected open fun onNewHeaderAttached(itemPosition: Int) {
        onNewHeaderAttachedListeners.forEach { it(itemPosition) }
    }

    /**@SelfDocumented */
    @CallSuper
    protected open fun onHeaderDetached(itemPosition: Int) {
        onHeaderDetachedListeners.forEach { it(itemPosition) }
    }

    /**@SelfDocumented */
    @CallSuper
    protected open fun onHeaderMove(itemPosition: Int) {
        onHeaderMoveListeners.forEach { it(itemPosition) }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }

        val headerView = getHeaderViewForItem(topChildPosition, parent) ?: return

        val contactPoint = headerView.bottom
        val childInContact = getChildInContact(parent, contactPoint) ?: return
        val childInContactPosition = parent.getChildAdapterPosition(childInContact)
        if (childInContactPosition == RecyclerView.NO_POSITION) {
            return
        }

        if (isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, headerView, childInContact)
            onHeaderMove(topChildPosition)
            return
        }

        drawHeader(c, headerView)

        shadow?.let { if (shadowEnabled) drawShadow(c, it, headerView) }
    }

    private fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View? {
        if (parent.adapter == null) {
            return null
        }

        val headerPosition = getHeaderPositionForItem(itemPosition)

        if (headerPosition == -1) {
            currentHeader?.let { onHeaderDetached(it.first) }
            return null
        }

        val headerType = parent.adapter?.getItemViewType(headerPosition) ?: return null

        onHeaderAttached(headerPosition)

        // if match reuse viewHolder
        if (canReuse(headerPosition, headerType)) {
            onCachedHeaderAttached(headerPosition)
            return currentHeader?.second?.itemView
        }

        onNewHeaderAttached(headerPosition)

        val headerHolder = parent.adapter?.createViewHolder(parent, headerType)
        if (headerHolder != null) {
            parent.adapter?.onBindViewHolder(headerHolder, headerPosition)
            fixLayoutSize(parent, headerHolder.itemView)

            currentHeader = headerPosition to headerHolder
        }
        return headerHolder?.itemView
    }

    private fun canReuse(headerPosition: Int, headerType: Int) =
        currentHeader?.first == headerPosition && currentHeader?.second?.itemViewType == headerType

    protected open fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    protected open fun drawShadow(c: Canvas, shadow: Drawable, header: View) {
        shadow.setBounds(header.left, header.bottom, header.right, header.bottom + shadow.intrinsicHeight)
        c.save()
        c.translate(0f, 0f)
        shadow.draw(c)
        c.restore()
    }

    protected open fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0f, (nextHeader.top - currentHeader.height).toFloat())
        currentHeader.draw(c)
        currentHeader.setMargins(0, nextHeader.top - currentHeader.height, 0, 0)
        c.restore()
    }

    private fun View.setMargins(l: Int, t: Int, r: Int, b: Int) {
        if (this.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = this.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(l, t, r, b)
            this.requestLayout()
        }
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val mBounds = Rect()
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            if (mBounds.bottom > contactPoint) {
                if (mBounds.top <= contactPoint) {
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidthSpec =
            ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = -1
        var currentPosition = itemPosition
        do {
            if (isHeader(currentPosition)) {
                headerPosition = currentPosition
                break
            }
            currentPosition -= 1
        } while (currentPosition >= 0)
        return headerPosition
    }
}

private inline fun View.doOnEachNextLayout(crossinline action: (view: View) -> Unit) {
    addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ -> action(view) }
}