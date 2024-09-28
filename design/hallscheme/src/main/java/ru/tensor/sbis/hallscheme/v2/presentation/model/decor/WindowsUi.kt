package ru.tensor.sbis.hallscheme.v2.presentation.model.decor

import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения окна.
 * id = 500
 * @author aa.gulevskiy
 */
internal class WindowSingleUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_windowtype1

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90 -> R.drawable.hall_scheme_window_type1_90
            180 -> R.drawable.hall_scheme_window_type1_180
            270 -> R.drawable.hall_scheme_window_type1_270
            else -> R.drawable.hall_scheme_window_type1_0
        }
    }
}

/**
 * Класс для отображения длинного окна.
 * id = 501
 * @author aa.gulevskiy
 */
internal class WindowLongUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_windowtype2

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90 -> R.drawable.hall_scheme_window_type2_90
            180 -> R.drawable.hall_scheme_window_type2_180
            270 -> R.drawable.hall_scheme_window_type2_270
            else -> R.drawable.hall_scheme_window_type2_0
        }
    }
}