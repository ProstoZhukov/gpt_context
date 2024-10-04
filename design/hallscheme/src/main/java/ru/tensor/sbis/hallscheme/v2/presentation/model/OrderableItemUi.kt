package ru.tensor.sbis.hallscheme.v2.presentation.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.caverock.androidsvg.PreserveAspectRatio
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile.person.setPersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.PlanTheme
import ru.tensor.sbis.hallscheme.v2.business.BookingStatus
import ru.tensor.sbis.hallscheme.v2.business.SofaPartType
import ru.tensor.sbis.hallscheme.v2.business.TableTexture
import ru.tensor.sbis.hallscheme.v2.business.TableType
import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.Booking
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.OrderableItem
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder
import ru.tensor.sbis.hallscheme.v2.widget.FlatItemView
import ru.tensor.sbis.hallscheme.v2.widget.Item3dView
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val MAX_LATENCY_VALUE = 999L
private const val COUNT_LATENCY_DIVIDER = " | "

// Число выбрано с учётом того, что на схеме вряд ли будет больше 500 элементов, можно увеличить при необходимости
private const val MAX_ELEMENT_Z = 500F

/**
 * Абстракнтый класс для отображения элементов, на которых можно сделать заказ (столы, бары).
 * @author aa.gulevskiy
 */
