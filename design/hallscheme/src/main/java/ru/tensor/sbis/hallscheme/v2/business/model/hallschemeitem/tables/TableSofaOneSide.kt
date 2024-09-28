package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType.*
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.UUID

/**
 * Стол с диваном с одной стороны.
 * id = 4, 6, 7, 8
 */
internal class TableSofaOneSide(
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
    private val sofaSpec: HallSchemeSpecHolder.SofaSpec,
    billSpec: HallSchemeSpecHolder.BillSpec,
    bookingSpec: HallSchemeSpecHolder.BookingSpec,
    assigneeSpec: HallSchemeSpecHolder.AssigneeSpec,
    chairMargin: Int = getDefaultMargin(tableSpec),
    tableInfo: TableInfo
) : TableTwoSides(
    id, cloudId, category, disposition, kind, name, type, x, y, z, sofaStyle,
    tableSpec, chairSpec, billSpec, bookingSpec, assigneeSpec, chairMargin, tableInfo
) {

    private val topPlaces by unsafeLazy {
        tableInfo.totalPlaces -
                when (type) {
                    6, 7 -> 1
                    8 -> 2
                    else -> 0
                }
    }

    override val addedLength: Int
        get() = when {
            tableInfo.totalPlaces >= 2 -> chairFactor * (topPlaces - 1)
            else -> 0
        }

    override fun getSofaParts(): List<SofaPartType> {
        val parts = mutableListOf<SofaPartType>()

        when (sofaStyle) {
            // 2 - коричневый секционный
            2 -> {
                val topSectionsCount: Int
                when (type) {
                    // L-образный слева
                    6 -> {
                        parts.add(SECTION_CORNER_LEFT)
                        parts.add(SECTION_RIGHT_TOP)
                        topSectionsCount = tableInfo.totalPlaces - 1
                    }
                    // L-образный справа
                    7 -> {
                        parts.add(SECTION_LEFT_TOP)
                        parts.add(SECTION_CORNER_RIGHT)
                        topSectionsCount = tableInfo.totalPlaces - 1
                    }
                    // U-образный
                    8 -> {
                        parts.add(SECTION_CORNER_LEFT)
                        parts.add(SECTION_CORNER_RIGHT)
                        topSectionsCount = tableInfo.totalPlaces - 2
                    }
                    else -> {
                        parts.add(SECTION_LEFT_TOP)
                        parts.add(SECTION_RIGHT_TOP)
                        topSectionsCount = tableInfo.totalPlaces
                    }
                }

                for (section in 0 until topSectionsCount) {
                    parts.add(SECTION_TOP)
                }
            }

            // 1 - серый прямой (по умолчанию)
            else -> {
                parts.add(STRAIGHT_TOP)

                when (type) {
                    // L-образный слева
                    6 -> {
                        parts.add(STRAIGHT_CORNER_LEFT)
                        parts.add(STRAIGHT_RIGHT_TOP)
                    }
                    // L-образный справа
                    7 -> {
                        parts.add(STRAIGHT_LEFT_TOP)
                        parts.add(STRAIGHT_CORNER_RIGHT)
                    }
                    // U-образный
                    8 -> {
                        parts.add(STRAIGHT_CORNER_LEFT)
                        parts.add(STRAIGHT_CORNER_RIGHT)
                    }
                    else -> {
                        parts.add(STRAIGHT_LEFT_TOP)
                        parts.add(STRAIGHT_RIGHT_TOP)
                    }
                }
            }
        }

        return parts
    }

    override fun getSofaPartBounds(sofaPartType: SofaPartType, index: Int): SchemeItemBounds {
        val y = 0
        val x: Int

        val middleX = rect.width / 2

        return when (sofaPartType) {
            STRAIGHT_LEFT_TOP -> {
                x = tablePadding.left
                SchemeItemBounds(x, y, x + sofaSpec.straightWidth, y + sofaSpec.straightHeight)
            }
            STRAIGHT_TOP -> {
                x = tablePadding.top + sofaSpec.straightWidth
                val width = rect.width - tablePadding.horizontal - sofaSpec.straightWidth * 2
                SchemeItemBounds(x, y, x + width, y + sofaSpec.straightHeight)
            }
            STRAIGHT_RIGHT_TOP -> {
                x = rect.width - tablePadding.right - sofaSpec.straightWidth
                SchemeItemBounds(x, y, x + sofaSpec.straightWidth, y + sofaSpec.straightHeight)
            }
            SECTION_LEFT_TOP -> {
                x = middleX - (topPlaces / 2.0 * sofaSpec.sectionWidth).toInt() - sofaSpec.straightWidth
                SchemeItemBounds(x, y, x + sofaSpec.straightWidth, y + sofaSpec.straightHeight)
            }
            SECTION_TOP -> {
                x = middleX + ((index * 2 - topPlaces) / 2.0 * sofaSpec.sectionWidth).toInt()
                SchemeItemBounds(x, y, x + sofaSpec.sectionWidth, y + sofaSpec.straightHeight)
            }
            SECTION_RIGHT_TOP -> {
                x = middleX + (topPlaces / 2.0 * sofaSpec.sectionWidth).toInt()
                SchemeItemBounds(x, y, x + sofaSpec.straightWidth, y + sofaSpec.straightHeight)
            }
            STRAIGHT_CORNER_LEFT, SECTION_CORNER_LEFT -> {
                x = middleX - (topPlaces / 2.0 * sofaSpec.sectionWidth).toInt() - sofaSpec.cornerWidth
                SchemeItemBounds(x, y, x + sofaSpec.cornerWidth, y + sofaSpec.cornerHeight)
            }
            STRAIGHT_CORNER_RIGHT, SECTION_CORNER_RIGHT -> {
                x = middleX + (topPlaces / 2.0 * sofaSpec.sectionWidth).toInt()
                SchemeItemBounds(x, y, x + sofaSpec.cornerWidth, y + sofaSpec.cornerHeight)
            }
            else -> super.getSofaPartBounds(sofaPartType, index)
        }
    }
}