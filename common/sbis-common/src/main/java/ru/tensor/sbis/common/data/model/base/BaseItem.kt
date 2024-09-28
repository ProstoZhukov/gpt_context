package ru.tensor.sbis.common.data.model.base

import ru.tensor.sbis.common.R
import java.io.Serializable

/**
 * Created by kabramov on 07.06.2018.
 */
class BaseItem<DATA>

/**
 * Constructor for making base item with type and subType - to define specific items
 *
 * @param type    like items view
 * @param subType like extra type - lastItem, firsItem, etc...
 * @param data    of item
 */(val type: Int, val subType: Int = R.id.regular_item_view, val data: DATA) : Serializable
