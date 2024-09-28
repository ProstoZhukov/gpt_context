package ru.tensor.sbis.design.chips.list

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.chips.item.SbisChipsItemView
import ru.tensor.sbis.design.chips.models.SbisChipsItem
import ru.tensor.sbis.design.chips.SbisChipsView

/**
 * Holder для списка элементов [SbisChipsView].
 *
 * @author ps.smirnyh
 */
internal class SbisChipsHolder(
    private val sbisChipsItemView: SbisChipsItemView
) : RecyclerView.ViewHolder(sbisChipsItemView) {
    fun bind(config: SbisChipsItem) {
        sbisChipsItemView.title = config.caption
        sbisChipsItemView.icon = config.icon
        sbisChipsItemView.accentedCounter = config.counter ?: 0
    }
}