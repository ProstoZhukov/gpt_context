package ru.tensor.sbis.hallscheme.v2

import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus

/**
 * Класс для хранения специфических цветов.
 * @author aa.gulevskiy
 */
internal data class ColorsHolder(
    val specificColorsMap: Map<TableStatus, Int>,
    val itemDefaultColor: Int
)