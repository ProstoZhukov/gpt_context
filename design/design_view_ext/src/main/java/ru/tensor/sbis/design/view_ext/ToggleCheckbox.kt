package ru.tensor.sbis.design.view_ext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.R as RDesign

/**
 * Кастомный чекбокс
 */
@SuppressLint("AppCompatCustomView")
class ToggleCheckbox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    /** @SelfDocumented */
    @ColorInt
    var iconColor: Int = Color.MAGENTA

    /** @SelfDocumented */
    var isChecked: Boolean = false
        set(value) {
            field = value
            text = if (value) {
                resources.getString(RDesign.string.design_mobile_icon_check)
            } else {
                ""
            }
        }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ToggleCheckbox)

        isChecked = attributes.getBoolean(R.styleable.ToggleCheckbox_isChecked, false)
        typeface = TypefaceManager.getSbisMobileIconTypeface(context)

        iconColor = attributes.getColor(R.styleable.ToggleCheckbox_checkIconColor, iconColor)
            .takeUnless { it == Color.MAGENTA }
            ?: context.getColorFromAttr(RDesign.attr.markerColor)
        val states = Array(2) { IntArray(1) }
        states[0] = intArrayOf(android.R.attr.state_enabled)
        //disabled state
        states[1] = intArrayOf(-android.R.attr.state_enabled)
        val colors = intArrayOf(iconColor, context.getColorFromAttr(RDesign.attr.readonlyTextColor))
        setTextColor(ColorStateList(states, colors))

        attributes.recycle()

        gravity = Gravity.CENTER
        text = ""
    }

    /** @SelfDocumented */
    fun switchChecked() {
        isChecked = !isChecked
    }

    /** @SelfDocumented */
    fun setIsChecked(checked: Boolean) {
        isChecked = checked
    }
}
