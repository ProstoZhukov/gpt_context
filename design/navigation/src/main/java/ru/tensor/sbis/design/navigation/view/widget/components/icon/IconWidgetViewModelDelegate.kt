package ru.tensor.sbis.design.navigation.view.widget.components.icon

import androidx.lifecycle.LiveData
import io.reactivex.Observable
import ru.tensor.sbis.design.navigation.view.model.createLiveData
import ru.tensor.sbis.design.navigation.view.widget.WidgetViewModel

/**
 * Базовая реализация [IconWidgetViewModel] для повторного использования в композитных [WidgetViewModel].
 *
 * @author ma.kolpakov
 */
internal class IconWidgetViewModelDelegate(
    iconObservable: Observable<Int>,
    iconColorObservable: Observable<Int>,
    private val clickListener: IconWidgetClickListener
) : IconWidgetViewModel {

    override val icon: LiveData<Int> = createLiveData(iconObservable)
    override val iconColor: LiveData<Int> = createLiveData(iconColorObservable)

    override fun onIconClicked() {
        clickListener.onIconClicked()
    }
}