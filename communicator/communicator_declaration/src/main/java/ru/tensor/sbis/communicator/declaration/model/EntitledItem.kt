package ru.tensor.sbis.communicator.declaration.model

import androidx.annotation.StringRes

/** Элемент списка "Уполномоченный". */
interface EntitledItem {

    /** @SelfDocumented */
    @StringRes
    fun getTitleRes(): Int

    /** @SelfDocumented */
    @StringRes
    fun getFilterTitleRes(): Int? = null

}