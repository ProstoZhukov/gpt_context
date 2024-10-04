package ru.tensor.sbis.hallscheme.v2.presentation.model.bars

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.BarSquareBracket
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения бара "скобкой".
 * @author aa.gulevskiy
 */
internal class BarSquareBracketUi(
    private val bar: BarSquareBracket,
    drawablesHolder: DrawablesHolder,
    tableConfig: TableConfig,
    color: Int
) : BarUi(bar, drawablesHolder, tableConfig, color) {

    override fun constructMainLayerPath() {
        val cornerCircleDiameter = bar.tableCornerRadius * 2

        with(mainLayerPath) {
            arcTo(cornerCircleRect, 270F, -90F)

            cornerCircleRect.offset(0F, bar.verticalLineSize)
            arcTo(cornerCircleRect, 180F, -90F)

            cornerCircleRect.offset(bar.horizontalLineSize, 0F)
            arcTo(cornerCircleRect, 90F, -90F)

            cornerCircleRect.offset(0F, -bar.verticalLineSize)
            arcTo(cornerCircleRect, 0F, -90F)

            cornerCircleRect.offset(-bar.topEdgeWidth, 0F)
            arcTo(cornerCircleRect, 270F, -90F)

            cornerCircleRect.offset(
                -cornerCircleDiameter.toFloat() - bar.occupationOffset.right,
                bar.edgesHeightsDiff - cornerCircleDiameter
            )
            arcTo(cornerCircleRect, 0F, 90F)

            val middleArea = bar.tableTopWidth - 2 * bar.topEdgeWidth - cornerCircleDiameter * 3
            cornerCircleRect.offset(-middleArea, 0F)
            arcTo(cornerCircleRect, 90F, 90F)

            cornerCircleRect.offset(
                -cornerCircleDiameter.toFloat(),
                -bar.edgesHeightsDiff + cornerCircleDiameter
            )
            arcTo(cornerCircleRect, 0F, -90F)

            close()
        }
    }
}