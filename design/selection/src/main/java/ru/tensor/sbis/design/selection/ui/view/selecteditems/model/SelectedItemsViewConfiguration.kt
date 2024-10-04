package ru.tensor.sbis.design.selection.ui.view.selecteditems.model

import android.content.Context
import androidx.annotation.Px
import ru.tensor.sbis.design.selection.R

/**
 * Настраиваемые параметры контейнера выбранных элементов.
 *
 * @author us.bessonov
 */
data class SelectedItemsViewConfiguration(
    @Px
    val maxItemWidth: Int
)

/**@SelfDocumented**/
fun createDefaultConfiguration(context: Context) =
    SelectedItemsViewConfiguration(
        context.resources.getDimensionPixelSize(R.dimen.selection_selected_item_default_max_width)
    )
