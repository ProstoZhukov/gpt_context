package ru.tensor.sbis.base_components.fragment.selection.shadow

import android.view.View
import androidx.core.widget.NestedScrollView

/**
 * Legacy-код
 * Created by aa.mironychev on 18.05.2018.
 */
class NestedScrollViewVisibilityDispatcher : ShadowVisibilityDispatcher {

    override fun canDispatch(scrolledView: View) = scrolledView is NestedScrollView

    override fun getVisibility(scrolledView: View, child: View): Int {
        return if (scrolledView.scrollY > 0) View.VISIBLE else View.INVISIBLE
    }

}