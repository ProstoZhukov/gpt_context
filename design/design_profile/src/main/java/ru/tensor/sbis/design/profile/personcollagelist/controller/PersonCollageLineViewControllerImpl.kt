package ru.tensor.sbis.design.profile.personcollagelist.controller

import android.content.res.Resources
import android.graphics.Canvas
import android.view.View
import android.view.View.resolveSize
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.personcollagelist.PersonCollageLineView
import ru.tensor.sbis.design.profile.personcollagelist.collagechildrenmanager.PersonCollageLineViewChildrenManager
import ru.tensor.sbis.design.profile.personcollagelist.collagechildrenmanager.PersonCollageLineViewChildrenManagerImpl
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.theme.global_variables.ImageSize
import ru.tensor.sbis.design.utils.checkSafe
import kotlin.math.max
import kotlin.math.roundToInt

const val ITEM_OVERLAP_FRACTION = 1 / 3f

/**
 * Реализует логику компонента [PersonCollageLineView].
 *
 * @author us.bessonov
 */
internal class PersonCollageLineViewControllerImpl(
    private val childrenManager: PersonCollageLineViewChildrenManagerImpl = PersonCollageLineViewChildrenManagerImpl()
) : PersonCollageLineViewController, PersonCollageLineViewChildrenManager by childrenManager {

    private lateinit var collageView: View

    @Px
    private var itemSize = 0

    @Px
    private var itemOverlapPart = 0

    private var dataList = emptyList<PhotoData>()

    private var size = PhotoSize.UNSPECIFIED

    private var maxVisibleCount = Int.MAX_VALUE

    private var totalCount = 0

    private var isHidden = false

    //region PersonCollageLineViewController
    override fun setDataList(dataList: List<PhotoData>) {
        if (this.dataList == dataList) return
        this.dataList = dataList
        if (!collageView.isLaidOut || !collageView.isLayoutRequested && !tryUpdateChildren(dataList)) {
            collageView.requestLayout()
        }
    }

    override fun setSize(size: PhotoSize) {
        if (size != this.size) {
            this.size = size
            itemSize = size.photoImageSize.takeUnless { it == null }
                ?.let(::getDimensionPixelSizeNullable)
                ?: 0
            collageView.requestLayout()
        }
    }

    override fun setMaxVisibleCount(count: Int) {
        require(count >= 0) {
            "Max visible count should be positive"
        }
        if (count != maxVisibleCount) {
            maxVisibleCount = count
            collageView.requestLayout()
        }
    }

    override fun setTotalCount(count: Int) {
        if (totalCount != count) {
            totalCount = count
            collageView.requestLayout()
        }
    }

    override fun getMinRequiredWidth(count: Int): Int {
        require(count >= 0) {
            "Cannot get width when count is negative"
        }
        return when (count) {
            0 -> 0
            1 -> itemSize
            else -> 2 * itemSize - itemOverlapPart
        }
    }
    //endregion

    // region PersonCollageLineViewControllerInternal
    override fun setCollageView(view: View) {
        collageView = view
        childrenManager.setCollageView(view)
    }

    override fun performMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int, minWidth: Int): MeasuredDimension {
        val paddingHorizontal = collageView.paddingLeft + collageView.paddingRight
        val paddingVertical = collageView.paddingTop + collageView.paddingBottom
        itemSize = getItemSize(heightMeasureSpec, paddingVertical)
        if (dataList.isEmpty()) {
            childrenManager.updateChildren(0, 0, dataList)
            return MeasuredDimension(
                maxOf(minWidth, resolveSize(paddingHorizontal, widthMeasureSpec)),
                itemSize
            )
        }
        checkSafe(
            size != PhotoSize.UNSPECIFIED ||
                View.MeasureSpec.getMode(heightMeasureSpec) != View.MeasureSpec.UNSPECIFIED
        ) { "Photo size is set as ${PhotoSize.UNSPECIFIED}, but view height is also unspecified" }
        val initialsTextSize = size.getInitialsTextSize(collageView.context)
        childrenManager.updateItemSize(itemSize, itemOverlapPart, initialsTextSize)
        itemOverlapPart = (itemSize * ITEM_OVERLAP_FRACTION).roundToInt()

        val displayedData = dataList.take(maxVisibleCount)
        val availableWidth = max(resolveSize(Int.MAX_VALUE, widthMeasureSpec), minWidth) - paddingHorizontal
        val defaultHiddenCount = totalCount.coerceAtLeast(dataList.size) - displayedData.size
        val (visibleCount, desiredWidth) = calculateVisibleCountAndDesiredWidth(
            availableWidth,
            displayedData.size
        )

        childrenManager.updateChildren(visibleCount, defaultHiddenCount, displayedData)

        return MeasuredDimension(
            max(minWidth, resolveSize(desiredWidth + paddingHorizontal, widthMeasureSpec)),
            itemSize + paddingVertical
        )
    }

    override fun performLayout() {
        var childLeft = collageView.paddingLeft
        val childTop = collageView.paddingTop
        childrenManager.children.forEach {
            it.layout(childLeft, childTop, childLeft + itemSize, childTop + itemSize)
            childLeft += itemSize - itemOverlapPart
        }
    }

    override fun performDraw(canvas: Canvas) {
        children.forEach {
            it.draw(canvas)
        }
    }

    override fun performInvalidate() {
        children.forEach(PersonImageView::invalidate)
    }

    override fun onDetachedFromWindow() {
        childrenManager.onDetachedFromWindow()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        if (isHidden && isVisible) {
            reloadImagesIfRecycled()
        }
        isHidden = !isVisible
    }

    //endregion

    private fun tryUpdateChildren(dataList: List<PhotoData>): Boolean {
        if (childrenManager.isReLayoutRequired(dataList.size, maxVisibleCount, totalCount)) {
            return false
        }
        val displayedData = dataList.take(maxVisibleCount)
        val defaultHiddenCount = totalCount.coerceAtLeast(dataList.size) - displayedData.size

        val visibleCount = displayedData.size
        val shouldRequestLayout = dataList.size.coerceAtMost(visibleCount) != children.size
        childrenManager.updateChildren(visibleCount, defaultHiddenCount, dataList)
        collageView.invalidate()
        if (shouldRequestLayout) collageView.requestLayout()
        return true
    }

    private fun getItemSize(heightMeasureSpec: Int, @Px paddingVertical: Int) = when {
        size != PhotoSize.UNSPECIFIED -> itemSize
        View.MeasureSpec.getMode(heightMeasureSpec) != View.MeasureSpec.UNSPECIFIED -> {
            View.MeasureSpec.getSize(heightMeasureSpec) - paddingVertical
        }

        else -> 0
    }

    private fun calculateVisibleCountAndDesiredWidth(
        @Px availableWidth: Int,
        displayedCount: Int
    ): Pair<Int, Int> {
        var visibleCount = displayedCount
        var desiredWidth: Int
        while (true) {
            desiredWidth = getDesiredWidth(visibleCount)
            if (desiredWidth <= availableWidth || visibleCount == 0) break
            visibleCount--
        }

        return visibleCount to desiredWidth
    }

    @Px
    private fun getDimensionPixelSizeNullable(size: ImageSize) = try {
        size.getDimenPx(collageView.context)
    } catch (e: Resources.NotFoundException) {
        null
    }

    @Px
    private fun getDesiredWidth(visibleCount: Int): Int {
        return itemSize + (itemSize - itemOverlapPart) * (visibleCount - 1)
    }

}