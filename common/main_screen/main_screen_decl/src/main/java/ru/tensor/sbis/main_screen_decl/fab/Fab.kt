package ru.tensor.sbis.main_screen_decl.fab

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.SecondaryButtonStyle
import java.util.Date

/**
 * Модель плавающей кнопки
 *
 * @author us.bessonov
 */
sealed class Fab(
    val icon: Drawable?,
    val clickListener: View.OnClickListener? = null
)

/**
 * Модель кнопки с иконкой
 *
 * @param icon используемая иконка, либо `null`, если текущая иконка должна остаться неизменной
 */
class IconFab(
    icon: Drawable?,
    val style: SbisButtonStyle = SecondaryButtonStyle,
    clickListener: View.OnClickListener
) : Fab(icon, clickListener)

/**
 * Модель кнопки с датой
 *
 * @param icon используемая иконка, либо `null`, если текущая иконка должна остаться неизменной
 */
class TodayFab(
    icon: Drawable?,
    val date: Date,
    val isWorkDay: Boolean,
    clickListener: View.OnClickListener
) : Fab(icon, clickListener)

/**
 * Создаёт [Drawable] для стандартной кнопки "+"
 */
fun createDefaultMainFabDrawable(context: Context) = IconicsDrawable(context, SbisMobileIcon.Icon.smi_navBarPlus)