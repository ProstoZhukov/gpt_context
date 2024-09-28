package ru.tensor.sbis.business.common.ui.base.contract

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.business.theme.R as RBusinessTheme

/**
 * Контракт, состояния и взаимодействия VM с View списка
 *
 * @author as.chadov
 *
 * @property isShown [ObservableBoolean] видимость содержимого списка
 * @property list [ObservableField] состояние списка контента для VM списка
 * @property scrollToTopPosition необходимость скролла к первому элементу при первичном показе данных
 * @property showInitialProgress [ObservableBoolean] состояние отображения [ru.tensor.sbis.design.view_ext.SbisProgressBar] если данные не из одного источника не получены
 * @property isRefresh [ObservableBoolean] состояние отображения [ru.tensor.sbis.design.progress.SbisPullToRefresh] в прогрессе после свайпа
 * @property isRefreshEnabled [ObservableBoolean] состояние активности [ru.tensor.sbis.design.progress.SbisPullToRefresh]
 * @property isLoadMore [ObservableBoolean] состояние отображения прогресса-пагинации во [RecyclerView]
 * @property isSearchVisible [ObservableBoolean] состояние видимости панели поиска [ru.tensor.sbis.design.view.input.searchinput.SearchInput]
 * @property isSearchAnimated состояние анимированности панели поиска
 * @property listBackgroundColorRes цвет фона контейнера
 * @property recyclerViewBackgroundColor цвет фона RecyclerView
 * @property recyclerViewBackgroundColorAttr атрибут цвета фона RecyclerView
 * @property isListHeightWrapContent должна ли высота списка определяться исходя из содержимого
 * @property isEmptyListHidden скрывать ли пустой список при отсутствии контента и показывать только при его наличии
 * @property isInPaginationProgress Событие требующее показать\скрыть прогресс бар
 */
interface ListContract {

    val isShown: ObservableBoolean
        get() = ObservableBoolean(true)

    val list: ObservableField<List<BaseObservable>>
        get() = ObservableField<List<BaseObservable>>()

    val scrollToTopPosition: Boolean
        get() = false

    val showInitialProgress: ObservableBoolean
        get() = ObservableBoolean(false)

    val isRefresh: ObservableBoolean
        get() = ObservableBoolean(false)

    val isRefreshEnabled: ObservableBoolean
        get() = ObservableBoolean(true)

    val isLoadMore: ObservableBoolean
        get() = ObservableBoolean(false)

    val isSearchVisible: ObservableBoolean
        get() = ObservableBoolean(false)

    val isSearchAnimated: ObservableBoolean
        get() = ObservableBoolean(false)

    val listBackgroundColorRes: ObservableInt
        get() = ObservableInt(R.color.business_app_background)

    val recyclerViewBackgroundColor: ObservableInt
        get() = ObservableInt(R.color.business_white)

    val recyclerViewBackgroundColorAttr: ObservableInt
        get() = ObservableInt(RBusinessTheme.attr.business_recycler_view_background_color)

    val isListHeightWrapContent: Boolean
        get() = false

    val isEmptyListHidden: Boolean
        get() = true

    val isInPaginationProgress: LiveData<Boolean>
        get() = MutableLiveData(false)
}

