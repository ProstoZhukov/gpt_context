package ru.tensor.sbis.business.common.ui.base.contract

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import io.reactivex.subjects.Subject
import ru.tensor.sbis.business.common.ui.utils.ViewActionObservable
import ru.tensor.sbis.common.util.SingleLiveEvent
import ru.tensor.sbis.design.view.input.searchinput.SearchInput

/**
 * Контракт, состояния и взаимодействия VM с View "Строки поиска с фильтром"
 * Ссылка на стандарт:
 * @see <a href="http://axure.tensor.ru/MobileStandart8/#p=строка_поиска">Строка поиска с фильтром</a>
 *
 * @author as.chadov
 *
 * @property cancelSearchChannel [ObservableField] канал уведомления о событии отмены поискав в строке поиска
 * @property searchFieldEditorChannel [ObservableField] канал уведомления о событии изменения отмены
 * поиска в строке поиска
 * @property searchQueryChangedChannel [ObservableField] канал уведомления о событии действий редактирования
 * в строке поиска [TextView.OnEditorActionListener]
 * @property searchFocusChangedChannel [ObservableField] канал уведомления о событии фокуса на строке поиска
 * @property clickFilterChannel [ObservableField] канал уведомления о событии нажатия на кнопку фильтра  в строке поиска
 * @property searchHintState [ObservableField] состояние текста подсказки в строке поиска
 * @property currentFiltersState [ObservableField] состояние текущего фильтра в строке поиска
 * @property hasFilterState [ObservableBoolean] состояние доступности фильтра в строке поиска
 * @property clearText [ObservableField] канал сброса поискового запроса в строке поиска.
 * Для сброса рекомендовано использовать [FilterSearchContract.clear] вместо [clearText] напрямую
 * @property hideKeyboardOnSearchClick true если закрывать клавиатуру при клике на поиск
 * @property isVisible true если строка поиска отображается
 * @property isElevated true применить тень к строке поиска
 * @property isAnimated true если строка поиска отображается с анимацией при смене [isVisible]
 * @property drawableLevel номер слоя, который будет показан в фоне drawable (business_simple_search_filter_background_dark.xml)
 * @property inputSearchText текст поискового запроса в строке фильтра.
 * Задаёт текущую поисковую строку через [SearchInput.setSearchText]
 * @property viewActionChannel канал выполнения обращения ко вью
 */
interface FilterSearchContract {
    val cancelSearchChannel: ObservableField<Subject<Any>?>
    val searchFieldEditorChannel: ObservableField<Subject<Int>?>
    val searchQueryChangedChannel: ObservableField<Subject<String>?>
    val searchFocusChangedChannel: ObservableField<Subject<Boolean>?>
    val clickFilterChannel: ObservableField<Subject<Any>?>
    val searchHintState: ObservableField<String>
    val currentFiltersState: ObservableField<List<String>?>
    val hasFilterState: ObservableBoolean
    val clearText: ObservableBoolean
    val hideKeyboardOnSearchClick: Boolean
    val isVisible: ObservableBoolean
    val isElevated: ObservableBoolean
    val isAnimated: ObservableBoolean
    val drawableLevel: ObservableInt
    var inputSearchText: String
    val viewActionChannel: ViewActionObservable<SearchInput>
}
