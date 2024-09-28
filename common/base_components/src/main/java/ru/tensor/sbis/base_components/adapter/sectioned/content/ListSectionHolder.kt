package ru.tensor.sbis.base_components.adapter.sectioned.content

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

/**
 * Интерфейс компонента, который содержит секцию
 *
 * @author am.boldinov
 */
interface ListSectionHolder : LifecycleOwner {

    /**@SelfDocumented*/
    fun getActivity(): FragmentActivity?

    /**@SelfDocumented*/
    fun getChildFragmentManager(): FragmentManager

    /**@SelfDocumented*/
    fun getListView(): RecyclerView?
}