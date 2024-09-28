package ru.tensor.sbis.hallscheme.v2.presentation.model.bars

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.Bar
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi

/**
 * Абстрактный класс для отображения бара (барной стойки).
 * @author aa.gulevskiy
 */
internal abstract class BarUi(
    bar: Bar,
    drawablesHolder: DrawablesHolder,
    tableConfig: TableConfig,
    color: Int
) : OrderableItemUi(bar, drawablesHolder, tableConfig, color)