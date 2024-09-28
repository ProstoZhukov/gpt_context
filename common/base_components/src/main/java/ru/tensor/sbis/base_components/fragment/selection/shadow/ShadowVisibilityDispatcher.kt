package ru.tensor.sbis.base_components.fragment.selection.shadow

import android.view.View

/**
 * Created by aa.mironychev on 17.05.2018.
 */
/**
 * Диспетчер видимости тени.
 */
interface ShadowVisibilityDispatcher {

    /**
     * Может ли диспетчер обработать указанный скролл-вью.
     */
    fun canDispatch(scrolledView: View): Boolean

    /**
     * Какая видимость должна быть у тени в данный момент.
     */
    fun getVisibility(scrolledView: View, child: View): Int
}