package ru.tensor.sbis.hallscheme.v2.presentation.model.decor

import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения двери слева.
 * type = 520
 * @author aa.gulevskiy
 */
internal class DoorLeftUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_doortype1

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90   -> R.drawable.hall_scheme_doortype1_90
            180  -> R.drawable.hall_scheme_doortype1_180
            270  -> R.drawable.hall_scheme_doortype1_270
            else -> {
                R.drawable.hall_scheme_doortype1_0
            }
        }
    }
}

/**
 * Класс для отображения двойной двери.
 * type = 521
 * @author aa.gulevskiy
 */
internal class DoorDoubleUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_doortype2

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90   -> R.drawable.hall_scheme_doortype2_90
            180  -> R.drawable.hall_scheme_doortype2_180
            270  -> R.drawable.hall_scheme_doortype2_270
            else -> R.drawable.hall_scheme_doortype2_0
        }
    }
}

/**
 * Класс для отображения двери справа.
 * type = 522
 * @author aa.gulevskiy
 */
internal class DoorRightUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_doortype3

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90   -> R.drawable.hall_scheme_doortype3_90
            180  -> R.drawable.hall_scheme_doortype3_180
            270  -> R.drawable.hall_scheme_doortype3_270
            else -> R.drawable.hall_scheme_doortype3_0
        }
    }
}