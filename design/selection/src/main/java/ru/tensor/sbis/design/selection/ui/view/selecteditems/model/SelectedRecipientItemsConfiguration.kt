package ru.tensor.sbis.design.selection.ui.view.selecteditems.model

import android.content.Context
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemsContainerView

/**
 * Создаёт конфигурацию для корректного вида блока выбранных адресатов в [SelectedItemsContainerView]
 */
internal fun createSelectedRecipientItemsConfiguration(context: Context) =
    createDefaultConfiguration(context)