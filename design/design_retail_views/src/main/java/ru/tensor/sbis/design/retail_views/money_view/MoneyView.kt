package ru.tensor.sbis.design.retail_views.money_view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ColorStateListInflaterCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.retail_models.utils.roundHalfUp
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.common.span.BottomAlignSpan
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * SbisTextView для отображения денежных полей
 * Формат суммы:  567 478.23
 *
 * Пример атрибутов:
 * <item name="retail_views_money_color_integer_state">@color/money_integer_state</item> - StateListColor для целой части
 * <item name="retail_views_money_color_decimal_state">@color/money_decimal_state</item> - StateListColor для десятичной части
 * <item name="retail_views_money_size_integer">@color/color</item>  - размер целой части
 * <item name="retail_views_money_size_decimal">@color/color</item>  - размер десятичной части
 * <item name="retail_views_money_color_integer">@color/color</item> - цвет целой части
 * <item name="retail_views_money_color_decimal">@color/color</item> - цвет десятичной части вместе с точкой
 * <item name="retail_views_money_alpha_decimal">0.6</item> - уровень прозрачности десятичной части вместе с точкой
 * <item name="retail_views_money_bold_integer">@color/color</item>  - жирный стиль целой части. По умолчанию false
 * <item name="retail_views_money_bottom_align">true</item>  - прижимать ли весь текст к низу. По умолчанию true
 * <item name="retail_views_money_show_kopecka_if_zero">false</item> - показывать ли копейки. По умолчанию false
 *
 * Пример использования в binding:
 * app:showMoney="@{BigDecimal"}          - полностью сумма          567.56 -> 567.56
 * app:showInteger="@{BigDecimal"}        - только целая часть       567.56 -> 567
 * app:showDecimal="@{BigDecimal"}        - только десятичная часть  567.56 -> .56
 * app:showUnit="@{String}"               - еденица измерения
 *
 * @see ru.tensor.retail.cashboxescommon.ui.BindingAttribute
 */
