package ru.tensor.sbis.design.logo

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.logo.api.SbisLogoApi
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.logo.utils.LogoIcon
import ru.tensor.sbis.design.logo.utils.SbisLogoStyle
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.HorizontalPosition.LEFT
import ru.tensor.sbis.design.theme.res.SbisDrawable
import ru.tensor.sbis.design.theme.res.createString
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.loadEnum
import kotlin.math.ceil

/**
 * Компонент логотипа.
 *
 * @author da.zolotarev
 */
class SbisLogoView private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = ID_NULL,
    private val controller: LogoController = LogoController(context)
) : View(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    SbisLogoApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        @StyleRes defStyleRes: Int = ID_NULL
    ) : this(context, attrs, defStyleAttr, defStyleRes, LogoController(context))

    init {
        controller.attach(this)
        context.withStyledAttributes(attrs, R.styleable.SbisLogo, defStyleAttr, defStyleRes) {
            style = getInt(R.styleable.SbisLogo_SbisLogo_style, SbisLogoStyle.PAGE_TYPE).let {
                when (it) {
                    SbisLogoStyle.TERMINAL_REVERSED_TYPE -> SbisLogoStyle.TerminalReversed
                    SbisLogoStyle.NAVIGATION_TYPE -> SbisLogoStyle.Navigation
                    SbisLogoStyle.TERMINAL_TYPE -> SbisLogoStyle.Terminal
                    SbisLogoStyle.PAGE_TYPE -> SbisLogoStyle.Page
                    else -> SbisLogoStyle.Page
                }
            }
            type = getInt(R.styleable.SbisLogo_SbisLogo_type, SbisLogoType.TEXT_ICON_TYPE).let {
                when (it) {
                    SbisLogoType.ICON -> SbisLogoType.Icon
                    SbisLogoType.TEXT_ICON_TYPE -> SbisLogoType.TextIcon
                    SbisLogoType.ICON_TEXT_TYPE -> SbisLogoType.IconText
                    SbisLogoType.TEXT_ICON_APP_NAME -> createTextIconAppNameFromAttrs(this)
                    else -> SbisLogoType.Empty
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(controller.measureViewWidth(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(controller.styleHolder.viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) = with(controller) {
        when (icon) {
            is LogoIcon.BrandImage -> {
                icon.iconDrawable.bounds.set(0, 0, measuredWidth, measuredHeight)
                icon.iconDrawable.draw(canvas)
            }

            is LogoIcon.BrandLogo -> {
                title.draw(canvas)
                icon.iconDrawable.draw(canvas)
            }

            is LogoIcon.DefaultIcon -> {
                if (style is SbisLogoStyle.Navigation) drawIconTextCircle(canvas)
                title.draw(canvas)
                icon.iconDrawable.draw(canvas)
            }
        }
    }

    private fun drawIconTextCircle(canvas: Canvas) = with(controller) {
        icon.iconDrawable.let {
            val cx = it.bounds.left + ceil(it.bounds.width().toFloat() / 2)
            val cy = styleHolder.viewHeight.toFloat() / 2
            canvas.drawCircle(
                cx,
                cy,
                styleHolder.brandIconBackgroundCircleRadius.toFloat(),
                styleHolder.circleIconPaint
            )
        }
    }

    private fun createTextIconAppNameFromAttrs(typedArray: TypedArray): SbisLogoType.TextIconAppName =
        with(typedArray) {
            SbisLogoType.TextIconAppName(
                appName = createString(getString(R.styleable.SbisLogo_SbisLogo_appName) ?: EMPTY_STRING),
                brandName = createString(getString(R.styleable.SbisLogo_SbisLogo_brandName) ?: EMPTY_STRING),
                iconImage = SbisDrawable.Value(getDrawable(R.styleable.SbisLogo_SbisLogo_logo)),
                iconPosition =
                loadEnum(R.styleable.SbisLogo_SbisLogo_iconHorizontalPosition, LEFT, *HorizontalPosition.values())
            )
        }

}

private const val EMPTY_STRING = ""