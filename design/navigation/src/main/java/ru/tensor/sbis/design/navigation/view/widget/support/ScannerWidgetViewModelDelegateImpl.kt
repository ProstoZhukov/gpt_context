package ru.tensor.sbis.design.navigation.view.widget.support

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.tensor.sbis.design.navigation.view.widget.NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT
import ru.tensor.sbis.design.navigation.view.widget.components.icon.IconWidgetViewModel

/**
 * @author ma.kolpakov
 */
@Deprecated(NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT)
internal class ScannerWidgetViewModelDelegateImpl(
    private val vm: IconWidgetViewModel
) : WidgetViewModelDelegate {

    override val widgetTitle: LiveData<String>? = null
    override val widgetIcon: LiveData<Int> = vm.icon
    override val widgetIconColor: LiveData<Int> = vm.iconColor
    override val widgetIsVisible: LiveData<Boolean> = widgetIcon.map { it != null }

    override fun onWidgetTitleClicked(ignored: View) = Unit

    override fun onWidgetIconClicked(ignored: View) {
        vm.onIconClicked()
    }
}