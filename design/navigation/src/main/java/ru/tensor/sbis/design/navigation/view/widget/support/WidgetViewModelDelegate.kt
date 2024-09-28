package ru.tensor.sbis.design.navigation.view.widget.support

import android.view.View
import androidx.lifecycle.LiveData
import ru.tensor.sbis.design.navigation.view.model.NavViewModel
import ru.tensor.sbis.design.navigation.view.widget.NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT
import ru.tensor.sbis.design.navigation.view.widget.NavItemWidget
import ru.tensor.sbis.design.navigation.view.widget.WidgetViewModel

/**
 * Интерфейс для интеграции [WidgetViewModel] в [NavViewModel] без смешивания API
 *
 * @see NavItemWidget
 * @author ma.kolpakov
 */
@Deprecated(NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT)
internal interface WidgetViewModelDelegate {
    val widgetTitle: LiveData<String>?
    val widgetIcon: LiveData<Int>?
    val widgetIconColor: LiveData<Int>?
    val widgetIsVisible: LiveData<Boolean>

    fun onWidgetTitleClicked(ignored: View)
    fun onWidgetIconClicked(ignored: View)
}