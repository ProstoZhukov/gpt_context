package ru.tensor.sbis.design.navigation.view.widget.components.icon

import androidx.lifecycle.LiveData
import ru.tensor.sbis.design.navigation.view.widget.WidgetViewModel

/**
 * Вьюмодель виджета с иконкой.
 *
 * @author ma.kolpakov
 */
internal interface IconWidgetViewModel : WidgetViewModel {

    /** @SelfDocumented */
    val icon: LiveData<Int>

    /** @SelfDocumented */
    val iconColor: LiveData<Int>

    /** @SelfDocumented */
    fun onIconClicked()
}