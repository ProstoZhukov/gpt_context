package ru.tensor.sbis.design.logo

import android.content.Context
import android.graphics.Rect
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.logo.api.IconProvider
import ru.tensor.sbis.design.logo.api.SbisLogoApi
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.logo.utils.LogoIcon
import ru.tensor.sbis.design.logo.utils.PriorityLogoIconProvider
import ru.tensor.sbis.design.logo.utils.ResourcesIconSource
import ru.tensor.sbis.design.logo.utils.SbisLogoStyle
import ru.tensor.sbis.design.logo.utils.SbisLogoStyleHolder
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan
import ru.tensor.sbis.design.theme.HorizontalPosition.LEFT
import ru.tensor.sbis.design.theme.HorizontalPosition.RIGHT
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import ru.tensor.sbis.design.theme.res.PlatformSbisString

/**
 * Контроллер компонента логотипа.
 * Отвечает за конфигурирование компонента в зависимости от входных данных (API, типа иконки)
 *
 * @author ra.geraskin
 */
internal class LogoController(
    private val context: Context,
    private val iconProvider: IconProvider = PriorityLogoIconProvider(ResourcesIconSource(context)),
    val styleHolder: SbisLogoStyleHolder = SbisLogoStyleHolder(context)
) : SbisLogoApi {

    private lateinit var logo: View

    @get:TestOnly
    internal val smallIconHeight: Int
        get() = if (icon is LogoIcon.DefaultIcon && style is SbisLogoStyle.Navigation) {
            styleHolder.brandIconHeightNavigation
        } else {
            styleHolder.brandIconHeightPage
        }

    private val iconHorizontalMargin: Int
        get() = when {
            type.isEmptyNames(context) -> 0
            style is SbisLogoStyle.Navigation -> styleHolder.navigationIconTextOffset
            else -> styleHolder.defaultIconTextOffset
        }

    @get:TestOnly
    internal val iconWidth: Int
        get() {
            val iconHeight = when (icon) {
                is LogoIcon.BrandImage -> styleHolder.viewHeight
                is LogoIcon.BrandLogo -> smallIconHeight
                is LogoIcon.DefaultIcon -> smallIconHeight
            }
            val ratio = (icon.iconDrawable.intrinsicWidth.toFloat() / icon.iconDrawable.intrinsicHeight)
            return (iconHeight * ratio).toInt()
        }

    /** Layout текста с названием бренда и названием приложения. */
    internal val title = TextLayout().apply {
        configure {
            paint.textSize = styleHolder.logoTextSize
            paint.typeface = styleHolder.logoTextTypeFace
        }
    }

    private var brandNameSpan = ForegroundColorSpan(SbisLogoStyle.Page.getBrandNameColor(context))
    private var appNameSpan = ForegroundColorSpan(SbisLogoStyle.Page.getAppNameColor(context))

    override var type: SbisLogoType = SbisLogoType.Empty
        set(value) {
            field = value
            configureView()
        }

    override var style: SbisLogoStyle = SbisLogoStyle.Page
        set(value) {
            field = value
            configureView()
        }

    override fun setZenTheme(themeModel: ZenThemeModel) {
        style = SbisLogoStyle.Zen(themeModel)
    }

    /** @SelfDocumented */
    internal var icon: LogoIcon = iconProvider.getIcon(type.iconImage?.getOrNull(context))

    /** @SelfDocumented */
    internal fun attach(logo: View) {
        this.logo = logo
    }

    private fun configureView() {
        icon = iconProvider.getIcon(type.iconImage?.getOrNull(context))
        if (icon is LogoIcon.DefaultIcon) setIconStyleTint()
        appNameSpan = ForegroundColorSpan(style.getAppNameColor(context))
        brandNameSpan = ForegroundColorSpan(style.getBrandNameColor(context))
        title.configure {
            text = createSpannedLogoText(type.brandName, type.appName)
            paint.color = style.getSingleNameColor(context)
        }
        logo.requestLayout()
    }

    /** Расчёт ширины компонента в зависимости от типа иконки. */
    internal fun measureViewWidth(): Int = when (icon) {
        is LogoIcon.BrandImage -> measureViewWidthByBrandImage()
        is LogoIcon.BrandLogo -> measureViewWidthWithDefaultLogo()
        is LogoIcon.DefaultIcon -> measureViewWidthWithDefaultLogo()
    }

    private fun measureViewWidthByBrandImage(): Int = iconWidth

    private fun measureViewWidthWithDefaultLogo(): Int = when {
        style is SbisLogoStyle.Navigation && type.iconPosition == LEFT -> iconLeftConfigurationWithCircle()
        style is SbisLogoStyle.Navigation && type.iconPosition == RIGHT -> iconRightConfigurationWithCircle()
        type.iconPosition == LEFT -> iconLeftConfiguration()
        type.iconPosition == RIGHT -> iconRightConfiguration()
        else -> 0
    }

    private fun iconLeftConfiguration(): Int {
        var dx = 0
        icon.iconDrawable.bounds = createIconBounds(dx)
        dx += iconWidth + iconHorizontalMargin
        title.layout(dx, (styleHolder.viewHeight - title.height) / 2)
        dx += title.width
        return dx
    }

    private fun iconRightConfiguration(): Int {
        var dx = 0
        title.layout(dx, (styleHolder.viewHeight - title.height) / 2)
        dx += title.width
        icon.iconDrawable.bounds = createIconBounds(dx)
        dx += iconWidth
        return dx
    }

    private fun iconLeftConfigurationWithCircle(): Int {
        var dx = 0
        dx += styleHolder.brandIconBackgroundCircleRadius * 2
        icon.iconDrawable.bounds = createIconBounds((dx - iconWidth) / 2)
        dx += iconHorizontalMargin
        title.layout(dx, (styleHolder.viewHeight - title.height) / 2)
        dx += title.width
        return dx
    }

    private fun iconRightConfigurationWithCircle(): Int {
        var dx = 0
        title.layout(dx, (styleHolder.viewHeight - title.height) / 2)
        dx += title.width + iconHorizontalMargin
        icon.iconDrawable.bounds =
            createIconBounds(dx + (styleHolder.brandIconBackgroundCircleRadius * 2 - iconWidth) / 2)
        dx += styleHolder.brandIconBackgroundCircleRadius * 2
        return dx
    }

    private fun createIconBounds(dx: Int) = Rect(
        dx,
        (styleHolder.viewHeight - smallIconHeight) / 2,
        dx + iconWidth,
        (styleHolder.viewHeight - smallIconHeight) / 2 + smallIconHeight
    )

    private fun setIconStyleTint() = icon.iconDrawable.setTint(style.getIconColor(context))

    private fun createSpannedLogoText(
        brandNameRes: PlatformSbisString,
        appNameRes: PlatformSbisString
    ): CharSequence {
        val brandName = brandNameRes.getString(context)
        val appName = appNameRes.getString(context)
        if (appName.isEmpty()) return brandName
        val demiFontSpan = styleHolder.demiFontSpan
        return SpannableStringBuilder("$brandName$appName").apply {
            setSpan(brandNameSpan, 0, brandName.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(appNameSpan, brandName.length, this.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(CustomTypefaceSpan(demiFontSpan), brandName.length, this.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }

    private fun SbisLogoType.isEmptyNames(context: Context) =
        appName.getString(context).isEmpty() && brandName.getString(context).isEmpty()
}