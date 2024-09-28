package ru.tensor.sbis.design.context_menu.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.utils.IconCheckBoxStyleHolder
import ru.tensor.sbis.design.context_menu.utils.CheckboxIcon

/**
 * View иконки-галочки элемента [SbisMenu].
 *
 * @author ma.kolpakov
 */
class IconCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val styleHolder: IconCheckBoxStyleHolder = IconCheckBoxStyleHolder()
) : AppCompatImageView(context, attrs, defStyleAttr), Checkable {

    init {
        styleHolder.loadStyle(context)
        setIcon(context, styleHolder)
    }

    private var checkedValue: Boolean = false
        private set(value) {
            field = value
            visibility = if (field) VISIBLE else INVISIBLE
        }

    /** Задать вид иконки. */
    fun setOnIcon(icon: CheckboxIcon) {
        setIcon(context, styleHolder, icon)
        invalidate()
    }

    override fun setChecked(checked: Boolean) {
        checkedValue = checked
    }

    override fun isChecked() = checkedValue

    override fun toggle() {
        checkedValue = !checkedValue
    }

    private fun setIcon(
        context: Context,
        styleHolder: IconCheckBoxStyleHolder,
        icon: CheckboxIcon = CheckboxIcon.CHECK
    ) {
        setImageDrawable(
            IconicsDrawable(context).apply {
                when (icon) {
                    CheckboxIcon.CHECK -> {
                        icon(SbisMobileIcon.Icon.smi_checked)
                        color(styleHolder.iconChecboxColor)
                    }
                    CheckboxIcon.MARKER -> {
                        // Паддинг нужен, так как иконка маркера слишком большая
                        paddingPx(styleHolder.iconChecboxMarkerPadding)
                        icon(SbisMobileIcon.Icon.smi_markerCircle)
                        color(styleHolder.iconChecboxMarkerColor)
                    }
                }
                sizePx(styleHolder.iconChecboxSize)
            }
        )
    }
}