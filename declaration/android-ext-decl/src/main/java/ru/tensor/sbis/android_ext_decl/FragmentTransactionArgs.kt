package ru.tensor.sbis.android_ext_decl

import androidx.annotation.IdRes

/**
 * Параметры для создания транзакции.
 * @property containerResId идентификатор контейнера, в котором будет выполняться транзакция.
 * @property fragmentTag тег фрагмента для транзакции.
 * @property backStackName имя бекстека, null - если не требуется. Нужно для определения что вызвать - popBackStack или
 * remove в случае null.
 *
 * @author aa.sviridov
 */
open class FragmentTransactionArgs(
    @IdRes val containerResId: Int,
    val fragmentTag: String,
    val backStackName: String? = null,
)