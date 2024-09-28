package ru.tensor.sbis.common.data.mapper.base

import ru.tensor.sbis.common.data.model.base.BaseItem

/**
 * Created by kabramov on 07.06.2018.
 */
class BaseItemMapper {

    fun <MODEL> toBaseItem(type: Int, model: MODEL): BaseItem<MODEL> {
        return BaseItem(type, data = model)
    }

    fun <MODEL> toBaseItem(type: Int, subtype: Int, model: MODEL): BaseItem<MODEL> {
        return BaseItem(type, subtype, model)
    }

}

fun <MODEL> toBaseItem(type: Int, model: MODEL): BaseItem<MODEL> = BaseItem(type, data = model)

fun <MODEL> toBaseItem(type: Int, subtype: Int, model: MODEL): BaseItem<MODEL> = BaseItem(type, subtype, model)
