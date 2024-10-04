package ru.tensor.sbis.hallscheme.v2.presentation.model.places

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.places.Place
import ru.tensor.sbis.hallscheme.v2.presentation.factory.DrawablesHolder

/**
 * Класс для отображения кресла.
 * @author aa.gulevskiy
 */
internal class PlaceArmchairUi(place: Place, private val drawablesHolder: DrawablesHolder) : PlaceUi(place) {

    override fun getImage() = drawablesHolder.placeChairDrawable

}