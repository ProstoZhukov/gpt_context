package ru.tensor.sbis.crud4.view.items

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.crud4.view.MoreButtonViewHolderHelper
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Ячейка для кнопки еще
 *
 * @author ma.kolpakov
 */
class MoreButtonItem<SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    actionDelegate: ItemActionDelegate<SOURCE_ITEM, IDENTIFIER>,
    pos: Long
) : Item<Long, ViewHolder>(pos, MoreButtonViewHolderHelper(actionDelegate))