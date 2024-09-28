package ru.tensor.sbis.hallscheme.v2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus
import ru.tensor.sbis.hallscheme.v2.data.HallSchemeItemDto
import ru.tensor.sbis.hallscheme.v2.presentation.factory.creator.ItemFlatCreator
import kotlin.math.max
import kotlin.math.min

/**
 * View для отображения одиночного стола из схемы зала.
 */
class SimpleTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.hallSchemeTheme,
    @StyleRes defStyleRes: Int = R.style.HallSchemeDarkTheme
) : ViewGroup(
    ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    /**
     * Элемент схемы зала для отображения.
     */
    var schemeItem: HallSchemeItemDto? = null
        set(value) {
            removeAllViews()
            field = value
            if (value != null) {
                val tableItem = itemCreator.mapItems(listOf(value)).firstOrNull()
                tableView = tableItem?.getView(this)?.also {
                    it.x += hallSchemeSpecHolder.tableSpec.padding
                    it.y += hallSchemeSpecHolder.tableSpec.padding
                    it.isEnabled = false
                    addView(it)
                }
            }
        }

    /**
     * Цвет контура стола.
     */
    @ColorInt
    var tableColor: Int = IconColor.DEFAULT.getValue(context)

    private val colorsHolder by lazy {
        ColorsHolder(mapOf(TableStatus.Default to tableColor), tableColor)
    }

    private val hallSchemeSpecHolder by lazy { initHallSchemeSpecificationsHolder() }

    private val itemCreator by lazy {
        ItemFlatCreator(
            context,
            colorsHolder,
            hallSchemeSpecHolder
        )
    }

    private var tableView: View? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val layoutWidth = right - left
        val layoutHeight = bottom - top

        val child = tableView
        if (child != null) {
            measureChild(child, MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
            if (child.measuredWidth > layoutWidth || child.measuredHeight > layoutHeight) {
                val scaleX = layoutWidth / child.measuredWidth.toFloat()
                val scaleY = layoutHeight / child.measuredHeight.toFloat()
                if (scaleX < scaleY) {
                    child.scaleX = scaleX
                    child.scaleY = scaleX
                    child.translationX = (layoutWidth - child.measuredWidth) / 2f
                } else {
                    child.scaleX = scaleY
                    child.scaleY = scaleY
                    child.translationY = (layoutHeight - child.measuredHeight) / 2f
                }
            }
            child.layout(0, 0, child.measuredWidth, child.measuredHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val child = tableView
        if (child != null) {
            if (widthMeasureSpec != MeasureSpec.EXACTLY || heightMeasureSpec != MeasureSpec.EXACTLY) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
            }

            val width =
                if (widthMode == MeasureSpec.EXACTLY) widthSize
                else {
                    val maxWidth = max(suggestedMinimumWidth, child.measuredWidth)
                    if (widthSize == 0) {
                        maxWidth
                    } else {
                        min(widthSize, maxWidth)
                    }
                }

            val height =
                if (heightMode == MeasureSpec.EXACTLY) heightSize
                else {
                    val maxHeight = max(suggestedMinimumHeight, child.measuredHeight)
                    if (heightSize == 0) {
                        maxHeight
                    } else {
                        min(heightSize, maxHeight)
                    }
                }

            setMeasuredDimension(
                MeasureSpec.makeMeasureSpec(width, widthMode),
                MeasureSpec.makeMeasureSpec(height, heightMode)
            )
        }
    }

    private fun initHallSchemeSpecificationsHolder(): HallSchemeSpecHolder {
        val extraWidth = context.resources.getDimensionPixelOffset(R.dimen.hall_scheme_table_extra_width)
        val tableSpec = initTableSpec(extraWidth)
        val chairSpec = initChairSpec()
        val sofaSpec = initSofaSpec()
        val billSpec = initBillSpec()
        val bookingSpec = initBookingSpec()
        val assigneeSpec = initAssigneeSpec()
        return HallSchemeSpecHolder(tableSpec, chairSpec, sofaSpec, billSpec, bookingSpec, assigneeSpec)
    }

    private fun initTableSpec(extraWidth: Int): HallSchemeSpecHolder.TableSpec {
        val roundCornerRadius = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_corner_radius)
        val circleEnhancement = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_circle_enhancement)
        val padding = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_padding)
        return HallSchemeSpecHolder.TableSpec(extraWidth, roundCornerRadius, circleEnhancement, padding)
    }

    private fun initChairSpec(): HallSchemeSpecHolder.ChairSpec {
        val chairHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_chair_height)
        val chairFullHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_chair_full_height)
        val chairWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_chair_width)
        return HallSchemeSpecHolder.ChairSpec(chairHeight, chairFullHeight, chairWidth)
    }

    private fun initSofaSpec(): HallSchemeSpecHolder.SofaSpec {
        val straightHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_straight_height)
        val straightWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_straight_width)
        val sectionWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_section_width)
        val cornerHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_corner_height)
        val cornerWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_sofa_corner_width)
        return HallSchemeSpecHolder.SofaSpec(straightHeight, straightWidth, sectionWidth, cornerHeight, cornerWidth)
    }

    private fun initBillSpec(): HallSchemeSpecHolder.BillSpec {
        val billHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_bill_height)
        val billWidth = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_bill_width)
        val billOffset = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_bill_offset)
        return HallSchemeSpecHolder.BillSpec(billHeight, billWidth, billOffset)
    }

    private fun initBookingSpec(): HallSchemeSpecHolder.BookingSpec {
        val bookingHeight = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_booking_height)
        val bookingIntersection =
            context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_booking_intersection)
        return HallSchemeSpecHolder.BookingSpec(bookingHeight, bookingIntersection)
    }

    private fun initAssigneeSpec(): HallSchemeSpecHolder.AssigneeSpec {
        val size = context.resources.getDimensionPixelSize(R.dimen.hall_scheme_table_assignee_size)
        return HallSchemeSpecHolder.AssigneeSpec(size)
    }
}