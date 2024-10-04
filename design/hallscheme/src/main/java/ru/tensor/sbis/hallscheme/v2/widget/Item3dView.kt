package ru.tensor.sbis.hallscheme.v2.widget

import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi
import ru.tensor.sbis.hallscheme.v2.util.withClipOut

/**
 * Класс для отображения стола/бара в объёмной схеме.
 * @author aa.gulevskiy
 */
internal class Item3dView private constructor(context: Context) : AbstractItemView(context) {

    private var paintMainLayer: Paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val paintDepthLayer: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintShadow: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var unpressedShader: BitmapShader? = null
    private var pressedShader: BitmapShader? = null
    @ColorRes
    private var occupiedColor: Int? = null
    private var shadowSmallOffset = 6F
    private var shadowLargeOffset = 6F

    private lateinit var shadowPath: Path

    companion object {
        /**@SelfDocumented*/
        fun newInstance(
            context: Context,
            orderableItemUi: OrderableItemUi,
            pressedShader: BitmapShader?,
            unpressedShader: BitmapShader?,
            @ColorRes occupiedColor: Int
        ): AbstractItemView {
            return Item3dView(context).apply {
                this.unpressedShader = unpressedShader
                this.pressedShader = pressedShader
                this.occupiedColor = occupiedColor
                initialize(orderableItemUi)
            }
        }
    }

    init {
        paintMainLayer.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(orderableItemUi) {
            rotateItem(canvas)

            canvas.drawPath(shadowPath, paintShadow)

            canvas.withClipOut(mainLayerPath) {
                draw3dChairs(canvas)
                draw3dSofas(canvas)
            }

            canvas.drawPath(mainLayerPath, paintMainLayer)
            canvas.drawPath(depthLayerPath, paintDepthLayer)
            canvas.drawPath(mainLayerPath, paintContourLayer)

            drawInfoWindow(canvas)
        }
    }

    override fun adjustPaint() {
        initShadowLayerPaint()
        initMainLayerPaint()
        initContourLayerPaint()
        initDepthLayerPaint()
    }

    private fun initShadowLayerPaint() {
        paintShadow.style = Paint.Style.FILL
        paintShadow.strokeWidth = 10F
        paintShadow.color = ContextCompat.getColor(context, R.color.hall_scheme_black)
        paintShadow.alpha = 75

        changeShadowOffsets()
        shadowPath = Path(orderableItemUi.mainLayerPath)
        shadowPath.offset(shadowSmallOffset, shadowLargeOffset)
    }

    private fun changeShadowOffsets() {
        when (orderableItemUi.orderableItem.itemRotation) {
            90 -> {
                shadowSmallOffset = 6F
                shadowLargeOffset = -6F
            }
            180 -> {
                shadowSmallOffset = -6F
                shadowLargeOffset = -6F
            }
            270 -> {
                shadowSmallOffset = -6F
                shadowLargeOffset = 6F
            }
        }
    }

    override fun initMainLayerPaint() {
        paintMainLayer = Paint(Paint.FILTER_BITMAP_FLAG)
        paintMainLayer.style = Paint.Style.FILL

        when (orderableItemUi.orderableItem.tableInfo.tableStatus) {
            TableStatus.HasReadyDishes, TableStatus.Occupied ->
                paintMainLayer.color = ContextCompat.getColor(
                    context,
                    occupiedColor ?: context.getThemeColor(R.attr.hall_scheme_table_occupied_background)
                )
            TableStatus.OccupiedForUser ->
                paintMainLayer.color = ContextCompat.getColor(
                    context,
                    context.getThemeColor(R.attr.hall_scheme_table_occupied_background)
                )
            else -> paintMainLayer.shader = unpressedShader
        }
    }

    private fun initContourLayerPaint() {
        paintContourLayer.style = Paint.Style.STROKE
        resetContour()
    }

    private fun initDepthLayerPaint() {
        paintDepthLayer.style = Paint.Style.FILL
        paintDepthLayer.color = ContextCompat.getColor(context, R.color.hall_scheme_table_depth_color)
    }

    override fun setViewPressed() {
        when (orderableItemUi.orderableItem.tableInfo.tableStatus) {
            TableStatus.HasReadyDishes, TableStatus.Occupied ->
                paintMainLayer.color = if (occupiedColor != null) {
                    ContextCompat.getColor(context, occupiedColor!!).and(0xC0FFFFFF.toInt())
                } else {
                    ContextCompat.getColor(
                        context,
                        context.getThemeColor(R.attr.hall_scheme_table_occupied_background_pressed)
                    )
                }
            TableStatus.OccupiedForUser ->
                paintMainLayer.color = ContextCompat.getColor(
                    context,
                    context.getThemeColor(R.attr.hall_scheme_table_occupied_background_pressed)
                )
            else -> paintMainLayer.shader = pressedShader
        }
    }

    override fun select() {
        paintMainLayer.shader = null
        paintMainLayer.color = selectionBackgroundColor
        paintContourLayer.color = selectionContourColor
        paintContourLayer.strokeWidth = strokeWidth.toFloat()
        super.select()
    }

    override fun unSelect() {
        initMainLayerPaint()
        resetContour()
        super.unSelect()
    }

    override fun selectForMultiSelect() {
        paintContourLayer.color = selectionContourColor
        paintContourLayer.strokeWidth = strokeWidth.toFloat()
        super.selectForMultiSelect()
    }

    override fun unSelectForMultiSelect() {
        resetContour()
        super.unSelectForMultiSelect()
    }

    private fun resetContour() {
        paintContourLayer.color = Color.TRANSPARENT
        paintContourLayer.strokeWidth = 0F
    }
}