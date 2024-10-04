package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType.*
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.UUID

/**
 * Стол с диванами с двух сторон.
 * id = 5
 */
internal open class TableSofaTwoSides(
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

    override val addedLength: Int
        get() = when {
            tableInfo.totalPlaces > 2 -> chairFactor * ((tableInfo.totalPlaces - 1) / 2)
            else -> 0
        }

    override fun getSofaParts(): List<SofaPartType> {
        val parts = mutableListOf<SofaPartType>()

        when (sofaStyle) {
            // 2 - коричневый секционный
            2 -> {
                val topSectionsNumber = (tableInfo.totalPlaces + 1) / 2
                val bottomSectionsNumber = tableInfo.totalPlaces - topSectionsNumber

                for (section in 0 until topSectionsNumber) {
                    parts.add(SECTION_TOP)
                }
                parts.add(SECTION_LEFT_TOP)
                parts.add(SECTION_RIGHT_TOP)
                for (section in 0 until bottomSectionsNumber) {
                    parts.add(SECTION_BOTTOM)
                }
                parts.add(SECTION_LEFT_BOTTOM)
                parts.add(SECTION_RIGHT_BOTTOM)
            }
            // 1 - серый прямой (по умолчанию)
            else -> {
                parts.add(STRAIGHT_LEFT_TOP)
                parts.add(STRAIGHT_TOP)
                parts.add(STRAIGHT_RIGHT_TOP)
                parts.add(STRAIGHT_LEFT_BOTTOM)
                parts.add(STRAIGHT_BOTTOM)
                parts.add(STRAIGHT_RIGHT_BOTTOM)
            }
        }

        return parts
    }

    override fun getSofaPartBounds(sofaPartType: SofaPartType, index: Int): SchemeItemBounds {
        val y = when (sofaPartType) {
            in STRAIGHT_LEFT_BOTTOM..SECTION_RIGHT_BOTTOM -> rect.height - sofaSpec.straightHeight
            else -> 0
        }

        val startX = tablePadding.left
        val placesOnTop = (tableInfo.totalPlaces + 1) / 2
        val placesOnBottom = tableInfo.totalPlaces - placesOnTop

        val x: Int
        val places: Int

        return when (sofaPartType) {
            STRAIGHT_LEFT_TOP, SECTION_LEFT_TOP, STRAIGHT_LEFT_BOTTOM, SECTION_LEFT_BOTTOM -> {
                SchemeItemBounds(startX, y, startX + sofaSpec.straightWidth, y + sofaSpec.straightHeight)
            }
            STRAIGHT_TOP, STRAIGHT_BOTTOM -> {
                places = if (sofaPartType == STRAIGHT_BOTTOM) placesOnBottom else placesOnTop
                x = startX + sofaSpec.straightWidth
                val width = rect.width - tablePadding.horizontal - sofaSpec.straightWidth * 2 -
                        sofaSpec.sectionWidth * (placesOnTop - places)
                SchemeItemBounds(x, y, x + width, y + sofaSpec.straightHeight)
            }
            STRAIGHT_RIGHT_TOP, SECTION_RIGHT_TOP, STRAIGHT_RIGHT_BOTTOM, SECTION_RIGHT_BOTTOM -> {
                places = if (sofaPartType in setOf(STRAIGHT_RIGHT_BOTTOM, SECTION_RIGHT_BOTTOM))
                    placesOnBottom
                else
                    placesOnTop

                x = startX + sofaSpec.straightWidth + sofaSpec.sectionWidth * places
                SchemeItemBounds(x, y, x + sofaSpec.straightWidth, y + sofaSpec.straightHeight)
            }
            SECTION_TOP, SECTION_BOTTOM -> {
                x = startX + sofaSpec.straightWidth + sofaSpec.sectionWidth * index
                SchemeItemBounds(x, y, x + sofaSpec.sectionWidth, y + sofaSpec.straightHeight)
            }
            else -> super.getSofaPartBounds(sofaPartType, index)
        }
    }
}