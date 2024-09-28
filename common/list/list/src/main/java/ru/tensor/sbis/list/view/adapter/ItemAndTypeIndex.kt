package ru.tensor.sbis.list.view.adapter

import ru.tensor.sbis.list.view.item.AnyItem

/**
 * Элемент списка и соответствующий ему тип. Используется для оптимизации работы с элементами в [SbisAdapterDelegate]
 *
 * @property item сам элемент списка.
 * @property typeIndex уникальный индекс с специальном наборе типов ячеек.
 */
internal data class ItemAndTypeIndex(val item: AnyItem, val typeIndex: Int)