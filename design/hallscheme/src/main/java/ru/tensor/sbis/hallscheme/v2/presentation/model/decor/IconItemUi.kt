package ru.tensor.sbis.hallscheme.v2.presentation.model.decor

import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения иконок.
 * id 620-626
 * @author aa.gulevskiy
 */
internal class IconItemUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int {
        return when (decor.type) {
            620 -> R.drawable.hall_scheme_wc
            621 -> R.drawable.hall_scheme_exit
            622 -> R.drawable.hall_scheme_wardrobe
            623 -> R.drawable.hall_scheme_kitchen
            624 -> R.drawable.hall_scheme_noentry
            625 -> R.drawable.hall_scheme_childrenroom
            626 -> R.drawable.hall_scheme_nosmoking
            else -> R.drawable.hall_scheme_wc
        }
    }

    override fun get3dImageRes(): Int {
        return when (decor.type) {
            620 -> R.drawable.hall_scheme_wc_3d
            621 -> R.drawable.hall_scheme_exit_3d
            622 -> R.drawable.hall_scheme_wardrobe_3d
            623 -> R.drawable.hall_scheme_kitchen_3d
            624 -> R.drawable.hall_scheme_noentry_3d
            625 -> R.drawable.hall_scheme_childrenroom_3d
            626 -> R.drawable.hall_scheme_nosmoking_3d
            else -> R.drawable.hall_scheme_wc_3d
        }
    }
}