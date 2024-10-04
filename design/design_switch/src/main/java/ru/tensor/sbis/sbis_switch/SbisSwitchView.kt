package ru.tensor.sbis.sbis_switch

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.sbis_switch.SbisSwitchSection.*
import android.R as RAndroid

/**
 * Надстройка над Switch Compat, которая переопределяет цвета на основе кастомных аттрибутов
 *
 * @author da.zolotarev
 */
class SbisSwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.sbisSwitchTheme,
    @StyleRes defStyleRes: Int = R.style.SbisSwitchPrimaryContrastTheme
) : SwitchCompat(ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr) {

    @ColorInt
    private val defaultColor = Color.MAGENTA

    @ColorInt
    private var thumbCheckedColor: Int = defaultColor

    @ColorInt
    private var thumbUncheckedColor: Int = defaultColor

    @ColorInt
    private var thumbDisabledCheckedColor: Int = defaultColor

    @ColorInt
    private var thumbDisabledUncheckedColor = defaultColor

    @ColorInt
    private var trackCheckedColor: Int = defaultColor

    @ColorInt
    private var trackUncheckedColor: Int = defaultColor

    @ColorInt
    private var trackDisabledCheckedColor: Int = defaultColor

    @ColorInt
    private var trackDisabledUncheckedColor: Int = defaultColor

    private var themeColorsThumbTintList: ColorStateList? = null
    private var themeColorsTrackTintList: ColorStateList? = null

    init {
        thumbTintMode = PorterDuff.Mode.MULTIPLY
        getContext().withStyledAttributes(attrs, R.styleable.SbisSwitchView, defStyleAttr, defStyleRes) {
            thumbCheckedColor = getColor(THUMB.checkedColor)
            thumbUncheckedColor = getColor(THUMB.uncheckedColor)
            thumbDisabledCheckedColor = getColor(THUMB.disabledCheckedColor)
            thumbDisabledUncheckedColor = getColor(THUMB.disabledUncheckedColor)

            trackCheckedColor = getColor(TRACK.checkedColor)
            trackUncheckedColor = getColor(TRACK.uncheckedColor)
            trackDisabledCheckedColor = getTransparentColor(TRACK.disabledCheckedColor)
            trackDisabledUncheckedColor = getTransparentColor(TRACK.disabledUncheckedColor)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        thumbDrawable.setTintList(themeColorsThumbTintList ?: getThemeColorsTintList(THUMB))
        trackDrawable.setTintList(themeColorsTrackTintList ?: getThemeColorsTintList(TRACK))
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        // На 7 андроиде и ниже state view не доставляется детям (drawable).
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) return
        thumbDrawable.state = drawableState
        trackDrawable.state = drawableState
    }

    private fun getThemeColorsTintList(section: SbisSwitchSection): ColorStateList? {
        return when (section) {
            THUMB -> {
                themeColorsThumbTintList = getThemeColorsTintList(
                    thumbCheckedColor, thumbUncheckedColor, thumbDisabledCheckedColor, thumbDisabledUncheckedColor
                )
                themeColorsThumbTintList
            }

            TRACK -> {
                themeColorsTrackTintList = getThemeColorsTintList(
                    trackCheckedColor, trackUncheckedColor, trackDisabledCheckedColor, trackDisabledUncheckedColor
                )
                themeColorsTrackTintList
            }
        }
    }

    private fun getThemeColorsTintList(
        checkedColor: Int,
        uncheckedColor: Int,
        disabledCheckedColor: Int,
        disabledUncheckedColor: Int
    ): ColorStateList {
        val switchColorsList = IntArray(ENABLED_CHECKED_STATES.size)
        switchColorsList[0] = checkedColor
        switchColorsList[1] = uncheckedColor
        switchColorsList[2] = disabledCheckedColor
        switchColorsList[3] = disabledUncheckedColor
        return ColorStateList(ENABLED_CHECKED_STATES, switchColorsList)
    }

    @ColorInt
    private fun TypedArray.getColor(@StyleableRes index: Int) = getColor(index, defaultColor)

    @ColorInt
    private fun TypedArray.getTransparentColor(@StyleableRes index: Int) =
        ColorUtils.setAlphaComponent(getColor(index, defaultColor), (255 * DISABLED_COLORS_ALPHA).toInt())

    companion object {
        private const val DISABLED_COLORS_ALPHA = 0.6f

        private val ENABLED_CHECKED_STATES = arrayOf(
            intArrayOf(RAndroid.attr.state_enabled, RAndroid.attr.state_checked),
            intArrayOf(RAndroid.attr.state_enabled, -RAndroid.attr.state_checked),
            intArrayOf(-RAndroid.attr.state_enabled, RAndroid.attr.state_checked),
            intArrayOf(-RAndroid.attr.state_enabled, -RAndroid.attr.state_checked)
        )
    }
}