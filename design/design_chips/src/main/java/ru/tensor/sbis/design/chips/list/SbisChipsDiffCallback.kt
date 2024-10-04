package ru.tensor.sbis.design.chips.list

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.design.chips.models.SbisChipsItem
import ru.tensor.sbis.design.chips.SbisChipsView

/**
 * Diff utils для списка элементов [SbisChipsView].
 *
 * @author ps.smirnyh
 */
internal class SbisChipsDiffCallback : DiffUtil.ItemCallback<SbisChipsItem>() {
    override fun areItemsTheSame(oldItem: SbisChipsItem, newItem: SbisChipsItem) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SbisChipsItem, newItem: SbisChipsItem): Boolean {
        return oldItem.caption == newItem.caption && oldItem.icon == newItem.icon && oldItem.counter == newItem.counter
    }
}