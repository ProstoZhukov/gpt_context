package ru.tensor.sbis.design.container

import android.view.View
import android.view.ViewGroup

/**
 * Листенер нужен для оповещения контейнера о добавлении контента в этот момент можно производить замеры контента когда
 * все вью уже готовы но контейнер еще не отрисовался
 * @author ma.kolpakov
 */
internal fun ViewGroup.onNextHierarchyChange(action: () -> Unit) {
    setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
        override fun onChildViewAdded(parent: View?, child: View?) {
            this@onNextHierarchyChange.setOnHierarchyChangeListener(null)
            action.invoke()
        }
        override fun onChildViewRemoved(parent: View?, child: View?) = Unit
    })
}