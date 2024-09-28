package ru.tensor.sbis.design.navigation.view.widget.calendar

import io.reactivex.Observable
import ru.tensor.sbis.design.navigation.view.widget.components.icon.IconWidgetViewModel
import ru.tensor.sbis.design.navigation.view.widget.components.icon.IconWidgetViewModelDelegate
import ru.tensor.sbis.design.navigation.view.widget.components.title.TitleWidgetViewModel
import ru.tensor.sbis.design.navigation.view.widget.components.title.TitleWidgetViewModelDelegate

/**
 * Реализация [CalendarWidgetViewModel].
 * @author ma.kolpakov
 */
internal class CalendarWidgetViewModelImpl(
    titleObservable: Observable<String>,
    iconObservable: Observable<Int>,
    iconColorObservable: Observable<Int>,
    private val clickListener: CalendarWidgetClickListener
) : CalendarWidgetViewModel,
    IconWidgetViewModel by IconWidgetViewModelDelegate(iconObservable, iconColorObservable, clickListener),
    TitleWidgetViewModel by TitleWidgetViewModelDelegate(titleObservable, clickListener)