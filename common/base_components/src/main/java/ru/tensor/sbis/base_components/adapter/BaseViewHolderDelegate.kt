package ru.tensor.sbis.base_components.adapter

import android.view.ViewGroup

import ru.tensor.sbis.common.data.model.base.BaseItem

/**
 * @SelfDocumented
 * Created by kabramov on 17.07.2018.
 */
abstract class BaseViewHolderDelegate<DM, VH : AbstractViewHolder<BaseItem<DM>>> {

    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<BaseItem<DM>>

    /**@SelfDocumented*/
    open fun onBindViewHolder(holder: VH, item: BaseItem<DM>) {
        holder.bind(item)
    }

}
