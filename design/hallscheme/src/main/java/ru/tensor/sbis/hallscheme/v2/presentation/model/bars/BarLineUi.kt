package ru.tensor.sbis.hallscheme.v2.presentation.model.bars

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.BarLine
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения прямого бара.
 * @author aa.gulevskiy
 */
internal class BarLineUi(
    private val bar: BarLine,
    drawablesHolder: DrawablesHolder,
    tableConfig: TableConfig,
    color: Int
) : BarUi(bar, drawablesHolder, tableConfig, color) {

    override fun constructMainLayerPath() {
        with(mainLayerPath) {
            arcTo(cornerCircleRect, 270F, -90F)

            cornerCircleRect.offset(0F, bar.verticalLineSize)
            arcTo(cornerCircleRect, 180F, -90F)

            cornerCircleRect.offset(bar.horizontalLineSize, 0F)
            arcTo(cornerCircleRect, 90F, -90F)

            cornerCircleRect.offset(0F, -bar.verticalLineSize)
            arcTo(cornerCircleRect, 0F, -90F)
            close()
        }
    }
}