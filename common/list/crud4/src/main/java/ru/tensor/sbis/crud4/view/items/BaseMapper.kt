package ru.tensor.sbis.crud4.view.items

import ru.tensor.sbis.crud4.domain.ItemInSectionMapper
import ru.tensor.sbis.crud4.view.viewmodel.ItemActionDelegate
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.service.DecoratedProtocol
import java.lang.IllegalStateException

/**
 * Базовая реализация маппера умеющая создавать ячейки по умолчанию
 * @author ma.kolpakov
 */
abstract class BaseMapper<SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    private val folderContentProvider: () -> ViewHolderDelegate<SOURCE_ITEM, IDENTIFIER>,
    private val itemContentProvider: () -> ViewHolderDelegate<SOURCE_ITEM, IDENTIFIER>,
    private val isSelectMode: Boolean = false
) : ItemInSectionMapper<SOURCE_ITEM, AnyItem, IDENTIFIER> {
    override fun map(item: SOURCE_ITEM, actionDelegate: ItemActionDelegate<SOURCE_ITEM, IDENTIFIER>): AnyItem {
        return when {
            item.stub != null -> {
                MoreButtonItem(actionDelegate, item.stub!!.pos)
            }

            item.nodeType == null -> {
                BaseItem(
                    item,
                    item.level.toInt(),
                    ItemViewHolderHelper(actionDelegate, itemContentProvider, isSelectMode)
                )
            }

            item.nodeType != null -> {
                BaseItem(
                    item,
                    item.level.toInt(),
                    FolderViewHolderHelper(actionDelegate, folderContentProvider, isSelectMode)
                )
            }

            else -> {
                throw IllegalStateException()
            }
        }
    }

}

/**
 * Вию-холдер-хелпер для ячеек, необходим отдельный класс так как вью-холдер-хелперы разделяются по типу.
 */
internal class ItemViewHolderHelper<DATA : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    actionDelegate: ItemActionDelegate<DATA, IDENTIFIER>,
    viewHolderDelegate: () -> ViewHolderDelegate<DATA, IDENTIFIER>,
    isSelectMode: Boolean = false
) : BaseItemViewHolderHelper<DATA, IDENTIFIER>(
    actionDelegate,
    viewHolderDelegate,
    isSelectMode
)

/**
 * Виюхолдер хелпер для папок, необходим отдельный класс так как вью-холдер хелперы разделяются по типу.
 */
internal class FolderViewHolderHelper<DATA : DecoratedProtocol<IDENTIFIER>, IDENTIFIER>(
    actionDelegate: ItemActionDelegate<DATA, IDENTIFIER>,
    viewHolderDelegate: () -> ViewHolderDelegate<DATA, IDENTIFIER>,
    isSelectMode: Boolean = false
) : BaseItemViewHolderHelper<DATA, IDENTIFIER>(
    actionDelegate,
    viewHolderDelegate,
    isSelectMode
)

