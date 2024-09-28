package ru.tensor.sbis.base_components.adapter

import android.view.View

import ru.tensor.sbis.common.data.model.base.BaseItem

/**@SelfDocumented*/
abstract class AbstractBaseItemViewHolder<DM>(itemView: View) : AbstractViewHolder<BaseItem<DM>>(itemView) {

    override fun bind(item: BaseItem<DM>) {
        super.bind(item)
        bindHolder(item.data, item.subType)
    }

    /**@SelfDocumented*/
    protected abstract fun bindHolder(item: DM, subType: Int)

}