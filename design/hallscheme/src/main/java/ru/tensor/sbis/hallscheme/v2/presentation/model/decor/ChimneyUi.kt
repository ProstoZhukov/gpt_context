package ru.tensor.sbis.hallscheme.v2.presentation.model.decor

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения камина.
 * type = 600
 * @author aa.gulevskiy
 */
internal class ChimneyUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_chimney

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90   -> R.drawable.hall_scheme_chimney_90
            270  -> R.drawable.hall_scheme_chimney_270
            else -> R.drawable.hall_scheme_chimney_0
        }
    }
}

/**
 * Класс для отображения длинного камина.
 * type = 601
 * @author aa.gulevskiy
 */
internal class ChimneyLongUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int {
        return when (itemRotation) {
            90, 270 -> R.drawable.hall_scheme_chimneytype2_90
            else    -> R.drawable.hall_scheme_chimneytype2_0
        }
    }

    override fun getView(viewGroup: ViewGroup): View {
        val view: ImageView = getViewForDifferentImagePerAngle(viewGroup)
        view.setImageDrawable(drawablesHolder.getDecorFlatDrawable(getFlatImageRes()))
        view.alpha = decor.opacity
        return view
    }

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90, 270 -> R.drawable.hall_scheme_chimneytype2_3d_90
            else    -> R.drawable.hall_scheme_chimneytype2_3d_0
        }
    }
}

/**
 * Класс для отображения круглого камина.
 * type = 602
 * @author aa.gulevskiy
 */
internal class ChimneyRoundUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_chimneytype3

    override fun get3dImageRes(): Int = R.drawable.hall_scheme_chimneytype3_3d
}