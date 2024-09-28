package ru.tensor.sbis.design.buttons.cut

import android.content.Context
import androidx.annotation.Dimension
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.global_variables.IconSize

/**
 * Режимы для кнопки КАТ.
 *
 * @author ps.smirnyh
 */
enum class SbisCutButtonMode(
    val icon: Char,
    val iconSize: IconSize
) {

    /**
     * Стрелка вверх (свернуть).
     */
    ARROW_UP(
        SbisMobileIcon.Icon.smi_STTnew.character,
        IconSize.XL
    ),

    /**
     * Стрелка вних (развернуть).
     */
    ARROW_DOWN(
        SbisMobileIcon.Icon.smi_STTnewDown.character,
        IconSize.XL
    ),

    /**
     * Троеточие (развернуть).
     */
    MORE(
        SbisMobileIcon.Icon.smi_moreSmall.character,
        IconSize.X4L
    );

    /**
     * @see IconSize.getDimen
     */
    @Dimension
    fun getIconSizeDimen(context: Context) = iconSize.getDimenPx(context)
}