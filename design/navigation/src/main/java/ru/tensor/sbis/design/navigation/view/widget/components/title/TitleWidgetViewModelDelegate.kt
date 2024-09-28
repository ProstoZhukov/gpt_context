package ru.tensor.sbis.design.navigation.view.widget.components.title

import androidx.lifecycle.LiveData
import io.reactivex.Observable
import ru.tensor.sbis.design.navigation.view.model.createLiveData
import ru.tensor.sbis.design.navigation.view.widget.WidgetViewModel

/**
 * Базовая реализация [TitleWidgetViewModel] для повторного использования в композитных [WidgetViewModel].
 *
 * @author ma.kolpakov
 */
internal class TitleWidgetViewModelDelegate(
    titleObservable: Observable<String>,
    private val clickListener: TitleWidgetClickListener
) : TitleWidgetViewModel {

    override val title: LiveData<String> = createLiveData(titleObservable)

    override fun onTitleClicked() {
        clickListener.onTitleClicked()
    }
}