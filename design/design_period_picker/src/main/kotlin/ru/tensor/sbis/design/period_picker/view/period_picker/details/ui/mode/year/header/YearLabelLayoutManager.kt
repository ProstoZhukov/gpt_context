package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.header

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * LayoutManager для заголовков годов.
 *
 * @author mb.kruglova
 */
internal class YearLabelLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {
    private val itemCount = 4

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        return super.checkLayoutParams(lp) && lp!!.width == getItemSize()
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return setProperItemSize(super.generateDefaultLayoutParams())
    }

    override fun canScrollHorizontally() = false

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return setProperItemSize(super.generateLayoutParams(lp))
    }

    /** @SelfDocumented */
    private fun setProperItemSize(lp: RecyclerView.LayoutParams): RecyclerView.LayoutParams {
        val itemSize = getItemSize()
        lp.width = itemSize
        return lp
    }

    /** @SelfDocumented */
    private fun getItemSize(): Int = width / itemCount
}