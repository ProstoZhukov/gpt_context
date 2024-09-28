package ru.tensor.sbis.hallscheme.v2.presentation.model.decor

import ru.tensor.sbis.hallscheme.R
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Decor
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения сцены.
 * id = 580
 * @author aa.gulevskiy
 */
internal class SceneUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_scene

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90 -> R.drawable.hall_scheme_scene_90
            180 -> R.drawable.hall_scheme_scene_180
            270 -> R.drawable.hall_scheme_scene_270
            else -> R.drawable.hall_scheme_scene_0
        }
    }
}

/**
 * Класс для отображения круглой сцены.
 * id = 581
 * @author aa.gulevskiy
 */
internal class SceneRoundUi(decor: Decor, drawablesHolder: DrawablesHolder) : DecorUi(decor, drawablesHolder) {

    override fun getFlatImageRes(): Int = R.drawable.hall_scheme_scenetype2

    override fun get3dImageRes(): Int {
        return when (itemRotation) {
            90 -> R.drawable.hall_scheme_scenetype2_3d_90
            180 -> R.drawable.hall_scheme_scenetype2_3d_180
            270 -> R.drawable.hall_scheme_scenetype2_3d_270
            else -> R.drawable.hall_scheme_scenetype2_3d_0
        }
    }
}