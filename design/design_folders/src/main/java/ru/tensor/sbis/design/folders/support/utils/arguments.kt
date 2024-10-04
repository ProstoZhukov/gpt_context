/**
 * Расширения для работы с аргументами AllFoldersDialogFragment без использования ключей
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.folders.support.utils

import android.os.Bundle
import ru.tensor.sbis.design.folders.support.FoldersViewModel

private const val VIEW_MODEL_KEY = "VIEW_MODEL_KEY"

/**
 * Ключ [FoldersViewModel]
 */
internal var Bundle.viewModelKey: String?
    get() = getString(VIEW_MODEL_KEY)
    set(value) = putString(VIEW_MODEL_KEY, value)
