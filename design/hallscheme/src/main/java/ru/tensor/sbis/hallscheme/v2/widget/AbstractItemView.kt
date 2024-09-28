package ru.tensor.sbis.hallscheme.v2.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.getThemeColor
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.OrderableItem
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.Payment
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeFontDecoration
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeFontStyle
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeFontWeight
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeTextConfig
import ru.tensor.sbis.hallscheme.v2.country_feature.CountryFeatureManager
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.bars.BarUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.tables.TableCircleUi
import ru.tensor.sbis.hallscheme.v2.presentation.model.tables.TableOvalUi
import ru.tensor.sbis.hallscheme.v2.util.DpToPxConverter
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import kotlin.math.max
import ru.tensor.sbis.design.R as RDesign

/**
 * Вью для отображения столов и баров с возможностью поворота.
 * @author aa.gulevskiy
 */
internal abstract class AbstractItemView protected constructor(context: Context) : View(context) {
    private val paintOccupiedForUserLayer: Paint by unsafeLazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private val paintTableName: Paint by unsafeLazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val paintDishCount: Paint by unsafeLazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val paintSum: Paint by unsafeLazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val paintLatency: Paint by unsafeLazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    /**
     * [Paint] для рисования основного контура элемента.
     */
    protected val paintContourLayer: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * [Paint] для рисования контура выделенного элемента.
     */
    protected val selectionContourColor by unsafeLazy {
        StyleColor.PRIMARY.getColor(context)
    }

    /**
     * Цвет фона выделенного элемента.
     */
    protected val selectionBackgroundColor by unsafeLazy {
        StyleColor.PRIMARY.getColor(context)
    }

    private val primaryTextColor by unsafeLazy {
        orderableItemUi.getPrimaryTextColor(context)
    }

    private val secondaryTextColor by unsafeLazy {
        orderableItemUi.getSecondaryTextColor(context)
    }

    private val selectedTextColor by unsafeLazy {
        ContextCompat.getColor(
            context,
            context.getThemeColor(R.attr.hall_scheme_table_selected_primary_text_color)
        )
    }

    private val readyTextColor by unsafeLazy {
        TextColor.CONTRAST.getValue(context)
    }

    private var w: Int = 0
    private var h: Int = 0
    private val spacingSmall = context.resources.getDimension(R.dimen.hall_scheme_spacing_small)
    private val spacingNormal = context.resources.getDimension(R.dimen.hall_scheme_spacing_normal)
    private val tableNameMargin = spacingNormal
    private val tableRightMargin = spacingNormal

    private var infoViewLeft: Float = 0F
    private var infoViewRight: Float = 0F
    private var infoViewTop: Float = 0F
    private var infoViewTopMargin: Float = 0F

    private var textLargeSize = context.resources.getDimension(R.dimen.hall_scheme_text_large)
    private var textExtraLargeSize = context.resources.getDimension(R.dimen.hall_scheme_text_extra_large)
    private var infoSymbolSize = context.resources.getDimension(R.dimen.hall_scheme_table_info_symbol)
    private var textSmallSize = context.resources.getDimension(R.dimen.hall_scheme_text_tiny)

    private var textTitleWidth = 0F
    private var textTitleHeight = 0

    private lateinit var sum: String
    private var textSumHeight = 0
    private var textSumWidth = 0F

    private lateinit var dishCountText: String
    private var textDishCountHeight = 0
    private var textDishCountWidth = 0F

    private lateinit var latencyText: String
    private var textLatencyHeight = 0
    private var textLatencyWidth = 0F

    private var clickDownTouch = false

    /**
     * Модель элемента схемы зала.
     */
    protected lateinit var orderableItemUi: OrderableItemUi

    private val tableConfig: TableConfig
        get() = orderableItemUi.tableConfig

    // Bell
    private val bellPaint: Paint by unsafeLazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var bellSize = textLargeSize

