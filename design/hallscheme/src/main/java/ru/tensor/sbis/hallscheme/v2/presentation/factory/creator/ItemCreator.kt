package ru.tensor.sbis.hallscheme.v2.presentation.factory.creator

import android.content.Context
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.ColorsHolder
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.PlanTheme
import ru.tensor.sbis.hallscheme.v2.business.TableType
import ru.tensor.sbis.hallscheme.v2.business.model.HallSchemeModel
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.OrderableItem
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.BarCorner
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.BarLine
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.BarRounded
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.bars.BarSquareBracket
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Media
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.places.Place
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.rows.Row
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.shapes.Shape
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableCircle
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableCombined
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableFourSides
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableOval
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableSofaOneSide
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableSofaTwoSides
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.tables.TableTwoSides
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.text.TextItem
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.Outline
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableOutline
import ru.tensor.sbis.hallscheme.v2.data.HallSchemeItemDto
import ru.tensor.sbis.hallscheme.v2.data.HallSchemeModelDto
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder
import ru.tensor.sbis.hallscheme.v2.util.DpToPxConverter
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.bars.BarCornerUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.bars.BarLineUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.bars.BarRoundedUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.bars.BarSquareBracketUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.decor.*
import ru.tensor.sbis.hallscheme.v2.presentation.model.media.MediaUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.places.PlaceArmchairUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.places.PlaceSofaUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.row.RowUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.shapes.HorizontalLineItemUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.shapes.OvalItemUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.shapes.RectItemUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.shapes.VerticalLineItemUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.tables.TableCircleUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.tables.TableOvalUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.tables.TableRectUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.text.TextItemUi
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import timber.log.Timber
import java.util.*
import ru.tensor.sbis.design.R as RDesign

private const val DEFAULT_VALUE_INT = 0

/**
 * Подготавливает объекты для отображения.
 * @author aa.gulevskiy
 */
