package ru.tensor.sbis.base_components.fragment.selection

import androidx.annotation.IdRes

/**
 * Created by sr.golovkin on 02.11.2018
 */

/**
 * Интерфейс, предоставляющий идентификатор некоторой View для доступа к этой View извне фрагмента, его содержащего
 */
interface ViewIdentifierProvider {

    /**
     * Предоставить id
     */
    @IdRes
    fun provideContentViewId() : Int
}