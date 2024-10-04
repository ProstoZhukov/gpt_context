package ru.tensor.sbis.hallscheme.v2.presentation.model.tables

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableCircle
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy

/**
 * Класс для отображения круглого стола.
 * @author aa.gulevskiy
 */
internal class TableCircleUi(
    private val table: TableCircle,
    drawablesHolder: DrawablesHolder,
    tableConfig: TableConfig,
    color: Int
) : OrderableItemUi(table, drawablesHolder, tableConfig, color) {

    private val circleEnhancement by unsafeLazy { table.tableSpec.circleEnhancement }

    override fun constructMainLayerPath() {
        with(mainLayerPath) {
            addCircle(
                table.rect.width / 2F,
                table.rect.height / 2F,
                table.tableTopWidth / 2 + circleEnhancement,
                Path.Direction.CW
            )
        }
    }

    override fun constructBottomDepthLayerPath() {
        val rectF = RectF(
            table.tablePadding.left.toFloat() - circleEnhancement - table.occupationOffset.left,
            table.tablePadding.top.toFloat() - circleEnhancement - table.occupationOffset.top,
            table.tablePadding.right + table.tableTopWidth + circleEnhancement - table.occupationOffset.right,
            table.tablePadding.bottom + table.tableTopHeight + circleEnhancement - table.occupationOffset.bottom
        )

        when (table.itemRotation) {
            0 -> addBottomDepthLayer(rectF)
            90 -> addRightDepthLayer(rectF)
            180 -> addTopDepthLayer(rectF)
            270 -> addLeftDepthLayer(rectF)
        }
    }

    private fun addBottomDepthLayer(rectF: RectF) {
        with(depthLayerPath) {
            addArc(rectF, 180F, -180F)
            rectF.offset(0F, -table.floatDepth)
            arcTo(rectF, 0F, 180F)
            close()
        }
    }

    private fun addRightDepthLayer(rectF: RectF) {
        with(depthLayerPath) {
            addArc(rectF, 90F, -180F)
            rectF.offset(-table.floatDepth, 0F)
            arcTo(rectF, 270F, 180F)
            close()
        }
    }

    private fun addLeftDepthLayer(rectF: RectF) {
        with(depthLayerPath) {
            addArc(rectF, 270F, -180F)
            rectF.offset(table.floatDepth, 0F)
            arcTo(rectF, 90F, 180F)
            close()
        }
    }

    private fun addTopDepthLayer(rectF: RectF) {
        with(depthLayerPath) {
            addArc(rectF, 180F, 180F)
            rectF.offset(0F, table.floatDepth)
            arcTo(rectF, 0F, -180F)
            close()
        }
    }

    override fun drawFlatChairs(canvas: Canvas) {
        chairs.forEach { number ->
            val chairType = orderableItem.getChairType(number)
            val drawable = drawablesHolder.getChairFlatDrawable(chairType, color)

            drawable?.let {
                setChairDrawableBounds(number, drawable, fullHeight = false)
                canvas.save()
                canvas.rotate(orderableItem.getChairAngle(number), table.rect.width / 2f, table.rect.height / 2f)
                drawable.draw(canvas)
                canvas.restore()
            }
        }
    }

    override fun draw3dChairs(canvas: Canvas) {
        chairs.forEach { number ->
            val chairType = orderableItem.getChairType(number)
            val drawable = drawablesHolder.chair3dDrawablesMap[chairType]

            drawable?.let {
                setChairDrawableBounds(number, drawable, fullHeight = true)
                canvas.save()
                canvas.rotate(orderableItem.getChairAngle(number), table.rect.width / 2f, table.rect.height / 2f)
                drawable.draw(canvas)
                canvas.restore()
            }
        }
    }
}