package ru.tensor.sbis.design.chips.api

import ru.tensor.sbis.design.chips.models.SbisChipsConfiguration
import ru.tensor.sbis.design.chips.models.SbisChipsItem
import ru.tensor.sbis.design.chips.SbisChipsView

/**
 * Api для компонента [SbisChipsView].
 *
 * @author ps.smirnyh
 */
interface SbisChipsViewApi {

    /** Конфигурация компонента. */
    var configuration: SbisChipsConfiguration

    /** Список элементов. */
    var items: List<SbisChipsItem>

    /** Выбранные значения. */
    var selectedKeys: List<Int>

    /** Делегат для реагирования на изменения выбранных элементов. */
    var selectionDelegate: SbisChipsSelectionDelegate?
}