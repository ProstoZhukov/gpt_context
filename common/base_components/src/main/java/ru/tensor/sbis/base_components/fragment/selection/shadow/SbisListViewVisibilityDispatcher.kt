package ru.tensor.sbis.base_components.fragment.selection.shadow

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.list_utils.AbstractListView

/**
 * Диспетчер видимости тени, способный работать с [AbstractListView] в качестве компонента списка
 *
 * @author sr.golovkin
 */
class SbisListViewVisibilityDispatcher : ShadowVisibilityDispatcher {

    override fun canDispatch(scrolledView: View): Boolean =
        scrolledView is RecyclerView || scrolledView is AbstractListView<*, *>

    override fun getVisibility(scrolledView: View, child: View): Int =
        if (scrolledView is AbstractListView<*, *>) {
            getVisibility(scrolledView.recyclerView)
        } else {
            getVisibility(scrolledView as RecyclerView)
        }

    /**
     * Получить актуальный аттрибут видимости исходя из состояния указанного [RecyclerView]
     */
    private fun getVisibility(recyclerView: RecyclerView): Int =
        when (val manager = recyclerView.layoutManager) {
            is LinearLayoutManager -> {
                //чайлды в список могут быть еще не добавлены,
                //потому обрабатываем и NO_POSITION тоже. Тень в этот момент не нужна
                if (manager.findFirstCompletelyVisibleItemPosition() <= 0) {
                    View.INVISIBLE
                } else {
                    View.VISIBLE
                }
            }
            else -> View.INVISIBLE
        }
}