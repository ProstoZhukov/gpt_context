package ru.tensor.sbis.hallscheme.v2.presentation.model.tables

import android.graphics.RectF
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableOval
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy

/**
 * Класс для отображения овального стола со стульями по двум сторонам.
 * id = 3
 */
internal class TableOvalUi(
    private val table: TableOval,
    drawablesHolder: DrawablesHolder,
    tableConfig: TableConfig,
    color: Int
) : OrderableItemUi(table, drawablesHolder, tableConfig, color) {

    private val circleEnhancement by unsafeLazy { table.tableSpec.circleEnhancement }

    override fun constructMainLayerPath() {
        val rectF = RectF(
            table.tablePadding.left.toFloat() - circleEnhancement - table.occupationOffset.left,
            table.tablePadding.top.toFloat() - circleEnhancement - table.occupationOffset.top,
            table.tablePadding.right + table.tableTopHeight + circleEnhancement - table.occupationOffset.right,
            table.tablePadding.bottom + table.tableTopHeight + circleEnhancement - table.occupationOffset.bottom)

        with(mainLayerPath) {
            arcTo(rectF, 270F, -180F)
            rectF.offset(table.horizontalLineSize - table.verticalLineSize, 0F)
            arcTo(rectF, 90F, -180F)
            close()
        }
    }

    override fun constructBottomDepthLayerPath() {
        val rectF = RectF(
            table.tablePadding.left.toFloat() - circleEnhancement - table.occupationOffset.left,
            table.tablePadding.top.toFloat() - circleEnhancement - table.occupationOffset.top,
            table.tablePadding.right + table.tableTopHeight + circleEnhancement - table.occupationOffset.right,
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
            addArc(rectF, 180F, -90F)

            rectF.offset(table.horizontalLineSize - table.verticalLineSize, 0f)
            arcTo(rectF, 90F, -90F)

            rectF.offset(0F, -table.floatDepth)
            arcTo(rectF, 0F, 90F)

            rectF.offset(-table.horizontalLineSize + table.verticalLineSize, 0F)
            arcTo(rectF, 90F, 90F)

            close()
        }
    }

    private fun addRightDepthLayer(rectF: RectF) {
        with(depthLayerPath) {
            rectF.offset(table.horizontalLineSize - table.verticalLineSize, 0F)
            addArc(rectF, 90F, -180F)
            rectF.offset((-table.depth).toFloat(), 0F)
            arcTo(rectF, 270F, 180F)
            close()
        }
    }

    private fun addTopDepthLayer(rectF: RectF) {
        with(depthLayerPath) {
            arcTo(rectF, 270F, -90F)

            rectF.offset(0F, table.floatDepth)
            arcTo(rectF, 180F, 90F)

            rectF.offset(table.horizontalLineSize - table.verticalLineSize, 0F)
            arcTo(rectF, 270F, 90F)

            rectF.offset(0F, -table.floatDepth)
            arcTo(rectF, 0F, -90F)

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
}