internal abstract class OrderableItemUi(
    val orderableItem: OrderableItem,
    val drawablesHolder: DrawablesHolder,
    val tableConfig: TableConfig,
    val color: Int
) : HallSchemeItemUi(orderableItem) {

    /**
     * Список стульев для отображения.
     */
    protected val chairs: MutableList<Int> = mutableListOf()

    /**
     * Список изображений для дивана.
     */
    private val sofaParts: MutableList<Pair<SofaPartType, Int>> = mutableListOf()

    /**
     * Объект, представляющий геометрический путь для поверхности столешницы.
     */
    val mainLayerPath = Path()

    /**
     * Объект, представляющий геометрический путь для толщины столешницы.
     */
    val depthLayerPath = Path()

    /**
     * Прямоугольник, задающий положение левого верхнего закруглённого края столешницы.
     */
    protected val cornerCircleRect =
        RectF(
            orderableItem.cornerCircleBounds.leftTop.x,
            orderableItem.cornerCircleBounds.leftTop.y,
            orderableItem.cornerCircleBounds.rightBottom.x,
            orderableItem.cornerCircleBounds.rightBottom.y
        )

    private var planTheme = PlanTheme.THEME_FLAT
    private var tableTexture = TableTexture.TABLE_BRIGHT_WOOD

    /**
     * Отступы до объекта за пределами его координат.
     * Нужны в основном для того, чтобы объекты не "прилипали" к краям экрана.
     */
    val margin = orderableItem.defaultPadding

    private val linkedViews: MutableSet<WeakReference<View>> = mutableSetOf()

    /**
     * Создаёт объект класса Path для рисования плоского объекта.
     */
    fun createFlatPath() {
        planTheme = PlanTheme.THEME_FLAT
        constructMainLayerPath()
    }

    /**
     * Создаёт объекты класса Path для рисования 3D объекта.
     */
    fun create3dPath(tableTexture: TableTexture) {
        planTheme = PlanTheme.THEME_3D
        this.tableTexture = tableTexture
        constructMainLayerPath()
        constructBottomDepthLayerPath()
    }

    /**
     * Строит основной слой объекта.
     */
    abstract fun constructMainLayerPath()

    /**
     * Строит слой толщины столешницы для объекта.
     */
    open fun constructBottomDepthLayerPath() {
        with(depthLayerPath) {
            cornerCircleRect.offsetTo(
                orderableItem.tablePadding.left.toFloat() - orderableItem.occupationOffset.left,
                orderableItem.tablePadding.top.toFloat() + orderableItem.verticalLineSize - orderableItem.depth - orderableItem.occupationOffset.top
            )

            arcTo(cornerCircleRect, 180F, -90F)

            cornerCircleRect.offset(orderableItem.horizontalLineSize, 0F)
            arcTo(cornerCircleRect, 90F, -90F)

            cornerCircleRect.offset(0F, orderableItem.floatDepth)
            arcTo(cornerCircleRect, 0F, 90F)

            cornerCircleRect.offset(-orderableItem.horizontalLineSize, 0F)
            arcTo(cornerCircleRect, 90F, 90F)

            close()
        }
    }

    override fun draw(
        viewGroup: ViewGroup,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        orderableItem.depth = 0

        val view = getView(viewGroup)
        viewReference = WeakReference(view)
        setElementZ(view)
        viewGroup.addView(view)

        drawOutlines(viewGroup)
        drawBookings(viewGroup)
        drawBills(viewGroup)
        drawAssignmentInfo(viewGroup)

        initClickListeners(view, onItemClickListener)
    }

    override fun getView(viewGroup: ViewGroup): View {
        return FlatItemView.newInstance(viewGroup.context, this)
    }

    override fun draw3D(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        val view = get3dViewWithShaders(viewGroup, pressedShader, unpressedShader)
        viewReference = WeakReference(view)
        setElementZ(view)
        viewGroup.addView(view)

        drawOutlines(viewGroup)
        drawBookings(viewGroup)
        drawBills(viewGroup)
        drawAssignmentInfo(viewGroup)

        initClickListeners(view, onItemClickListener)
    }

    @SuppressLint("ResourceType")
    private fun drawOutlines(viewGroup: ViewGroup) {
        if (orderableItem.tableInfo.tableOutlines.isEmpty()) {
            return
        }

        // Берём значение цвета без opacity
        val strokeColor = "#" + viewGroup.context.getString(R.color.hall_scheme_gray_empty).takeLast(6)
        val fillColor = "#" + viewGroup.context.getString(R.color.hall_scheme_dark_blue).takeLast(6)

        // Устанавливаем атрибуты обводки
        val cssAttributes =
            "path { fill: $fillColor; " +
                    "fill-opacity: 0.3; " +
                    "stroke: $strokeColor; " +
                    "stroke-dasharray: 4; " +
                    "stroke-width: 2 }"

        orderableItem.tableInfo.tableOutlines.forEach { tableOutline ->
            val svgImageView = ImageView(viewGroup.context)
            svgImageView.x = tableOutline.outline.x.toFloat()
            svgImageView.y = tableOutline.outline.y.toFloat()

            svgImageView.z = MIN_DRAWING_LAYER_FOR_SCHEME_ITEM - 1

            svgImageView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            viewGroup.addView(svgImageView)
            linkedViews.add(WeakReference(svgImageView))

            try {
                val svg = SVG.getFromString(tableOutline.outline.svgPath)
                svg.documentWidth = tableOutline.outline.width.toFloat()
                svg.documentHeight = tableOutline.outline.height.toFloat()

                // Растягиваем SVG-изображение, сохраняя пропорции
                svg.documentPreserveAspectRatio = PreserveAspectRatio.LETTERBOX

                val drawable: Drawable = PictureDrawable(svg.renderToPicture(RenderOptions.create().css(cssAttributes)))

                svgImageView.setImageDrawable(drawable)
            } catch (e: SVGParseException) {
                /* ignored */
            }
        }
    }

    private fun initClickListeners(view: View, onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?) {
        onItemClickListener?.let { clickListener ->
            view.setOnClickListener { clickListener.onItemClick(orderableItem) }
            view.setOnLongClickListener {
                return@setOnLongClickListener clickListener.onItemLongTap(schemeItem)
            }
        }
    }

    override fun get3dView(viewGroup: ViewGroup): View {
        return Item3dView.newInstance(viewGroup.context, this, null, null, tableTexture.occupiedColor)
    }

    private fun get3dViewWithShaders(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader
    ): View {
        return Item3dView.newInstance(viewGroup.context, this, pressedShader, unpressedShader, tableTexture.occupiedColor)
    }

    override fun removeView() {
        linkedViews.forEach { viewReference ->
            viewReference.get()?.let { (it.parent as ViewGroup).removeView(it) }
            viewReference.clear()
        }
        super.removeView()
    }

    // chairs region start
    /**
     * Подготавливает диваны и стулья для объекта в плоской схеме.
     */
    fun calculateSofasAndChairs() {
        orderableItem.type?.let {
            when (val tableType = TableType.getByValue(it)) {
                TableType.SOFA_ONE_SIDE, TableType.SOFA_TWO_SIDES, TableType.COMBINED -> {
                    calculateSofas()
                    if (tableType == TableType.COMBINED) {
                        calculateChairs(evenOnly = true)
                    }
                }
                else -> {
                    calculateChairs()
                }
            }
        }
    }

    /**
     * Подготавливает стулья для объекта.
     * @param evenOnly - только чётные номера стульев (для смешанного типа стола).
     */
    private fun calculateChairs(evenOnly: Boolean = false) {
        val totalPlaces = orderableItem.tableInfo.totalPlaces

        // У смешанных столов с количеством мест <3 отображается только диван.
        if (evenOnly && totalPlaces < 3) return

        val allNumbers = 1..totalPlaces

        chairs.addAll(
            if (evenOnly) {
                allNumbers.filter { it % 2 == 0 }
            } else {
                allNumbers
            }
        )
    }

    /**
     * Подготавливает диваны для объекта.
     */
    private fun calculateSofas() {
        val allParts = orderableItem.getSofaParts()
        val topSections = allParts.filter { it == SofaPartType.SECTION_TOP }
        val bottomSections = allParts.filter { it == SofaPartType.SECTION_BOTTOM }
        val cornerParts = allParts.minus(topSections.toSet()).minus(bottomSections.toSet())

        topSections.forEachIndexed { index, section -> sofaParts.add(section to index) }

        bottomSections.forEachIndexed { index, section -> sofaParts.add(section to index) }

        for (part in cornerParts) {
            sofaParts.add(part to 0)
        }
    }

    protected fun setChairDrawableBounds(number: Int, drawableWithBounds: Drawable, fullHeight: Boolean) {
        val drawableBounds = orderableItem.getChairBounds(number, fullHeight)
        drawableWithBounds.bounds = drawableBounds.boundsRect
    }

    private fun setSofaDrawableBounds(sofaPartType: SofaPartType, index: Int, sofaDrawable: Drawable) {
        val drawableBounds = orderableItem.getSofaPartBounds(sofaPartType, index)
        sofaDrawable.bounds = drawableBounds.boundsRect
    }

    /**
     * Рисует стулья в плоской схеме.
     */
    open fun drawFlatChairs(canvas: Canvas) {
        chairs.forEach { number ->
            val chairType = orderableItem.getChairType(number)
            val drawable = drawablesHolder.getChairFlatDrawable(chairType, color)

            drawable?.let {
                setChairDrawableBounds(number, drawable, fullHeight = false)
                drawable.draw(canvas)
            }
        }
    }

    /**
     * Рисует стулья в объёмной схеме.
     */
    open fun draw3dChairs(canvas: Canvas) {
        chairs.forEach { number ->
            val chairType = orderableItem.getChairType(number)
            val drawable = drawablesHolder.chair3dDrawablesMap[chairType]

            drawable?.let {
                setChairDrawableBounds(number, drawable, fullHeight = true)
                drawable.draw(canvas)
            }
        }
    }

    /**
     * Рисует диваны в плоской схеме.
     */
    open fun drawFlatSofas(canvas: Canvas) {
        sofaParts.forEach { (sofaPart, index) ->
            val drawable = drawablesHolder.getSofaFlatDrawable(sofaPart, color)

            drawable?.let {
                setSofaDrawableBounds(sofaPart, index, drawable)
                drawable.draw(canvas)
            }
        }
    }

    /**
     * Рисует диваны в объёмной схеме.
     */
    open fun draw3dSofas(canvas: Canvas) {
        sofaParts.forEach { (sofaPart, index) ->
            val drawable = drawablesHolder.sofa3dDrawablesMap[sofaPart]

            drawable?.let {
                setSofaDrawableBounds(sofaPart, index, drawable)
                drawable.draw(canvas)
            }
        }
    }
    // chairs region end

    /**
     * Возвращает основной цвет элемента.
     */
    fun getPrimaryTextColor(context: Context): Int {
        return ContextCompat.getColor(
            context,
            when (orderableItem.tableInfo.tableStatus) {
                TableStatus.HasReadyDishes, TableStatus.Occupied ->
                    when (planTheme) {
                        PlanTheme.THEME_FLAT -> context.getThemeColor(R.attr.hall_scheme_table_occupied_primary_text_color)
                        PlanTheme.THEME_3D -> tableTexture.getTextColorResId()
                    }
                TableStatus.OccupiedForUser ->
                    context.getThemeColor(R.attr.hall_scheme_table_occupied_primary_text_color)
                else -> {
                    when (planTheme) {
                        PlanTheme.THEME_FLAT -> context.getThemeColor(R.attr.hall_scheme_table_empty_primary_text_color)
                        PlanTheme.THEME_3D -> tableTexture.getTextColorResId()
                    }
                }
            }
        )
    }

    /**
     * Возвращает дополнительный цвет элемента.
     */
    fun getSecondaryTextColor(context: Context): Int {
        return ContextCompat.getColor(
            context,
            when (orderableItem.tableInfo.tableStatus) {
                TableStatus.HasReadyDishes, TableStatus.Occupied ->
                    when (planTheme) {
                        PlanTheme.THEME_FLAT -> context.getThemeColor(R.attr.hall_scheme_table_occupied_secondary_text_3d_color)
                        PlanTheme.THEME_3D -> tableTexture.getTextColorResId()
                    }
                TableStatus.OccupiedForUser ->
                    context.getThemeColor(R.attr.hall_scheme_table_occupied_secondary_text_3d_color)
                else -> {
                    when (planTheme) {
                        PlanTheme.THEME_FLAT -> context.getThemeColor(R.attr.hall_scheme_table_empty_secondary_text_color)
                        PlanTheme.THEME_3D -> tableTexture.getTextColorResId()
                    }
                }
            }
        )
    }

    /**
     * Возвращает true, если используются заданные пользователем цвета.
     */
    val useCustomColors: Boolean
        get() = planTheme == PlanTheme.THEME_3D ||
                orderableItem.hasReadyDishes() ||
                orderableItem.tableInfo.tableStatus is TableStatus.Occupied ||
                orderableItem.tableInfo.tableStatus is TableStatus.OccupiedForUser

    /**
     * Возвращает отформатированную строку суммы заказов на столе.
     */
    fun getTotalSumFormatted(): String {
        val totalSum = orderableItem.tableInfo.totalSum ?: return ""
        return sumFormatter.format(totalSum.toLong())
    }

    /**
     * Возвращает фон для названия стола при наличии готовых блюд.
     */
    fun getHasReadyDishesBackground(): Drawable? {
        return if (orderableItem.tableInfo.tableStatus == TableStatus.HasReadyDishes)
            drawablesHolder.tableNameReadyBackground
        else
            null
    }

    /**
     * Возвращает строку с количеством блюд на столе.
     */
    fun getDishCountText(): String =
        if (orderableItem.hasDishes() && orderableItem.tableInfo.dishesNumber != "0")
            orderableItem.tableInfo.dishesNumber.toString()
        else ""

    /**
     * Возвращает строку с наибольшим временем приготовления блюд.
     */
    fun getLatencyText(addDivider: Boolean): String {
        val latency = orderableItem.tableInfo.maxDishLatency

        if (latency > 0) {
            val text = StringBuilder()
            if (addDivider) text.append(COUNT_LATENCY_DIVIDER)
            text.append("${latency.coerceAtMost(MAX_LATENCY_VALUE)}'")
            return text.toString()
        }

        return ""
    }
    // info view region end

    // bookings region start
    private fun drawBookings(tablesLayout: ViewGroup) {
        orderableItem.tableInfo.bookings.take(2).forEachIndexed { index, booking ->
            val bookingView = inflateBookingView(tablesLayout, index, booking)
            tablesLayout.addView(bookingView)
            linkedViews.add(WeakReference(bookingView))
        }
    }

    private fun inflateBookingView(container: ViewGroup, index: Int, booking: Booking): View {
        val inflater = LayoutInflater.from(container.context)
        return inflater.inflate(R.layout.hall_scheme_layout_table_map_booking, container, false).apply {
            val textTime = findViewById<SbisTextView>(R.id.hall_scheme_booking_time)
            val imgIcon = findViewById<SbisTextView>(R.id.hall_scheme_booking_icon)
            val textGuests = findViewById<SbisTextView>(R.id.hall_scheme_guests_amount)

            textTime.text = timeFormatter.format(booking.dateBooked)
            imgIcon.setText(booking.status.iconRes)
            textGuests.text = booking.personsAmount.toString()

            setBackgroundResource(
                when (booking.status) {
                    BookingStatus.CONFIRMED_BOOKING, BookingStatus.GUEST_COME_IN -> R.drawable.hall_scheme_bg_table_ellipse_reservation_confirmed
                    BookingStatus.LATENESS -> R.drawable.hall_scheme_bg_table_ellipse_reservation_lateness
                    else -> R.drawable.hall_scheme_bg_table_ellipse_reservation_unconfirmed
                }
            )

            (layoutParams as RelativeLayout.LayoutParams).setMargins(margin, margin, margin, margin)
            outlineProvider = null

            x = orderableItem.rotatedRect.leftTop.x -
                    margin +
                    orderableItem.rotatedPadding.left -
                    orderableItem.defaultPadding +
                    orderableItem.getBookingExtraHorizontalOffset()

            y = orderableItem.rotatedRect.rightBottom.y -
                    orderableItem.rotatedPadding.bottom -
                    margin -
                    orderableItem.bookingSpec.height / 2 +
                    index * orderableItem.bookingSpec.height -
                    index * orderableItem.bookingSpec.intersection +
                    orderableItem.getBookingExtraVerticalOffset()

            z = schemeItem.z + MAX_ELEMENT_Z
        }
    }
    // bookings region end

    // bills region start
    private fun drawBills(tablesLayout: ViewGroup) {
        // Иконку выставленного счёта выводим через отдельную view, а не рисуем на канве,
        // потому что при выставлении alpha < 1 иконка по непонятным причинам обрезается.
        // https://online.sbis.ru/opendoc.html?guid=b85b2d54-70b4-4b43-8210-3aa74cc2b8d6

        if (orderableItem.tableInfo.billNumber > 0) {
            val billView = inflateBillView(tablesLayout, 0)
            tablesLayout.addView(billView)
            linkedViews.add(WeakReference(billView))
        }
        if (orderableItem.tableInfo.billNumber > 1) {
            val billView = inflateBillView(tablesLayout, 1)
            tablesLayout.addView(billView)
            linkedViews.add(WeakReference(billView))
        }
    }

    private fun inflateBillView(container: ViewGroup, number: Int): View {
        return View(container.context).apply {
            layoutParams =
                ViewGroup.MarginLayoutParams(orderableItem.billSpec.width, orderableItem.billSpec.height).apply {
                    setMargins(margin, margin, margin, margin)
                }
            background = drawablesHolder.billDrawable

            x = orderableItem.getBillViewX() -
                    margin +
                    number * orderableItem.billSpec.offset +
                    orderableItem.getAdditionalBillOffset()
            y = orderableItem.getBillViewY() -
                    margin +
                    number * orderableItem.billSpec.offset

            z = schemeItem.z + MAX_ELEMENT_Z
        }
    }
    // bills region end

    private fun drawAssignmentInfo(tablesLayout: ViewGroup) = with(orderableItem) {
        val assignment = tableInfo.assignmentInfo

        if (assignment != null) {
            val personView: View = if (assignment.isMy) {
                LayoutInflater.from(tablesLayout.context).inflate(R.layout.hall_scheme_layout_assignee, tablesLayout, false)
            } else {
                PersonView(tablesLayout.context).apply {
                    isClickable = false
                    setSize(PhotoSize.S)
                    setShape(Shape.CIRCLE)
                    setPersonData(
                        assignment.photoUri,
                        assignment.id,
                        assignment.fullName,
                        null
                    )
                }
            }.apply {
                x = rotatedRect.leftTop.x + getInfoViewXCanvas() + (getInfoViewWidth() - assigneeSpec.size) / 2F
                y = rotatedRect.leftTop.y + getInfoViewYCanvas() + (getInfoViewHeight() - assigneeSpec.size) / 2F
                z = schemeItem.z + MAX_ELEMENT_Z
            }

            tablesLayout.addView(personView)
            linkedViews.add(WeakReference(personView))
        }
    }
}

/**
 * Форматтер для суммы.
 */
@SuppressLint("ConstantLocale")
internal val sumFormatter = (NumberFormat.getInstance(Locale.getDefault()) as DecimalFormat).apply {
    val symbols = decimalFormatSymbols
    symbols.groupingSeparator = ' '
    symbols.decimalSeparator = '.'
    decimalFormatSymbols = symbols
    maximumFractionDigits = 2
}

/**
 * Форматтер для времени.
 */
@SuppressLint("ConstantLocale")
internal val timeFormatter: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())