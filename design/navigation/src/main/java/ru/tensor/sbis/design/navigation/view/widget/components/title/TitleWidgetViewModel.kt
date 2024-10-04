package ru.tensor.sbis.design.navigation.view.widget.components.title

import androidx.lifecycle.LiveData
import ru.tensor.sbis.design.navigation.view.widget.WidgetViewModel

/**
 * Вьюмодель виджета с заголовком.
 *
 * @author ma.kolpakov
 */
internal interface TitleWidgetViewModel : WidgetViewModel {

    /** @SelfDocumented */
    val title: LiveData<String>

    /** @SelfDocumented */
    fun onTitleClicked()
}