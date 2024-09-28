package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.list.view.adapter.ProgressViewHolderHelper
import ru.tensor.sbis.list.view.item.Item

/**
 * Индикатор подгрузки данных в списке.
 * @param place идентифицирует индикатор - сверху или снизу.
 */
internal class ProgressItem(place: ProgressItemPlace) : Item<ProgressItemPlace, ViewHolder>(
    place,
    ProgressViewHolderHelper(),
    comparable = place
)