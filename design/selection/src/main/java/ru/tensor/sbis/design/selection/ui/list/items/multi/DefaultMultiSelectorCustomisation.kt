package ru.tensor.sbis.design.selection.ui.list.items.multi

import ru.tensor.sbis.design.selection.ui.list.items.MultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.items.multi.region.RegionMultiSelectorCustomisation

/**
 * Внешний вид по умолчанию для множественного выбора
 *
 * @author ma.kolpakov
 */
internal class DefaultMultiSelectorCustomisation : MultiSelectorCustomisation by RegionMultiSelectorCustomisation()