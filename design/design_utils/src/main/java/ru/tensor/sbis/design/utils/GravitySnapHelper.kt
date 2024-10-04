package ru.tensor.sbis.design.utils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.View

private const val SNAP_DISTANCE_ARRAY_SIZE = 2
private const val HORIZONTAL_SNAP_DISTANCE_INDEX = 0
private const val VERTICAL_SNAP_DISTANCE_INDEX = 1

/**
 * Определяет тип квантования
 */
enum class SnapGravity {
    START, // квантование по началу элемента
    END; // квантование по концу элемента

    companion object {
        /**
         * Определяет для константы из [Gravity], задающей тип квантования, соответствующее значение [SnapGravity]
         */
        @SuppressLint("RtlHardcoded")
        fun fromGravity(gravity: Int) = when (gravity) {
            Gravity.END, Gravity.RIGHT, Gravity.BOTTOM -> END
            else -> START
        }
    }
}

/**
 * Класс, расширяющий [LinearSnapHelper] для обеспечения квантования при прокрутке по началу, либо концу элемента
 * (по левому/правому или  верхнему/нижнему краю соответственно, в зависимости от ориентации [LinearLayoutManager]).
 * При остановке прокрутки происходит автоматическая докрутка до начала, либо конца, первого видмого по крайней мере
 * наполовину элемента. Необходимость расширения и частичное дублирование логики обусловлены отсутствием возможности
 * переопределения целевой позиции прокрутки - по умолчанию [LinearSnapHelper] докручивает view до центра
 */
open class GravitySnapHelper(snapGravity: SnapGravity) : androidx.recyclerview.widget.LinearSnapHelper() {

    private var verticalHelper: OrientationHelper? = null
    private var horizontalHelper: OrientationHelper? = null
    private var gravity = snapGravity

    constructor(gravity: Int) : this(SnapGravity.fromGravity(gravity))

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray? {
        val out = IntArray(SNAP_DISTANCE_ARRAY_SIZE) { 0 }

        if (layoutManager.canScrollHorizontally()) {
            out[HORIZONTAL_SNAP_DISTANCE_INDEX] =
                getDistanceToSnap(targetView, getHorizontalHelper(layoutManager))
        }

        if (layoutManager.canScrollVertically()) {
            out[VERTICAL_SNAP_DISTANCE_INDEX] =
                getDistanceToSnap(targetView, getVerticalHelper(layoutManager))
        }

        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager !is LinearLayoutManager) {
            return super.findSnapView(layoutManager)
        }

        val orientationHelper = when {
            layoutManager.canScrollHorizontally() -> getHorizontalHelper(layoutManager)
            layoutManager.canScrollVertically() -> getVerticalHelper(layoutManager)
            else -> null
        }

        return orientationHelper?.let {
            when (gravity) {
                SnapGravity.START -> findStartView(layoutManager, orientationHelper)
                SnapGravity.END -> findEndView(layoutManager, orientationHelper)
            }
        }
    }

    private fun getDistanceToSnap(targetView: View, orientationHelper: OrientationHelper): Int {
        return when (gravity) {
            SnapGravity.START -> getDistanceToStart(targetView, orientationHelper)
            SnapGravity.END -> getDistanceToEnd(targetView, orientationHelper)
        }
    }

    private fun getDistanceToStart(targetView: View, helper: OrientationHelper): Int {
        return helper.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    private fun getDistanceToEnd(targetView: View, helper: OrientationHelper): Int {
        return helper.getDecoratedEnd(targetView) - helper.endAfterPadding
    }

    private fun findStartView(layoutManager: LinearLayoutManager, helper: OrientationHelper): View? {

        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val child = layoutManager.findViewByPosition(firstVisiblePosition)
            ?: return null
        val childDecoratedEnd = helper.getDecoratedEnd(child)

        return when {
            isAtTheEnd(layoutManager) -> null
            childDecoratedEnd >= helper.getDecoratedMeasurement(child) / 2 && childDecoratedEnd > 0 -> child
            else -> layoutManager.findViewByPosition(firstVisiblePosition + 1)
        }
    }

    private fun findEndView(layoutManager: LinearLayoutManager, helper: OrientationHelper): View? {
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        val child = layoutManager.findViewByPosition(lastVisiblePosition)
            ?: return null
        val childDecoratedStart = helper.getDecoratedStart(child)

        return when {
            isAtTheStart(layoutManager) -> null
            childDecoratedStart + helper.getDecoratedMeasurement(child) / 2 <= helper.totalSpace -> child
            else -> layoutManager.findViewByPosition(lastVisiblePosition - 1)
        }
    }

    private fun isAtTheStart(layoutManager: LinearLayoutManager): Boolean {
        return layoutManager.findFirstCompletelyVisibleItemPosition() == 0
    }

    protected fun isAtTheEnd(layoutManager: LinearLayoutManager): Boolean {
        return layoutManager.findFirstCompletelyVisibleItemPosition() == layoutManager.itemCount - 1
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        return verticalHelper
            ?: OrientationHelper.createVerticalHelper(layoutManager)
                .also { verticalHelper = it }
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        return horizontalHelper
            ?: OrientationHelper.createHorizontalHelper(layoutManager)
                .also { horizontalHelper = it }
    }
}