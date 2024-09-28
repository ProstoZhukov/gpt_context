package ru.tensor.sbis.design.navigation.view.widget.support

import android.view.View
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import ru.tensor.sbis.design.navigation.view.widget.NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT
import ru.tensor.sbis.design.navigation.view.widget.calendar.CalendarWidgetViewModel

/**
 * @author ma.kolpakov
 */
@Deprecated(NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT)
internal class CalendarWidgetViewModelDelegateImpl(
    private val vm: CalendarWidgetViewModel
) : WidgetViewModelDelegate {

    override val widgetTitle: LiveData<String> = vm.title
    override val widgetIcon: LiveData<Int> = vm.icon
    override val widgetIconColor: LiveData<Int> = vm.iconColor
    override val widgetIsVisible = MediatorLiveData<Boolean>().apply {
        addSource(vm.title, ::updateVisibility)
        addSource(vm.icon, ::updateVisibility)
    }

    override fun onWidgetTitleClicked(ignored: View) {
        vm.onTitleClicked()
    }

    override fun onWidgetIconClicked(ignored: View) {
        vm.onIconClicked()
    }

    private fun updateVisibility(ignored: Any) {
        widgetIsVisible.value = !widgetTitle.value.isNullOrEmpty() || (widgetIcon.value ?: ID_NULL) != ID_NULL
    }
}