@SuppressLint("CustomViewStyleable")
class MoneyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = ru.tensor.sbis.design.sbis_text_view.R.attr.sbisTextViewTheme
) : SbisTextView(
    context.applyStyle(R.attr.retail_views_money_theme, R.style.RetailViewsMoneyViewTheme_Light),
    attrs,
    defStyleAttr
) {

    private var colorIntegerStateList: ColorStateList?
    private var colorDecimalStateList: ColorStateList?
    private var colorInteger: Int
    private var colorDecimal: Int
    private var sizeInteger: Int
    private var sizeDecimal: Int
    private var boldInteger: Boolean
    private var bottomAlign: Boolean
    private var showKopecka: Boolean
    private var splitUnit: Boolean
    private var decimalAlpha: Float = 1f

    private var quantityFull: BigDecimal? = null
    private var moneyFull: BigDecimal? = null
    private var moneyInteger: BigDecimal? = null
    private var moneyDecimal: BigDecimal? = null
    private var unit: String? = null

    private fun isDecimalPartOfBigDecimalEqualsZero(bigDecimal: BigDecimal): Boolean {
        val decimalPart = bigDecimal.remainder(BigDecimal.ONE)
        return decimalPart.compareTo(BigDecimal.ZERO) == 0
    }

    private fun getOnlyDecimalAsString(bigDecimal: BigDecimal): String =
        decimalFormat.format(bigDecimal.remainder(BigDecimal.ONE))

    private fun getQuantityOnlyDecimalAsString(bigDecimal: BigDecimal): String =
        quantityFormat.format(bigDecimal.remainder(BigDecimal.ONE))

    private fun getOnlyDecimalAsString(bigDecimal: BigDecimal, isQuantity: Boolean): String {
        val onlyDecimalAsString = if (isQuantity) {
            getQuantityOnlyDecimalAsString(bigDecimal.abs())
        } else {
            getOnlyDecimalAsString(bigDecimal.abs())
        }

        return if (isDecimalPartOfBigDecimalEqualsZero(bigDecimal) && !showKopecka) {
            ""
        } else {
            onlyDecimalAsString.substring(1)
        }
    }

    init {
        val attributes = getContext().obtainStyledAttributes(attrs, R.styleable.RetailMoneyViewAttrs)

        colorInteger = attributes.getInteger(
            R.styleable.RetailMoneyViewAttrs_retail_views_money_color_integer,
            0
        )
        colorDecimal = attributes.getInteger(
            R.styleable.RetailMoneyViewAttrs_retail_views_money_color_decimal,
            0
        )
        sizeInteger = attributes.getDimensionPixelSize(
            R.styleable.RetailMoneyViewAttrs_retail_views_money_size_integer,
            0
        )
        sizeDecimal = attributes.getDimensionPixelSize(
            R.styleable.RetailMoneyViewAttrs_retail_views_money_size_decimal,
            0
        )
        boldInteger = attributes.getBoolean(
            R.styleable.RetailMoneyViewAttrs_retail_views_money_bold_integer,
            false
        )
        bottomAlign = attributes.getBoolean(
            R.styleable.RetailMoneyViewAttrs_retail_views_money_bottom_align,
            true
        )
        showKopecka = attributes.getBoolean(
            R.styleable.RetailMoneyViewAttrs_retail_views_money_show_kopecka_if_zero,
            false
        )
        splitUnit = attributes.getBoolean(
            R.styleable.RetailMoneyViewAttrs_retail_views_money_split_unit,
            false
        )

        colorIntegerStateList = attributes.getColorStateListCompat(
            getContext(),
            R.styleable.RetailMoneyViewAttrs,
            R.styleable.RetailMoneyViewAttrs_retail_views_money_color_integer_state
        )
        colorDecimalStateList = attributes.getColorStateListCompat(
            getContext(),
            R.styleable.RetailMoneyViewAttrs,
            R.styleable.RetailMoneyViewAttrs_retail_views_money_color_decimal_state
        )

        updateDecimalAlpha(
            alpha = attributes.getFloat(
                R.styleable.RetailMoneyViewAttrs_retail_views_money_alpha_decimal,
                1f
            )
        )

        if (attributes.hasValue(R.styleable.RetailMoneyViewAttrs_retail_views_money)) {
            val money = attributes.getFloat(R.styleable.RetailMoneyViewAttrs_retail_views_money, 0f)
            showMoney(money.toBigDecimal())
        }

        attributes.recycle()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        if (colorIntegerStateList != null && colorIntegerStateList!!.isStateful ||
            colorDecimalStateList != null && colorDecimalStateList!!.isStateful
        ) {
            updateColors()
        }
    }

    /** Установить цвет целой части */
    fun setColorIntegerAttr(@AttrRes integerColorAttr: Int) {
        colorInteger = context.getThemeColorInt(integerColorAttr)
        updateColors()
    }

    /** Установить цвет целой и дробной части */
    fun setColorNumberAttr(@AttrRes integerColorAttr: Int) {
        colorInteger = context.getThemeColorInt(integerColorAttr)
        colorDecimal = context.getThemeColorInt(integerColorAttr)
        updateColors()
    }

    fun setColorIntegerStateListAttr(@AttrRes integerColorStateListAttr: Int?) {
        integerColorStateListAttr?.let {
            colorIntegerStateList = context.getColorStateListFromAttrCompat(it)
            updateColors()
        }
    }

    fun setColorDecimalStateListAttr(@AttrRes decimalColorStateListAttr: Int?) {
        decimalColorStateListAttr?.let {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(decimalColorStateListAttr, typedValue, true)
            colorDecimalStateList = ContextCompat.getColorStateList(context, typedValue.resourceId)
        }
    }

    private fun updateDecimalAlpha(alpha: Float = decimalAlpha) {
        decimalAlpha = alpha
        colorDecimal = ColorUtils.setAlphaComponent(colorDecimal, (255 * alpha).toInt())
    }

    /**
     * Отображение денежного поля
     */
    fun showMoney(moneyBD: BigDecimal?) {
        moneyFull = moneyBD
        text = buildMoneyText(moneyBD)
    }

    fun showQuantity(quantityBD: BigDecimal) {
        quantityFull = quantityBD
        text = buildQuantity(quantityBD)
    }

    /**
     * Отображение еденицы измерения
     */
    fun showUnit(unit: String?) {
        this.unit = unit
        text = buildQuantity(quantityFull)
    }

    private fun buildQuantity(quantityBD: BigDecimal?): CharSequence {
        val integerStringSpannable = getIntegerSpannable(quantityBD ?: BigDecimal(0))
        val decimalStringSpannable =
            getDecimalStringSpannable(quantityBD ?: BigDecimal(0), true)

        val quantity = SpannableStringBuilder()
            .append(integerStringSpannable)
            .append(decimalStringSpannable)

        if (unit.isNullOrEmpty()) {
            return quantity
        }
        return quantity.append(getMeasureUnitSpannable())
    }

    private fun buildMoneyText(money: BigDecimal?): CharSequence {
        /**
         * Перед раздельной подготовкой текста целой и десятичной части
         * предварительно округляем значение до макс. кол-ва отображаемых знаков.
         * Чтобы число 1.999 с форматом до 2 знаков обрабатывалось не как
         * 1.999 -> 1 + 0.999 -> 1 + 0.00 -> 1.00,
         * а как 1.999 -> 2.00 -> 2 + 0.00 -> 2.00
         */
        val roundedMoney = money?.roundHalfUp(newScale = decimalFormat.maximumFractionDigits) ?: BigDecimal(0)
        val integerStringSpannable = getIntegerSpannable(roundedMoney)
        val decimalStringSpannable = getDecimalStringSpannable(roundedMoney, isQuantity = false)

        return SpannableStringBuilder()
            .append(integerStringSpannable)
            .append(decimalStringSpannable)
    }

    /**
     * Отображение только целого
     */
    fun showInteger(moneyBD: BigDecimal) {
        moneyInteger = moneyBD
        text = getIntegerSpannable(moneyBD)
    }

    /**
     * Отображение только десятичного
     */
    fun showDecimal(moneyBD: BigDecimal) {
        moneyDecimal = moneyBD
        text = getDecimalStringSpannable(moneyBD, false)
    }

    /** Измерить ширину размещаемого текста для предполагаемого значения [value]. */
    fun measureText(value: BigDecimal, isQuantity: Boolean = quantityFull != null): Float {
        val text = if (isQuantity) {
            buildQuantity(value)
        } else {
            buildMoneyText(value)
        }
        return measureText(text)
    }

    private fun getDecimalStringSpannable(bigDecimal: BigDecimal, isQuantity: Boolean): SpannableString {
        val decimalString = getOnlyDecimalAsString(bigDecimal, isQuantity)

        return SpannableString(decimalString).apply {
            if (colorDecimal != 0) {
                setSpan(ForegroundColorSpan(colorDecimal), 0, this.length, 0)
            }
            if (sizeDecimal != 0) {
                setSpan(AbsoluteSizeSpan(sizeDecimal, false), 0, this.length, 0)
            }

            if (bottomAlign) setSpan(BottomAlignSpan(), 0, this.length, 0)
        }
    }

    private fun getMeasureUnitSpannable(): Spannable? {
        if (unit == null) return null
        val unit = if (splitUnit) unit else " $unit"

        return SpannableString(unit).apply {
            if (colorDecimal != 0) {
                setSpan(ForegroundColorSpan(colorDecimal), 0, this.length, 0)
            }
            if (sizeDecimal != 0) {
                setSpan(AbsoluteSizeSpan(sizeDecimal, false), 0, this.length, 0)
            }
            if (bottomAlign) setSpan(BottomAlignSpan(), 0, this.length, 0)
        }
    }

    private fun getIntegerSpannable(bigDecimal: BigDecimal): SpannableString {
        val integerMoneyBI = bigDecimal.toBigInteger()
        val isLessThanZero = bigDecimal < BigDecimal.ZERO && bigDecimal > BigDecimal(MINUS_ONE)
        val integerFormatString = if (isLessThanZero) {
            MINUS.plus(integerFormat.format(integerMoneyBI.toLong()))
        } else {
            integerFormat.format(integerMoneyBI.toLong())
        }

        return SpannableString(integerFormatString).apply {
            if (boldInteger) {
                setSpan(StyleSpan(Typeface.BOLD), 0, this.length, 0)
            }
            if (colorInteger != 0) {
                setSpan(ForegroundColorSpan(colorInteger), 0, this.length, 0)
            }

            if (sizeInteger != 0) {
                setSpan(AbsoluteSizeSpan(sizeInteger, false), 0, this.length, 0)
            }

            if (bottomAlign) setSpan(BottomAlignSpan(), 0, this.length, 0)
        }
    }

    private fun updateColors() {
        var inval = false
        colorIntegerStateList?.let {
            colorInteger = it.getColorForState(drawableState, 0)
            inval = true
        }

        colorDecimalStateList?.let {
            colorDecimal = it.getColorForState(drawableState, 0)
            inval = true
        }

        updateDecimalAlpha()

        if (inval) {
            invalidate()
            moneyFull?.let { showMoney(it) }
            moneyInteger?.let { showInteger(it) }
            moneyDecimal?.let { showDecimal(it) }
            quantityFull?.let { showQuantity(it) }
        }
    }

    private companion object {
        const val MINUS_ONE = -1
        const val MINUS = "-"

        val formatSymbols = DecimalFormatSymbols().apply {
            groupingSeparator = ' '
            decimalSeparator = '.'
        }

        val quantityFormat = DecimalFormat().apply {
            maximumFractionDigits = 3
            minimumFractionDigits = 0
            decimalFormatSymbols = formatSymbols
        }

        val decimalFormat = DecimalFormat().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
            decimalFormatSymbols = formatSymbols
        }

        val integerFormat = DecimalFormat().apply {
            maximumFractionDigits = 0
            decimalFormatSymbols = formatSymbols
        }
    }

    /**
     * Функция, решающая ошибку https://online.sbis.ru/opendoc.html?guid=e8b01358-d6f9-475a-8321-64ed9bd0e098
     * В Android 5 не поддерживается использование аттрибутов темы в качестве ссылки на ресурс внутри <selector>
     * Из за чего инлфейт ColorStateList при инициализации вью из [AttributeSet] производится некорректно.
     * Функция позволяет корректно установить селектор с цветами текста через аттрибуты класса вью в том случае
     */
    private fun TypedArray.getColorStateListCompat(context: Context, attrs: IntArray, index: Int): ColorStateList? {
        return if (Build.VERSION.SDK_INT >= 23) {
            getColorStateList(index)
        } else {
            getResourceId(index, ResourcesCompat.ID_NULL)
                .takeIf { it != ResourcesCompat.ID_NULL }
                ?.let {
                    val appearance = context.obtainStyledAttributes(it, attrs)
                    appearance.getColorStateList(index)
                        .also {
                            appearance.recycle()
                        }
                }
        }
    }

    /**
     * Функция для получения правильного [ColorStateList] из атрибута темы, работает на Android API ниже версии 23
     */
    @SuppressLint("RestrictedApi") // Без хака, к сожалению, не получится пофиксить
    private fun Context.getColorStateListFromAttrCompat(attr: Int): ColorStateList? {
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return if (Build.VERSION.SDK_INT >= 23) {
            ContextCompat.getColorStateList(this, typedValue.resourceId)
        } else {
            val parser = resources.getXml(typedValue.resourceId)
            ColorStateListInflaterCompat.createFromXml(resources, parser, theme)
        }
    }
}