    // Call button
    private val paintCallButton: Paint by unsafeLazy {
        Paint().apply {
            typeface = ResourcesCompat.getFont(context, RDesign.font.sbis_mobile_icons)
            color = ContextCompat.getColor(context, RDesign.color.text_orange_color)
            textSize = textLargeSize * 1.7F
        }
    }

    // Payment
    private val paintPaymentBackgroundLayer: Paint by unsafeLazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, RDesign.color.palette_colorHeader)
        }
    }

    private val paintPaymentSymbol: Paint by unsafeLazy {
        Paint().apply {
            typeface = ResourcesCompat.getFont(context, RDesign.font.sbis_mobile_icons)
            color = ContextCompat.getColor(context, RDesign.color.text_orange_color)
            textSize = infoSymbolSize
        }
    }

    private val paintPartialPaymentSymbol: Paint by unsafeLazy {
        Paint().apply {
            typeface = ResourcesCompat.getFont(context, RDesign.font.sbis_mobile_icons)
            color = ContextCompat.getColor(context, RDesign.color.colorError)
            textSize = infoSymbolSize
        }
    }

    private val paintPaymentTableNameText: Paint by unsafeLazy {
        Paint().apply {
            color = ContextCompat.getColor(context, RDesign.color.palette_color_white0)
            style = Paint.Style.FILL
            textSize = textSmallSize
        }
    }

    private val paintSelectionSymbol: Paint by unsafeLazy {
        Paint().apply {
            typeface = Typeface.create(
                ResourcesCompat.getFont(context, RDesign.font.sbis_mobile_icons),
                Typeface.BOLD
            )
            color = selectionContourColor
            textSize = textExtraLargeSize
        }
    }

    /**
     * Id столика.
     */
    val tableId by unsafeLazy { orderableItemUi.orderableItem.id }
    val cloudId by unsafeLazy { orderableItemUi.orderableItem.cloudId }

    /**
     * Название столика.
     */
    private val tableName by unsafeLazy { orderableItemUi.orderableItem.name ?: "" }

    /**
     * Статус столика.
     */
    lateinit var status: TableStatus

    private val dpToPxConverter = DpToPxConverter(this.context)

    /**
     * Ширина линии контура.
     */
    val strokeWidth = dpToPxConverter.fromDpToPixels(2F)

    /**
     * Инициализация вью.
     */
    protected fun initialize(orderableItemUi: OrderableItemUi) {
        this.orderableItemUi = orderableItemUi
        setDataForAutoTests()

        // На устройствах с Android 7 и ниже при использовании аппаратного ускорения криво работает Canvas.clipPath()
        // https://online.sbis.ru/opendoc.html?guid=b89cdc86-c874-4a34-8e18-b28e7fad7b0f
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }

        status = orderableItemUi.orderableItem.tableInfo.tableStatus

        sum = orderableItemUi.getTotalSumFormatted()
        dishCountText = orderableItemUi.getDishCountText()
        latencyText = orderableItemUi.getLatencyText(addDivider = dishCountText.isNotEmpty())

        evaluateLayoutParams()
        adjustPaint()
        initTextPaint()
        setTransparentIfNeeded(this.orderableItemUi.orderableItem)
        infoViewLeft = orderableItemUi.orderableItem.getInfoViewXCanvas()
        infoViewRight = infoViewLeft + orderableItemUi.orderableItem.getInfoViewWidth()
        infoViewTop = orderableItemUi.orderableItem.getInfoViewYCanvas() + textSmallSize

        if (orderableItemUi.orderableItem.needDrawBell()) {
            initBellPaint()
        }
    }

    private fun initBellPaint() {
        with(bellPaint) {
            typeface = ResourcesCompat.getFont(context, RDesign.font.sbis_mobile_icons)
            color = ContextCompat.getColor(context, R.color.hall_scheme_yellow)
            style = Paint.Style.FILL
            textSize = bellSize
        }
    }

    /**
     * Проставляем id для идентификации вьюшек в автотестах.
     */
    private fun setDataForAutoTests() {
        id = generateViewId()
        contentDescription = tableId.toString()
        tag = tableName
    }

    private fun setTransparentIfNeeded(orderableItem: OrderableItem) {
        alpha =
            if (orderableItem.tableInfo.tableStatus == TableStatus.OccupiedForBooking
                || orderableItem.tableInfo.tableStatus == TableStatus.Disabled) {
                0.5F
            } else {
                1F
            }
    }

    /**
     * Подготавливает [Paint].
     */
    abstract fun adjustPaint()

    private fun initTextPaint() {
        initTableNamePaint()
        initDishCountPaint()
        initSumPaint()
        initLatencyPaint()

        val rect = Rect()

        if (tableName.isNotEmpty()) {
            textTitleWidth = paintTableName.measureText(tableName)
            paintTableName.getTextBounds(tableName, 0, tableName.length, rect)
            textTitleHeight = rect.height()
        }

        if (dishCountText.isNotEmpty()) {
            textDishCountWidth = paintDishCount.measureText(dishCountText)
            paintDishCount.getTextBounds(dishCountText, 0, dishCountText.length, rect)
            textDishCountHeight = rect.height()
        }

        if (latencyText.isNotEmpty()) {
            textLatencyWidth = paintLatency.measureText(latencyText)
            paintLatency.getTextBounds(latencyText, 0, latencyText.length, rect)
            textLatencyHeight = rect.height()
        }

        if (sum.isNotEmpty()) {
            textSumWidth = paintSum.measureText(sum)
            paintSum.getTextBounds(sum, 0, sum.length, rect)
            textSumHeight = rect.height()
        }
    }

    private fun initTableNamePaint() {
        initPaint(
            paintTableName,
            tableConfig.nameTextConfig,
            secondaryTextColor,
            Typeface.DEFAULT,
            textSmallSize
        )
    }

    private fun initDishCountPaint() {
        initPaint(
            paintDishCount,
            tableConfig.dishCountTextConfig,
            secondaryTextColor,
            Typeface.DEFAULT,
            textSmallSize
        )
    }

    private fun initSumPaint() {
        initPaint(
            paintSum,
            tableConfig.sumTextConfig,
            primaryTextColor,
            Typeface.DEFAULT_BOLD,
            textLargeSize
        )
    }

    private fun initLatencyPaint() {
        initPaint(
            paintLatency,
            tableConfig.latencyTextConfig,
            secondaryTextColor,
            Typeface.DEFAULT,
            textSmallSize
        )
    }

    private fun initPaint(
        paint: Paint,
        textConfig: HallSchemeTextConfig?,
        defaultColor: Int,
        defaultTypeface: Typeface,
        defaultTextSize: Float
    ) {
        with(paint) {
            val textColor = orderableItemUi.getHallSchemeColor(context, textConfig?.color)
            color =
                if (orderableItemUi.useCustomColors && textColor != null) textColor
                else defaultColor

            style = Paint.Style.FILL

            when (textConfig?.textDecoration) {
                HallSchemeFontDecoration.UNDERLINE -> {
                    isUnderlineText = true
                }
                HallSchemeFontDecoration.LINE_THROUGH -> {
                    isStrikeThruText = true
                }
                HallSchemeFontDecoration.UNDERLINE_LINE_THROUGH -> {
                    isUnderlineText = true
                    isStrikeThruText = true
                }
                else -> {
                    isUnderlineText = false
                    isStrikeThruText = false
                }
            }

            typeface = Typeface.create(
                defaultTypeface,
                when (textConfig?.fontWeight to textConfig?.textStyle) {
                    HallSchemeFontWeight.NORMAL to HallSchemeFontStyle.ITALIC -> Typeface.ITALIC
                    HallSchemeFontWeight.BOLD to HallSchemeFontStyle.NORMAL -> Typeface.BOLD
                    HallSchemeFontWeight.BOLD to HallSchemeFontStyle.ITALIC -> Typeface.BOLD_ITALIC
                    else -> Typeface.NORMAL
                }
            )

            val configSize = textConfig?.size
            textSize = if (configSize != null) {
                dpToPxConverter.intToDp(configSize)
            } else
                defaultTextSize
        }
    }

    private fun evaluateLayoutParams() {
        with(orderableItemUi.orderableItem) {
            w = rect.right - rect.left
            h = rect.bottom - rect.top
            layoutParams =
                when (itemRotation) {
                    90, 270 -> RelativeLayout.LayoutParams(h, w)
                    else -> RelativeLayout.LayoutParams(w, h)
                }

            this@AbstractItemView.x = rect.left.toFloat() - orderableItemUi.margin
            this@AbstractItemView.y = rect.top.toFloat() - orderableItemUi.margin
            (layoutParams as RelativeLayout.LayoutParams).setMargins(
                orderableItemUi.margin, orderableItemUi.margin,
                orderableItemUi.margin, orderableItemUi.margin
            )
        }
    }

    /**
     * Поворачивает вью.
     */
    protected fun rotateItem(canvas: Canvas) {
        with(orderableItemUi.orderableItem) {
            if (itemRotation == 0) {
                return
            }

            canvas.rotate(
                itemRotation.toFloat(),
                (canvas.width / 2).toFloat(),
                (canvas.height / 2).toFloat()
            )

            when (itemRotation) {
                90, 270 -> {
                    canvas.translate((-(w / 2 - h / 2)).toFloat(), ((w / 2 - h / 2).toFloat()))
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touched = super.onTouchEvent(event)
        if (isSelected || !isEnabled) {
            return touched
        }

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clickDownTouch = true
                setViewPressed()
                invalidate()
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (clickDownTouch) {
                    clickDownTouch = false
                    setViewUnpressed()
                    invalidate()
                    true
                } else {
                    false
                }
            }
            else -> return false
        }
    }

    /**
     * Метод, срабатывающий при нажатии на вью.
     */
    abstract fun setViewPressed()

    /**
     * Метод, срабатывающий при отпускании/прекращении нажатия вью.
     */
    private fun setViewUnpressed() {
        initMainLayerPaint()
    }

    /**
     * Инициализирует [Paint] для основного слоя столешницы стола/бара.
     */
    abstract fun initMainLayerPaint()

    override fun onDraw(canvas: Canvas) {
        canvas.save()
    }

    /**
     * Рисует данные на столах.
     */
    protected fun OrderableItemUi.drawInfoWindow(canvas: Canvas) {
        val occupiedForUser = orderableItem.tableInfo.tableStatus == TableStatus.OccupiedForUser

        canvas.run {
            rotate(-orderableItem.itemRotation.toFloat())
            if (tableName.isNotEmpty() && !occupiedForUser) drawName(canvas, paintTableName)
            if (dishCountText.isNotEmpty() || latencyText.isNotEmpty()) drawDishCountAndTime(canvas)
            if (sum.isNotEmpty()) drawSum(canvas)

            restore()
        }

        if (occupiedForUser) {
            paintOccupiedForUserLayer.apply {
                typeface = ResourcesCompat.getFont(context, RDesign.font.sbis_mobile_icons)
                color = primaryTextColor
                style = Paint.Style.FILL
                textSize = textLargeSize
            }
            drawOccupiedForUserLabel(canvas)
        }

        if (orderableItem.needDrawBell()) {
            drawBell(canvas)
        }

        if (orderableItem.needShowCallButton()) {
            drawCallButton(canvas)
        }

        val payment = orderableItem.getPayment()
        if (payment != null) {
            canvas.run {
                save()
                rotateItem(canvas)
                drawPath(mainLayerPath, paintPaymentBackgroundLayer)
                rotate(-orderableItem.itemRotation.toFloat())
                if (tableName.isNotEmpty()) drawName(canvas, paintPaymentTableNameText)
                restore()
                drawPaymentSymbol(canvas, payment)
            }
        }
    }

    private fun OrderableItemUi.drawName(canvas: Canvas, paint: Paint) {
        val topMargin = infoViewTop + infoViewTopMargin

        var nameX = when (orderableItemUi) {
            is BarUi, is TableCircleUi, is TableOvalUi -> {
                infoViewLeft + orderableItem.getInfoViewWidth() / 2 - textTitleWidth / 2
            }
            else -> {
                infoViewLeft + tableNameMargin
            }
        }

        var nameY = topMargin + tableNameMargin

        var backgroundTop = (nameY - textTitleHeight - spacingSmall).toInt()
        var backgroundBottom = (nameY + spacingNormal).toInt()

        when (orderableItem.itemRotation) {
            90 -> nameX -= h.toFloat()
            270 -> {
                nameY -= w.toFloat()
                backgroundTop -= w
                backgroundBottom -= w
            }
            180 -> {
                nameX -= w.toFloat()
                nameY -= h.toFloat()
                backgroundTop -= h
                backgroundBottom -= h
            }
        }

        val backgroundLeft = (nameX - spacingNormal).toInt()
        val backgroundRight = (nameX + spacingNormal + textTitleWidth).toInt()

        var selectionSymbolLeft = nameX + textTitleWidth

        getHasReadyDishesBackground()?.let { background ->
            selectionSymbolLeft = backgroundRight.toFloat()
            paint.color = readyTextColor
            background.setBounds(backgroundLeft, backgroundTop, backgroundRight, backgroundBottom)
            background.draw(canvas)
        }

        canvas.drawText(tableName, nameX, nameY, paint)

        if (isSelected) {
            val selectionIcon = SbisMobileIcon.Icon.smi_Yes.character.toString()
            // Выравниваем относительно названия стола.
            canvas.drawText(selectionIcon, selectionSymbolLeft, nameY + spacingNormal, paintSelectionSymbol)
        }
    }

    private fun OrderableItemUi.drawDishCountAndTime(canvas: Canvas) {
        val commonWidth = textDishCountWidth + textLatencyWidth

        var textX = when (orderableItemUi) {
            is BarUi, is TableCircleUi, is TableOvalUi -> {
                infoViewLeft + orderableItem.getInfoViewWidth() / 2 - commonWidth / 2
            }
            else -> {
                infoViewRight - commonWidth - tableRightMargin
            }
        }

        var textY = getVerticalCenterForText() - max(textLatencyHeight, textDishCountHeight)

        when (orderableItem.itemRotation) {
            90 -> textX -= h.toFloat()
            270 -> textY -= w.toFloat()
            180 -> {
                textX -= w.toFloat()
                textY -= h.toFloat()
            }
        }

        canvas.drawText(dishCountText, textX, textY, paintDishCount)

        textX += textDishCountWidth
        canvas.drawText(latencyText, textX, textY, paintLatency)
    }

    private fun OrderableItemUi.drawSum(canvas: Canvas) {
        if (orderableItem.tableInfo.totalSum != null
            && (orderableItem.tableInfo.totalSum != 0.0 || orderableItem.hasDishes())) {

            val textX = when (orderableItemUi) {
                is BarUi, is TableCircleUi, is TableOvalUi -> {
                    infoViewLeft + orderableItem.getInfoViewWidth() / 2 - textSumWidth / 2
                }
                else -> {
                    infoViewRight - textSumWidth - tableRightMargin
                }
            }

            val textY = orderableItem.getInfoViewYCanvas() + orderableItem.getInfoViewHeight() - textSumHeight

            when (orderableItem.itemRotation) {
                90 -> canvas.drawText(sum, textX - h.toFloat(), textY, paintSum)
                270 -> canvas.drawText(sum, textX, textY - w.toFloat(), paintSum)
                180 -> canvas.drawText(sum, textX - w.toFloat(), textY - h.toFloat(), paintSum)
                else -> canvas.drawText(sum, textX, textY, paintSum)
            }
        }
    }

    private fun drawOccupiedForUserLabel(canvas: Canvas) {
        val text = context.getString(R.string.hall_scheme_table_occupied_for_user)
        val icon = SbisMobileIcon.Icon.smi_Time.character.toString()

        val rect = Rect()
        paintOccupiedForUserLayer.getTextBounds(icon, 0, icon.length, rect)
        val iconHeight = rect.height()

        val xPos = canvas.width / 2
        var yPos =
            (canvas.height / 2 - (paintOccupiedForUserLayer.descent() + paintOccupiedForUserLayer.ascent()) / 2 - iconHeight / 2).toInt()

        paintOccupiedForUserLayer.textAlign = Paint.Align.CENTER
        canvas.drawText(icon, xPos.toFloat(), yPos.toFloat(), paintOccupiedForUserLayer)

        yPos += (paintOccupiedForUserLayer.descent() - paintOccupiedForUserLayer.ascent()).toInt()

        paintOccupiedForUserLayer.textAlign = Paint.Align.CENTER
        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paintOccupiedForUserLayer)
    }

    private fun drawCallButton(canvas: Canvas) {
        val icon = context.getString(R.string.hall_scheme_mobile_icon_call_button)
        drawSymbolInCenter(canvas, icon, paintCallButton)
    }

    private fun drawPaymentSymbol(canvas: Canvas, payment: Payment) {
        val (icon, paint) = if (payment.partial) {
            context.getString(RDesign.string.design_mobile_icon_exclamation_mark) to paintPartialPaymentSymbol
        } else {
            CountryFeatureManager.resProvider.iconMoneyRes.character.toString() to paintPaymentSymbol
        }

        drawSymbolInCenter(canvas, icon, paint)
    }

    private fun drawSymbolInCenter(canvas: Canvas, icon: String, paint: Paint) {
        val xPos =
            if (orderableItemUi is BarUi) {
                infoViewLeft + orderableItemUi.orderableItem.getInfoViewWidth() / 2
            } else {
                canvas.width.toFloat() / 2
            }

        val yPos = getVerticalCenterForText()

        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(icon, xPos, yPos, paint)
    }

    private fun drawBell(canvas: Canvas) {
        with(orderableItemUi.orderableItem) {
            val bellIcon = SbisMobileIcon.Icon.smi_menuNotificationsFilled.character.toString()

            val rect = Rect()
            bellPaint.getTextBounds(bellIcon, 0, bellIcon.length, rect)

            // Выравниваем относительно счёта
            val xPos = rotatedPadding.left - defaultPadding - billSpec.offset - getAdditionalBillOffset()
            val yPos = rotatedPadding.top + rect.height() - chairSpecHeight

            canvas.drawText(bellIcon, xPos.toFloat(), yPos.toFloat(), bellPaint)
        }
    }

    private fun getVerticalCenterForText() =
        infoViewTop + orderableItemUi.orderableItem.getInfoViewHeight() / 2

    /**
     * Делает стол выделенным.
     */
    open fun select() {
        paintTableName.color = selectedTextColor
        paintDishCount.color = selectedTextColor
        paintLatency.color = selectedTextColor
        paintSum.color = selectedTextColor
        isSelected = true
        invalidate()
    }

    /**
     * Снимает выделение со стола.
     */
    open fun unSelect() {
        setViewUnpressed()
        paintTableName.color = secondaryTextColor
        paintDishCount.color = secondaryTextColor
        paintLatency.color = secondaryTextColor
        paintSum.color = primaryTextColor
        isSelected = false
        invalidate()
    }

    /**
     * Выделяет стол в режиме мультиселекта.
     */
    open fun selectForMultiSelect() {
        setViewUnpressed()
        isSelected = true
        invalidate()
    }

    /**
     * Снимает выделение со стола в режиме мультиселекта.
     */
    open fun unSelectForMultiSelect() {
        setViewUnpressed()
        isSelected = false
        invalidate()
    }
}