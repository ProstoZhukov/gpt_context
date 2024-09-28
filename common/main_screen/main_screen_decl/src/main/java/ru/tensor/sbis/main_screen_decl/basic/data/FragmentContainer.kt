package ru.tensor.sbis.main_screen_decl.basic.data

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager

/**
 * Контейнер для прикладного содержимого
 *
 * @property containerId идентификатор контейнера для встраивания контента.
 * @property fragmentManager [FragmentManager] используемый для размещения контента.
 *
 * @author us.bessonov
 */
class FragmentContainer(
    @IdRes val containerId: Int,
    val fragmentManager: FragmentManager
)