/**
 * Тестовые модели
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.bl.vm

import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel

typealias DATA = TestData

const val DEFAULT_SELECTION_LIMIT = 50

/**
 * Тестовая модель данных
 */
interface TestData : RegionSelectorItemModel, HierarchySelectorItemModel