internal abstract class ItemCreator(
    private val context: Context,
    private val colorsHolder: ColorsHolder,
    private val hallSchemeSpecHolder: HallSchemeSpecHolder,
    protected val planTheme: PlanTheme
) {

    private val drawablesHolder by unsafeLazy { DrawablesHolder(context) }

    private var tableConfig: TableConfig = TableConfig(
        nameTextConfig = null,
        dishCountTextConfig = null,
        sumTextConfig = null,
        latencyTextConfig = null
    )

    private var commonOpacity: Float? = null

    private val converter: DpToPxConverter = DpToPxConverter(this.context)
    private val mapItems =
        mapOf<String, (HallSchemeItemDto) -> HallSchemeItemUi>(
            "bar" to { item -> item.mapToBar() },
            "table" to { item -> item.mapToTable() },
            "decor" to { item -> item.mapToDecor() },
            "camera" to {item -> item.mapToCamera() },
            "text" to { item -> item.createText() },
            "shape" to { item -> item.mapToShape() },
            "row" to { item -> item.mapToRow() },
            "place" to { item -> item.mapToPlace() },
            "media" to { item -> item.mapToMedia() }
        )

    /**
     * Подготавливает модель для отображения на схеме.
     */
    fun createSchemeModel(hallSchemeModelDto: HallSchemeModelDto): HallSchemeModel {
        return with(hallSchemeModelDto) {
            this@ItemCreator.commonOpacity = opacity
            this@ItemCreator.tableConfig = tableConfig

            HallSchemeModel(
                bottom = bottom ?: DEFAULT_VALUE_INT,
                top = top ?: DEFAULT_VALUE_INT,
                left = left ?: DEFAULT_VALUE_INT,
                right = right ?: DEFAULT_VALUE_INT,
                pinTables = pinTables,
                items = items
                    .map(this@ItemCreator::createItem)
                    .toMutableList(),
                background = background,
                planTheme = this@ItemCreator.planTheme,
                textureType = textureType,
                tableConfig = tableConfig,
                zoom = zoom,
                zoomBackground = zoomBackground
            )
        }
    }

    /**
     * Преобразует модели данных в конкретные модели элементов схемы, отображаемых на UI.
     */
    fun mapItems(items: List<HallSchemeItemDto>): List<HallSchemeItemUi> {
        return items.map(this@ItemCreator::createItem)
    }

    private fun createItem(hallSchemeItemDto: HallSchemeItemDto): HallSchemeItemUi {
        return with(hallSchemeItemDto) {
            mapItems[kind]?.invoke(this) ?: let {
                logNoSuchElementError()
                createText()
            }
        }
    }

    private fun HallSchemeItemDto.mapToBar(): HallSchemeItemUi {
        val bar = when (type) {
            100  -> createBarLine()
            101  -> createBarRounded()
            102  -> createBarCorner()
            103  -> createBarSquareBracket()
            else -> {
                logNoSuchElementError()
                createBarLine()
            }
        }

        createPath(bar)
        calculateChairs(bar)
        return bar
    }

    private fun HallSchemeItemDto.mapToTable(): HallSchemeItemUi {
        val table = when (type) {
            in TableType.FOUR_SIDES.values -> createTableFourSides()
            in TableType.TWO_SIDES.values -> createTableTwoSides()
            in TableType.CIRCLE.values -> createTableCircle()
            in TableType.OVAL.values -> createTableOval()
            in TableType.SOFA_ONE_SIDE.values -> createTableSofaOneSide()
            in TableType.SOFA_TWO_SIDES.values -> createTableSofaTwoSides()
            in TableType.COMBINED.values -> createTableCombined()
            else -> {
                logNoSuchElementError()
                createTableTwoSides()
            }
        }

        createPath(table)
        calculateChairs(table)
        return table
    }

    private fun HallSchemeItemDto.mapToDecor(): HallSchemeItemUi {
        return when (type) {
            500 -> createWindowSingle()
            501 -> createWindowLong()
            520 -> createDoorLeft()
            521 -> createDoorDouble()
            522 -> createDoorRight()
            540 -> createStairL()
            541 -> createStairU()
            542 -> createStairI()
            560 -> createTree1()
            561 -> createTree2()
            562 -> createTree3()
            563 -> createTree4()
            580 -> createScene()
            581 -> createRoundScene()
            600 -> createChimney()
            601 -> createLongChimney()
            602 -> createRoundChimney()
            in 620..626 -> createIcon()
            else -> {
                logNoSuchElementError()
                createTree1()
            }
        }
    }

    private fun HallSchemeItemDto.mapToCamera(): HallSchemeItemUi {
        return createText() // Камеры в МП не отображаются.
    }

    private fun HallSchemeItemDto.mapToShape(): HallSchemeItemUi {
        return when (type) {
            700  -> createRect()
            701  -> createOval()
            702  -> createHorizontalLine()
            703  -> createVerticalLine()
            else -> {
                logNoSuchElementError()
                createHorizontalLine()
            }
        }
    }

    private fun HallSchemeItemDto.mapToRow(): HallSchemeItemUi {
        return createRow()
    }

    private fun HallSchemeItemDto.mapToPlace(): HallSchemeItemUi {
        return when (type) {
            201  -> createPlaceArmChair()
            else -> createPlaceSofa() // на 30.05.2019 есть только 202 (Диван)
        }
    }

    private fun HallSchemeItemDto.mapToMedia(): HallSchemeItemUi {
        return createMedia()
    }

    private fun HallSchemeItemDto.logNoSuchElementError() {
        Timber.w(NoSuchElementException("Unknown type:$type for item with name $name"))
    }

    // Фигуры
    private fun HallSchemeItemDto.createRect(): HallSchemeItemUi {
        return RectItemUi(
            Shape(
                id, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, converter.fromDpToPixels(size?.toFloat() ?: 0F),
                color,
                fillColor,
                converter.fromDpToPixels((width ?: DEFAULT_VALUE_INT).toFloat()),
                converter.fromDpToPixels((height ?: DEFAULT_VALUE_INT).toFloat()),
                getOpacity(opacity)
            ),
            colorsHolder.itemDefaultColor
        )
    }

    private fun HallSchemeItemDto.createOval(): HallSchemeItemUi {
        return OvalItemUi(
            Shape(
                id, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, converter.fromDpToPixels(size?.toFloat() ?: 0F),
                color,
                fillColor,
                converter.fromDpToPixels((width ?: DEFAULT_VALUE_INT).toFloat()),
                converter.fromDpToPixels((height ?: DEFAULT_VALUE_INT).toFloat()),
                getOpacity(opacity)
            ),
            colorsHolder.itemDefaultColor
        )
    }

    private fun HallSchemeItemDto.createHorizontalLine(): HallSchemeItemUi {
        return HorizontalLineItemUi(
            Shape(
                id, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, converter.fromDpToPixels(size?.toFloat() ?: 0F),
                color,
                fillColor,
                converter.fromDpToPixels((width ?: DEFAULT_VALUE_INT).toFloat()),
                converter.fromDpToPixels((height ?: DEFAULT_VALUE_INT).toFloat()),
                getOpacity(opacity)
            ),
            colorsHolder.itemDefaultColor
        )
    }

    private fun HallSchemeItemDto.createVerticalLine(): HallSchemeItemUi {
        return VerticalLineItemUi(
            Shape(
                id, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, converter.fromDpToPixels(size?.toFloat() ?: 0F),
                color,
                fillColor,
                converter.fromDpToPixels((width ?: DEFAULT_VALUE_INT).toFloat()),
                converter.fromDpToPixels((height ?: DEFAULT_VALUE_INT).toFloat()),
                getOpacity(opacity)
            ),
            colorsHolder.itemDefaultColor
        )
    }


    // Текст
    private fun HallSchemeItemDto.createText(): HallSchemeItemUi {
        return TextItemUi(
            TextItem(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z,
                (if (size != null) size!! else 0),
                color,
                converter.fromDpToPixels(width?.toFloat() ?: 0F),
                converter.fromDpToPixels(height?.toFloat() ?: 0F),
                getOpacity(opacity),
                context.resources.getDimensionPixelSize(R.dimen.hall_scheme_text_item_margin)
            ),
            colorsHolder.itemDefaultColor,
            textConfig
        )
    }

    // Иконки
    private fun HallSchemeItemDto.createIcon(): HallSchemeItemUi {
        return IconItemUi(
            Decor(
                id, DEFAULT_VALUE_INT, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_icon_size),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_icon_size)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    // Камины
    private fun HallSchemeItemDto.createChimney(): HallSchemeItemUi {
        return ChimneyUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_chimney_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_chimney_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createLongChimney(): HallSchemeItemUi {
        return ChimneyLongUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_chimney2_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_chimney2_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createRoundChimney(): HallSchemeItemUi {
        return ChimneyRoundUi(
            Decor(
                id, DEFAULT_VALUE_INT, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_chimney3_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_chimney3_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    // Сцены
    private fun HallSchemeItemDto.createScene(): HallSchemeItemUi {
        return SceneUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_scene_size),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_scene_size)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createRoundScene(): HallSchemeItemUi {
        return SceneRoundUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_scene_round_size),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_scene_round_size)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    // Деревья
    private fun HallSchemeItemDto.createTree1(): HallSchemeItemUi {
        return Tree1Ui(
            Decor(
                id, DEFAULT_VALUE_INT, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_tree_size),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_tree_size)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createTree2(): HallSchemeItemUi {
        return Tree2Ui(
            Decor(
                id, DEFAULT_VALUE_INT, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_tree_size),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_tree_size)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createTree3(): HallSchemeItemUi {
        return Tree3Ui(
            Decor(
                id, DEFAULT_VALUE_INT, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_tree3_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_tree3_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createTree4(): HallSchemeItemUi {
        return Tree4GreenWallUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_tree4_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_tree4_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    // Лестницы
    private fun HallSchemeItemDto.createStairL(): HallSchemeItemUi {
        return StairLUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_stair_l_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_stair_l_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createStairU(): HallSchemeItemUi {
        return StairUUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_stair_u_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_stair_u_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createStairI(): HallSchemeItemUi {
        return StairIUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_stair_i_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_stair_i_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    // Двери
    private fun HallSchemeItemDto.createDoorLeft(): HallSchemeItemUi {
        return DoorLeftUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_door_single_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_door_single_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createDoorDouble(): HallSchemeItemUi {
        return DoorDoubleUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_door_double_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_door_double_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createDoorRight(): HallSchemeItemUi {
        return DoorRightUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_door_single_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_door_single_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    // Окна
    private fun HallSchemeItemDto.createWindowSingle(): HallSchemeItemUi {
        return WindowSingleUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_window_single_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_window_single_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createWindowLong(): HallSchemeItemUi {
        return WindowLongUi(
            Decor(
                id, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.DecorSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_window_long_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_window_long_height)
                ),
                getOpacity(opacity)
            ),
            drawablesHolder
        )
    }

    private fun getOpacity(itemOpacity: Float?): Float {
        val noOpacity = 1F
        val opacity = itemOpacity ?: (commonOpacity ?: noOpacity)

        return if (opacity <= noOpacity) {
            opacity
        } else {
            noOpacity
        }
    }

    // Столы
    private fun HallSchemeItemDto.createTableFourSides(): OrderableItemUi {
        val item = TableFourSides(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            tableInfo = convertTableInfo()
        )

        return TableRectUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createTableTwoSides(): OrderableItemUi {
        val item = TableTwoSides(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            tableInfo = convertTableInfo()
        )

        return TableRectUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createTableCircle(): OrderableItemUi {
        val item = TableCircle(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            tableInfo = convertTableInfo()
        )

        return TableCircleUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createTableOval(): OrderableItemUi {
        val item = TableOval(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            tableInfo = convertTableInfo()
        )

        return TableOvalUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createTableSofaOneSide(): OrderableItemUi {
        val item = TableSofaOneSide(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.sofaSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            tableInfo = convertTableInfo()
        )

        return TableRectUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createTableSofaTwoSides(): OrderableItemUi {
        val item = TableSofaTwoSides(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.sofaSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            tableInfo = convertTableInfo()
        )

        return TableRectUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createTableCombined(): OrderableItemUi {
        val item = TableCombined(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.sofaSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            tableInfo = convertTableInfo()
        )

        return TableRectUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    // Бары
    private fun HallSchemeItemDto.createBarLine(): OrderableItemUi {
        val item = BarLine(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()), z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            tableInfo = convertTableInfo()
        )

        return BarLineUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createBarRounded(): OrderableItemUi {
        val item = BarRounded(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            HallSchemeSpecHolder.BARS_HEIGHT_FACTOR,
            tableInfo = convertTableInfo()
        )

        return BarRoundedUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createBarCorner(): OrderableItemUi {
        val item = BarCorner(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            HallSchemeSpecHolder.BARS_HEIGHT_FACTOR,
            HallSchemeSpecHolder.BARS_TOP_EDGE_WIDTH_FACTOR,
            tableInfo = convertTableInfo()
        )

        return BarCornerUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.createBarSquareBracket(): OrderableItemUi {
        val item = BarSquareBracket(
            id, cloudId, category, disposition, kind, name,
            type,
            converter.fromDpToPixels(coordinateX.toFloat()),
            converter.fromDpToPixels(coordinateY.toFloat()),
            z,
            sofaStyle,
            hallSchemeSpecHolder.tableSpec,
            hallSchemeSpecHolder.chairSpec,
            hallSchemeSpecHolder.billSpec,
            hallSchemeSpecHolder.bookingSpec,
            hallSchemeSpecHolder.assigneeSpec,
            HallSchemeSpecHolder.BARS_HEIGHT_FACTOR,
            HallSchemeSpecHolder.BARS_TOP_EDGE_WIDTH_FACTOR,
            tableInfo = convertTableInfo()
        )

        return BarSquareBracketUi(item, drawablesHolder, tableConfig, getItemColor(item))
    }

    private fun HallSchemeItemDto.convertTableInfo(): TableInfo {
        fun convertOutline(tableOutlines: List<TableOutline>?): List<TableOutline> {
            if (tableOutlines.isNullOrEmpty()) return emptyList()

            return tableOutlines.map {
                with(it.outline) {
                    TableOutline(
                        Outline(
                            svgPath,
                            converter.fromDpToPixels(width),
                            converter.fromDpToPixels(height),
                            converter.fromDpToPixels(x),
                            converter.fromDpToPixels(y)
                        ), it.tableIds
                    )
                }
            }
        }

        return tableInfo?.let { it.copy(tableOutlines = convertOutline(it.tableOutlines)) } ?: TableInfo()
    }

    // Места
    private fun HallSchemeItemDto.createPlaceArmChair(): HallSchemeItemUi {
        return PlaceArmchairUi(
            Place(
                id, category, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.PlaceSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_place_armchair_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_place_armchair_height)
                ),
                context.resources.getDimensionPixelSize(R.dimen.hall_scheme_spacing_normal)
            ),
            drawablesHolder
        )
    }

    private fun HallSchemeItemDto.createPlaceSofa(): HallSchemeItemUi {
        return PlaceSofaUi(
            Place(
                id, category, disposition, kind, name, type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z, HallSchemeSpecHolder.PlaceSpec(
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_place_sofa_width),
                    context.resources.getDimensionPixelSize(R.dimen.hall_scheme_place_sofa_height)
                ),
                context.resources.getDimensionPixelSize(R.dimen.hall_scheme_spacing_normal)
            ),
            drawablesHolder
        )
    }

    // Ряды
    private fun HallSchemeItemDto.createRow(): HallSchemeItemUi {
        return RowUi(
            Row(
                id,
                category,
                disposition,
                kind,
                name,
                type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z,
                placeFrom ?: DEFAULT_VALUE_INT,
                placeTo ?: DEFAULT_VALUE_INT,
                rowFrom ?: DEFAULT_VALUE_INT,
                rowTo ?: DEFAULT_VALUE_INT,
                showLeftLabel ?: false,
                showRightLabel ?: false,
                context.resources.getDimensionPixelSize(R.dimen.hall_scheme_row_place_size),
                context.resources.getDimensionPixelSize(R.dimen.hall_scheme_row_place_margin)
            )
        )
    }

    // Произвольная картинка
    private fun HallSchemeItemDto.createMedia(): HallSchemeItemUi {
        return MediaUi(
            Media(
                id,
                disposition,
                kind,
                name,
                type,
                converter.fromDpToPixels(coordinateX.toFloat()),
                converter.fromDpToPixels(coordinateY.toFloat()),
                z,
                converter.fromDpToPixels((width ?: DEFAULT_VALUE_INT).toFloat()),
                converter.fromDpToPixels((height ?: DEFAULT_VALUE_INT).toFloat()),
                getOpacity(opacity),
                url
            )
        )
    }

    /**
     * Строит геометрический путь (кривую) для объекта.
     */
    abstract fun createPath(itemUi: OrderableItemUi)

    /**
     * Подготавливает стулья (выбирает изображение и считает размеры).
     */
    abstract fun calculateChairs(itemUi: OrderableItemUi)

    /**
     * Возвращает цвет для контура стола и стула.
     */
    private fun getItemColor(orderableItem: OrderableItem): Int {
        return colorsHolder.specificColorsMap[orderableItem.tableInfo.tableStatus] ?: RDesign.color.palette_color_white0
    }
}