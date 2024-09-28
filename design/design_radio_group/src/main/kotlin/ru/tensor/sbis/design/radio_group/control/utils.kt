package ru.tensor.sbis.design.radio_group.control

import ru.tensor.sbis.design.radio_group.control.models.SbisRadioGroupItem
import ru.tensor.sbis.design.radio_group.item.SbisRadioGroupItemView

/** Вспомогательный класс для хранения размеров. */
internal class Size(val width: Int, val height: Int) {

    operator fun component1() = width

    operator fun component2() = height
}

/** Вспомогательный класс для хранения модели и соответствующей ей view. */
internal class PairModelView(val model: SbisRadioGroupItem, val view: SbisRadioGroupItemView)

/** Вспомогательная функция для более простого создания [PairModelView]. */
internal infix fun SbisRadioGroupItem.to(view: SbisRadioGroupItemView) = PairModelView(this, view)