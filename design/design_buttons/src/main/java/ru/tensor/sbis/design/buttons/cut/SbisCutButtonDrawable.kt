package ru.tensor.sbis.design.buttons.cut

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.SbisCutButton
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.zentheme.plusAlpha
import ru.tensor.sbis.design.buttons.base.zentheme.setNewAlpha
import ru.tensor.sbis.design.buttons.base.utils.style.PRESSED_COLOR_STATES
import ru.tensor.sbis.design.buttons.base.utils.style.getColorFromAttr
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.base.zentheme.ButtonZenThemeSupport
import ru.tensor.sbis.design.buttons.cut.SbisCutButtonType.ACCENTED
import ru.tensor.sbis.design.buttons.cut.SbisCutButtonType.ACTIVE
import ru.tensor.sbis.design.buttons.cut.SbisCutButtonType.UNACCENTED
import ru.tensor.sbis.design.custom_view_tools.utils.StaticLayoutConfigurator
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import kotlin.math.roundToInt

/**
 * [Drawable] реализация кнопки КАТ для использования в custom view.
 * View реализация находится в [SbisCutButton].
 *
 * @author ps.smirnyh
 */
@Suppress("DeprecatedCallableAddReplaceWith")
class SbisCutButtonDrawable(
    private val context: Context,
    private val attrs: AttributeSet? = null
) : Drawable(), ButtonZenThemeSupport {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val background = RectF().apply {
        right = context.resources.getDimension(R.dimen.design_buttons_cut_button_width)
        bottom = InlineHeight.X7S.getDimenPx(context).toFloat()
    }
    private lateinit var backgroundColors: ColorStateList

    private val iconPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getSbisMobileIconTypeface(context)
    }
    private var iconWidth = 0f
    private var iconSize = 0
    private var zenThemeEnable = false
    private lateinit var iconLayout: Layout
    private lateinit var iconColors: ColorStateList

    private val cornerRadius = BorderRadius.X3S.getDimenPx(context).toFloat()

    /**
     * Режим отображения иконки.
     */
    var mode: SbisCutButtonMode = SbisCutButtonMode.ARROW_DOWN
        set(value) {
            if (field == value) return
            field = value
            updateMode()
            invalidateSelf()
        }

    /**
     * Тип иконки.
     */
    var type: SbisCutButtonType = ACCENTED
        set(value) {
            field = value
            updateType()
            invalidateSelf()
        }

    init {
        updateMode()
        loadState(attrs) {
            mode = it.loadMode()
            type = it.loadType()
        }
    }

    override fun draw(canvas: Canvas) = with(canvas) {
        drawButton()
    }

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = background.width().roundToInt()

    override fun getIntrinsicHeight(): Int = background.height().roundToInt()

    override fun onStateChange(stateSet: IntArray): Boolean {
        if (type == ACTIVE || zenThemeEnable) updateView(stateSet)
        return super.onStateChange(stateSet)
    }

    private fun updateView(stateSet: IntArray = state) {
        val pressedBackgroundColor = backgroundColors.getColorForState(stateSet, backgroundColors.defaultColor)
        backgroundPaint.color = pressedBackgroundColor

        val pressedIconColor = iconColors.getColorForState(stateSet, iconColors.defaultColor)
        iconPaint.color = pressedIconColor
    }

    private fun Canvas.drawButton() {
        drawRoundRect(background, cornerRadius, cornerRadius, backgroundPaint)
        withTranslation(
            (background.width() - iconWidth) / 2,
            (background.height() - iconLayout.height) / 2
        ) {
            iconLayout.draw(this)
        }
    }

    private fun updateMode() {
        iconSize = mode.getIconSizeDimen(context)
        iconPaint.textSize = iconSize.toFloat()
        iconWidth = iconPaint.measureText(mode.icon.toString())
        iconLayout = createLayout()
    }

    private fun updateType() {
        val style = when (type) {
            ACCENTED -> R.style.SbisCutButtonDefaultTheme
            UNACCENTED -> R.style.SbisCutButtonUnaccentedTheme
            ACTIVE -> R.style.SbisCutButtonActiveTheme
        }

        loadState(attrs, style) {
            loadStyle(it)
        }
    }

    private fun createLayout() = StaticLayoutConfigurator.createStaticLayout(
        mode.icon.toString(),
        iconPaint
    ) {
        includeFontPad = false
        alignment = Layout.Alignment.ALIGN_CENTER
        width = minOf(iconWidth.roundToInt(), background.right.roundToInt())
    }

    /**
     * Загрузить состояние из атрибутов.
     */
    private fun loadState(
        attrs: AttributeSet?,
        style: Int = R.style.SbisCutButtonDefaultTheme,
        handler: (TypedArray) -> Unit
    ) {
        context.theme.applyStyle(style, true)
        val styledAttributes = R.styleable.SbisCutButton
        context.withStyledAttributes(
            attrs,
            styledAttributes,
            R.attr.defaultSbisCutButtonTheme,
            style
        ) {
            handler(this)
        }
    }

    /**
     * Загрузка стиля из атрибутов.
     */
    private fun loadStyle(array: TypedArray) = with(array) {
        backgroundColors = ColorStateList(
            PRESSED_COLOR_STATES,
            intArrayOf(
                getColorFromAttr(R.styleable.SbisCutButton_SbisCutButton_backgroundColorPressed, Color.MAGENTA),
                getColorFromAttr(R.styleable.SbisCutButton_SbisCutButton_backgroundColor, Color.MAGENTA)
            )
        )

        iconColors = ColorStateList(
            PRESSED_COLOR_STATES,
            intArrayOf(
                getColorFromAttr(R.styleable.SbisCutButton_SbisCutButton_iconColorPressed, Color.MAGENTA),
                getColorFromAttr(R.styleable.SbisCutButton_SbisCutButton_iconColor, Color.MAGENTA)
            )
        )

        backgroundPaint.color = getColor(
            R.styleable.SbisCutButton_SbisCutButton_backgroundColor,
            Color.MAGENTA
        )

        iconPaint.color = getColor(
            R.styleable.SbisCutButton_SbisCutButton_iconColor,
            Color.MAGENTA
        )
    }

    /**
     * Загрузка режима иконки из атрибутов.
     */
    private fun TypedArray.loadMode() =
        loadEnum(
            R.styleable.SbisCutButton_SbisCutButton_mode,
            SbisCutButtonMode.ARROW_DOWN,
            *SbisCutButtonMode.values()
        )

    /**
     * Загрузка типа иконки из атрибутов.
     */
    private fun TypedArray.loadType() =
        loadEnum(
            R.styleable.SbisCutButton_SbisCutButton_type,
            ACCENTED,
            *SbisCutButtonType.values()
        )

    /**
     * Не поддерживается данным видом кнопок.
     */
    override fun setZenThemeForced(themeModel: ZenThemeModel, style: SbisButtonStyle) = setZenTheme(themeModel)

    /** @SelDocumented */
    override fun setZenTheme(themeModel: ZenThemeModel) {
        if (type != ACCENTED) return

        zenThemeEnable = true
        backgroundColors = ColorStateList(
            PRESSED_COLOR_STATES,
            intArrayOf(
                themeModel.dominantColor.setNewAlpha(.6f).plusAlpha(+.4f),
                themeModel.dominantColor.setNewAlpha(.6f)
            )
        )

        iconColors = ColorStateList(
            PRESSED_COLOR_STATES,
            intArrayOf(
                themeModel.elementsColors.labelColor.getColor(context),
                themeModel.elementsColors.labelColor.getColor(context)
            )
        )

        invalidateSelf()
        updateView()
    }
}