package ru.tensor.sbis.hallscheme.v2.presentation.model.decor

import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения L-образной лестницы.
 * id = 540
 * @author aa.gulevskiy
 */
internal class StairLUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_stairtype1

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90   -> R.drawable.hall_scheme_stair_type1_90
            180  -> R.drawable.hall_scheme_stair_type1_180
            270  -> R.drawable.hall_scheme_stair_type1_270
            else -> R.drawable.hall_scheme_stair_type1_0
        }
    }
}

/**
 * Класс для отображения U-образной лестницы.
 * id = 541
 * @author aa.gulevskiy
 */
internal class StairUUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_stairtype2

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90   -> R.drawable.hall_scheme_stair_type2_90
            180  -> R.drawable.hall_scheme_stair_type2_180
            270  -> R.drawable.hall_scheme_stair_type2_270
            else -> R.drawable.hall_scheme_stair_type2_0
        }
    }
}

/**
 * Класс для отображения винтовой лестницы.
 * id = 542
 * @author aa.gulevskiy
 */
internal class StairIUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_stairtype3

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90   -> R.drawable.hall_scheme_stair_type3_90
            180  -> R.drawable.hall_scheme_stair_type3_180
            270  -> R.drawable.hall_scheme_stair_type3_270
            else -> R.drawable.hall_scheme_stair_type3_0
        }
    }
}