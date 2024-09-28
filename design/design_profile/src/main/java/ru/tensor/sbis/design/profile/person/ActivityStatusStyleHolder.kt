package ru.tensor.sbis.design.profile.person

import android.content.Context
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Класс, содержащий ресурсы для [ActivityStatusView].
 *
 * @author mb.kruglova
 */
data class ActivityStatusStyleHolder(
    var backgroundColor: Int = 0,
    var primaryColor: Int = 0,
    var unaccentedColor: Int = 0,
    var statusSizeSmall: Int = 0,
    var statusSizeMedium: Int = 0,
    var strokeWidth: Int = 0
) {
    companion object {
        /**
         * Метод [ActivityStatusStyleHolder] для получения значений из глобальных переменных.
         */
        fun create(context: Context): ActivityStatusStyleHolder {
            return ActivityStatusStyleHolder().apply {
                backgroundColor = BackgroundColor.DEFAULT.getValue(context)
                primaryColor = StyleColor.PRIMARY.getIconColor(context)
                unaccentedColor = StyleColor.UNACCENTED.getIconColor(context)
                statusSizeSmall = InlineHeight.XS.getDimenPx(context) / 4
                statusSizeMedium = InlineHeight.M.getDimenPx(context) / 4
                strokeWidth = Offset.X3S.getDimenPx(context)
            }
        }
    }
}