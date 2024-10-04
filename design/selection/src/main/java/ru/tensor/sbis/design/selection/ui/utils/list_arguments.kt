/**
 * Набор расширений для работы с аргументами фрагментов списков без использования ключей. Аргументы в этом файле
 * локальны для отдельного уровня вложенности
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.ui.utils

import android.os.Bundle
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId

private const val PARENT_ITEM_ID = "PARENT_ITEM_ID"
private const val PARENT_TITLE = "PARENT_TITLE"

/**
 * Идентификатор родительского элемента при переходе во вложенные списки
 */
internal var Bundle.parentItemIdArg: SelectorItemId?
    get() = getString(PARENT_ITEM_ID)
    set(value) = putString(PARENT_ITEM_ID, value)

/**
 * Заголовок родительского элемента, от которого осуществляется переход
 */
internal var Bundle.parentItemTitleArg: String
    get() = getString(PARENT_TITLE)!!
    set(value) = putString(PARENT_TITLE, value)