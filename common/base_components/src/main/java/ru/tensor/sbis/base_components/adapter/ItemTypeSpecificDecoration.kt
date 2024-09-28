package ru.tensor.sbis.base_components.adapter

import android.graphics.drawable.Drawable
import ru.tensor.sbis.base_components.ItemSpecificDecoration
import ru.tensor.sbis.base_components.ItemsDecorationData
import ru.tensor.sbis.base_components.adapter.vmadapter.ViewModelAdapter

/**
 * Реализация [ItemSpecificDecoration] для отображения разделителей в зависимости от типа вьюмодели при использовании
 * [ViewModelAdapter]
 */
class ItemTypeSpecificDecoration @JvmOverloads constructor(
    decorationDataList: List<ItemsDecorationData<Any>>,
    defaultDecoration: Drawable? = null,
    shouldDrawLastDivider: Boolean = true
) : ItemSpecificDecoration<ViewModelAdapter, Any>(
    ViewModelAdapter::getViewModelType,
    decorationDataList,
    defaultDecoration,
    shouldDrawLastDivider
)