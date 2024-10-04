package ru.tensor.sbis.hallscheme.v2.presentation.model.bars

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.BarCorner
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения углового бара.
 * @author aa.gulevskiy
 */
internal class BarCornerUi(
    private val bar: BarCorner,
    drawablesHolder: DrawablesHolder,
    tableConfig: TableConfig,
    color: Int
) : BarUi(bar, drawablesHolder, tableConfig, color) {

    override fun constructMainLayerPath() {
        val cornerCircleDiameter = bar.tableCornerRadius * 2

        with(mainLayerPath) {
            arcTo(cornerCircleRect, 270F, -90F)
            val leftEdgeHeight = bar.verticalLineSize
            val rightEdgeHeight = leftEdgeHeight - bar.edgesHeightsDiff - bar.occupationOffset.top

            cornerCircleRect.offset(0F, leftEdgeHeight)
            arcTo(cornerCircleRect, 180F, -90F)

            cornerCircleRect.offset(bar.horizontalLineSize, 0F)
            arcTo(cornerCircleRect, 90F, -90F)

            cornerCircleRect.offset(0F, -rightEdgeHeight)
            arcTo(cornerCircleRect, 0F, -90F)

            cornerCircleRect.offset(
                -bar.horizontalLineSize + bar.topEdgeWidth + cornerCircleDiameter + bar.occupationOffset.horizontal * 2,
                -cornerCircleDiameter.toFloat()
            )
            arcTo(cornerCircleRect, 90F, 90F)

            cornerCircleRect.offset(
                -cornerCircleDiameter.toFloat() - bar.occupationOffset.left,
                -bar.edgesHeightsDiff + cornerCircleDiameter - bar.occupationOffset.top
            )
            arcTo(cornerCircleRect, 0F, -90F)

            close()
        }
    }
}