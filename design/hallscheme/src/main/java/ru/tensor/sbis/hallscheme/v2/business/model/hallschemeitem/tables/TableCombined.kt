package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType.*
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import java.util.UUID

/**
 * Смешанный стол (сторона с диваном + стулья напротив).
 * id = 9
 */
internal class TableCombined(
    id: UUID?,
    cloudId: Int?,
    category: String?,
    disposition: Int,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    sofaStyle: Int,
    tableSpec: HallSchemeSpecHolder.TableSpec,
    chairSpec: HallSchemeSpecHolder.ChairSpec,
    sofaSpec: HallSchemeSpecHolder.SofaSpec,
    billSpec: HallSchemeSpecHolder.BillSpec,
    bookingSpec: HallSchemeSpecHolder.BookingSpec,
    assigneeSpec: HallSchemeSpecHolder.AssigneeSpec,
    chairMargin: Int = getDefaultMargin(tableSpec),
    tableInfo: TableInfo
) : TableSofaTwoSides(
    id, cloudId, category, disposition, kind, name, type, x, y, z, sofaStyle,
    tableSpec, chairSpec, sofaSpec,  billSpec, bookingSpec, assigneeSpec, chairMargin, tableInfo
) {

    override val addedLength: Int
        get() = run {
            val places = tableInfo.totalPlaces.coerceAtLeast(4)
            when {
                places > 2 -> chairFactor * ((places - 1) / 2)
                else -> 0
            }
        }

    override fun getSofaParts(): List<SofaPartType> {
        val parts = mutableListOf<SofaPartType>()

        when (sofaStyle) {
            // 2 - коричневый секционный
            2 -> {
                for (section in 0 until (tableInfo.totalPlaces.coerceAtLeast(4) + 1) / 2) {
                    parts.add(SECTION_TOP)
                }
                parts.add(SECTION_LEFT_TOP)
                parts.add(SECTION_RIGHT_TOP)
            }

            // 1 - серый прямой (по умолчанию)
            else -> {
                parts.add(STRAIGHT_TOP)
                parts.add(STRAIGHT_LEFT_TOP)
                parts.add(STRAIGHT_RIGHT_TOP)
            }
        }

        return parts
    }
}