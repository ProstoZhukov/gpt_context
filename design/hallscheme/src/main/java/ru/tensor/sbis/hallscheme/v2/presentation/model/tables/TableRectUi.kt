package ru.tensor.sbis.hallscheme.v2.presentation.model.tables

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableFourSides
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi

/**
 * Класс для отображения прямоугольного стола.
 * @author aa.gulevskiy
 */
internal class TableRectUi(
    private val table: TableFourSides,
    drawablesHolder: DrawablesHolder,
    tableConfig: TableConfig,
    color: Int
) : OrderableItemUi(table, drawablesHolder, tableConfig, color) {

    override fun constructMainLayerPath() {
        with(mainLayerPath) {
            arcTo(cornerCircleRect, 270F, -90F)

            cornerCircleRect.offset(0F, table.verticalLineSize)
            arcTo(cornerCircleRect, 180F, -90F)

            cornerCircleRect.offset(table.horizontalLineSize, 0F)
            arcTo(cornerCircleRect, 90F, -90F)

            cornerCircleRect.offset(0F, -table.verticalLineSize)
            arcTo(cornerCircleRect, 0F, -90F)
            close()
        }
    }

    override fun constructBottomDepthLayerPath() {
        when (table.itemRotation) {
            0 -> super.constructBottomDepthLayerPath()
            90 -> addRightDepthLayer()
            180 -> addTopDepthLayer()
            270 -> addLeftDepthLayer()
        }
    }

    private fun addRightDepthLayer() {
        with(depthLayerPath) {
            arcTo(cornerCircleRect, 0F, -90F)

            cornerCircleRect.offset((-table.depth).toFloat(), 0F)
            arcTo(cornerCircleRect, 270F, 90F)

            cornerCircleRect.offset(0F, table.verticalLineSize)
            arcTo(cornerCircleRect, 0F, 90F)

            cornerCircleRect.offset(table.floatDepth, 0F)
            arcTo(cornerCircleRect, 90F, -90F)

            close()
        }
    }

    private fun addTopDepthLayer() {
        with(depthLayerPath) {
            cornerCircleRect.offsetTo(
                table.tablePadding.left.toFloat() - table.occupationOffset.left,
                table.tablePadding.top.toFloat() - table.occupationOffset.top
            )
            arcTo(cornerCircleRect, 270F, -90F)

            cornerCircleRect.offset(0F, table.floatDepth)
            arcTo(cornerCircleRect, 180F, 90F)

            cornerCircleRect.offset(table.horizontalLineSize, 0F)
            arcTo(cornerCircleRect, 270F, 90F)

            cornerCircleRect.offset(0F, -table.floatDepth)
            arcTo(cornerCircleRect, 0F, -90F)

            close()
        }
    }

    private fun addLeftDepthLayer() {
        with(depthLayerPath) {
            cornerCircleRect.offsetTo(
                table.tablePadding.left.toFloat() - table.occupationOffset.left,
                table.tablePadding.top.toFloat() - table.occupationOffset.top
            )
            arcTo(cornerCircleRect, 270F, -90F)

            cornerCircleRect.offset(0F, table.verticalLineSize)
            arcTo(cornerCircleRect, 180F, -90F)

            cornerCircleRect.offset(table.floatDepth, 0F)
            arcTo(cornerCircleRect, 90F, 90F)

            cornerCircleRect.offset(0F, -table.verticalLineSize)
            arcTo(cornerCircleRect, 180F, 90F)

            close()
        }
    }
}