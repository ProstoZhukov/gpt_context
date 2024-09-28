package ru.tensor.sbis.base_components.fragment.selection.shadow

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Legacy-код
 * Created by aa.mironychev on 18.05.2018.
 */
class RecyclerViewVisibilityDispatcher : ShadowVisibilityDispatcher {

    override fun canDispatch(scrolledView: View) = scrolledView is RecyclerView

    override fun getVisibility(scrolledView: View, child: View): Int {
        with(scrolledView as RecyclerView) {
            return when (val manager = layoutManager) {
                is LinearLayoutManager -> {
                    if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                        View.INVISIBLE
                    } else {
                        View.VISIBLE
                    }
                }
                else -> View.INVISIBLE
            }
        }
    }

}