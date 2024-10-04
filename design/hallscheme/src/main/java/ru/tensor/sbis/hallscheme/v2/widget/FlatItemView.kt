package ru.tensor.sbis.hallscheme.v2.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi
import ru.tensor.sbis.hallscheme.v2.util.withClipOut

/**
 * Класс для отображения стола/бара в плоской схеме.
 * @author aa.gulevskiy
 */
internal open class FlatItemView private constructor(context: Context) : AbstractItemView(context) {

    private val paintMainLayer: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    companion object {
        /**@SelfDocumented*/
        fun newInstance(
            context: Context,
            orderableItem: OrderableItemUi
        ): AbstractItemView {
            return FlatItemView(context).apply {
                initialize(orderableItem)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(orderableItemUi) {
            rotateItem(canvas)

            canvas.withClipOut(mainLayerPath) {
                drawFlatChairs(canvas)
                drawFlatSofas(canvas)
            }

            canvas.drawPath(mainLayerPath, paintMainLayer)
            canvas.drawPath(mainLayerPath, paintContourLayer)

            drawInfoWindow(canvas)
        }
    }

    override fun adjustPaint() {
        paintMainLayer.style = Paint.Style.FILL
        paintContourLayer.style = Paint.Style.STROKE
        paintContourLayer.strokeWidth = strokeWidth.toFloat()
        paintContourLayer.color = orderableItemUi.color
        initMainLayerPaint()
    }

    override fun initMainLayerPaint() {
        with(paintMainLayer) {
            color = ContextCompat.getColor(
                context,
                when (orderableItemUi.orderableItem.tableInfo.tableStatus) {
                    TableStatus.HasReadyDishes,
                    TableStatus.Occupied,
                    TableStatus.OccupiedForUser ->
                        context.getThemeColor(R.attr.hall_scheme_table_occupied_background)
                    else ->
                        android.R.color.transparent
                }
            )
        }
    }

    override fun setViewPressed() {
        with(paintMainLayer) {
            color = ContextCompat.getColor(
                context,
                when (orderableItemUi.orderableItem.tableInfo.tableStatus) {
                    TableStatus.HasReadyDishes,
                    TableStatus.Occupied,
                    TableStatus.OccupiedForUser ->
                        context.getThemeColor(R.attr.hall_scheme_table_occupied_background_pressed)
                    TableStatus.Disabled ->
                        android.R.color.transparent
                    else ->
                        context.getThemeColor(R.attr.hall_scheme_table_empty_background_pressed)
                }
            )
        }
    }

    override fun select() {
        paintContourLayer.color = selectionContourColor
        paintMainLayer.color = selectionBackgroundColor
        super.select()
    }

    override fun unSelect() {
        initMainLayerPaint()
        paintContourLayer.color = orderableItemUi.color
        super.unSelect()
    }

    override fun selectForMultiSelect() {
        paintContourLayer.color = selectionContourColor
        super.selectForMultiSelect()
    }

    override fun unSelectForMultiSelect() {
        paintContourLayer.color = orderableItemUi.color
        super.unSelectForMultiSelect()
    }
}