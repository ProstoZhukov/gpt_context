package ru.tensor.sbis.design.navigation.view.widget.support

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.design.navigation.view.widget.NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT
import ru.tensor.sbis.design.navigation.view.widget.WidgetViewModel

/**
 * Реализация [WidgetViewModel] без собственной механики
 *
 * @author ma.kolpakov
 */
@Deprecated(NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT)
internal object EmptyWidgetViewModelDelegate : WidgetViewModelDelegate {

    override val widgetTitle: LiveData<String>? = null
    override val widgetIcon: LiveData<Int>? = null
    override val widgetIconColor: LiveData<Int>? = null
    override val widgetIsVisible: LiveData<Boolean> = MutableLiveData(false)

    override fun onWidgetTitleClicked(ignored: View) = Unit

    override fun onWidgetIconClicked(ignored: View) = Unit
}