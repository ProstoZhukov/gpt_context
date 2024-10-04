package ru.tensor.sbis.hallscheme.v2.presentation.model.decor

import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder
import ru.tensor.sbis.hallscheme.v2.widget.RotatableImageView

/**
 * Класс для отображения растения.
 * id = 560
 * @author aa.gulevskiy
 */
internal class Tree1Ui(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_treetype1

    override fun get3dImageRes(): Int = R.drawable.hall_scheme_treetype1_3d
}

/**
 * Класс для отображения растения.
 * id = 561
 * @author aa.gulevskiy
 */
internal class Tree2Ui(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_treetype2

    override fun get3dImageRes(): Int = R.drawable.hall_scheme_treetype2_3d
}

/**
 * Класс для отображения растения.
 * id = 562
 * @author aa.gulevskiy
 */
internal class Tree3Ui(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_treetype3

    override fun get3dImageRes(): Int = R.drawable.hall_scheme_treetype3_3d_0
}

/**
 * Класс для отображения растений.
 * id = 563
 * @author aa.gulevskiy
 */
internal class Tree4GreenWallUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_treetype4_greenwall

    override fun get3dImageRes(): Int = R.drawable.hall_scheme_treetype4_greenwall_3d_0

    override fun get3dView(viewGroup: ViewGroup): View {
        val image = drawablesHolder.getDecor3dDrawable(get3dImageRes())
        val view = RotatableImageView.newInstance(viewGroup.context, decor, image)
        view.alpha = decor.opacity
        return view
    }
}