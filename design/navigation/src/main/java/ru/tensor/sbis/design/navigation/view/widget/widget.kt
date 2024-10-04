package ru.tensor.sbis.design.navigation.view.widget

import android.view.View
import android.view.ViewStub
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer
import ru.tensor.sbis.design.navigation.view.model.NavViewModel
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter
import ru.tensor.sbis.design.navigation.view.widget.calendar.CalendarWidgetClickListener
import ru.tensor.sbis.design.navigation.view.widget.calendar.CalendarWidgetViewModel
import ru.tensor.sbis.design.navigation.view.widget.calendar.CalendarWidgetViewModelImpl
import ru.tensor.sbis.design.navigation.view.widget.components.icon.IconWidget
import ru.tensor.sbis.design.navigation.view.widget.components.icon.IconWidgetClickListener
import ru.tensor.sbis.design.navigation.view.widget.components.icon.IconWidgetViewModel
import ru.tensor.sbis.design.navigation.view.widget.components.icon.IconWidgetViewModelDelegate
import ru.tensor.sbis.design.navigation.view.widget.components.title.TitleWidget
import ru.tensor.sbis.design.navigation.view.widget.counter.CounterWidgetViewModel
import ru.tensor.sbis.design.navigation.view.widget.empty.EmptyWidgetViewModel
import ru.tensor.sbis.design.navigation.view.widget.support.CalendarWidgetViewModelDelegateImpl
import ru.tensor.sbis.design.navigation.view.widget.support.EmptyWidgetViewModelDelegate
import ru.tensor.sbis.design.navigation.view.widget.support.ScannerWidgetViewModelDelegateImpl
import ru.tensor.sbis.design.navigation.view.widget.support.WidgetViewModelDelegate
import ru.tensor.sbis.design.R as RDesign

internal const val NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT =
    "Концепция виджетов в элементах меню находится на стадии проработки и может быть отклонена в любой момент. " +
        "Если виджеты будет решено удалить, это произойдёт по задаче: " +
        "TODO: 6/9/2020 https://online.sbis.ru/opendoc.html?guid=821e1b43-7b81-4809-85e2-1b2def20cb5c"

/**
 * Модель виджета, который встраивается в элементы компонентов навигации.
 *
 * Концепция виджетов в элементах меню находится на стадии проработки и может быть отклонена в любой момент. Пока этого
 * не случилось компоненты навигации поддерживают два типа виджетов [CounterWidget] и [CalendarWidget]. Эти виджеты
 * жёстко интегрированы в [NavViewModel], но подход к развитию направления проработан:
 * 1. в элементах меню должно быть место для вставки виджета, он может быть только один
 * 2. виджет должен предоставлять идентификатор layout ресурса для вставки во [ViewStub] или же [View] для вставки в
 * контейнер
 * 3. виджет должен предоставлять [WidgetViewModel], которая с помощью биндинга будет привязана к layout
 * 4. вероятно потребуется разделить предоставление виджетов для аккордеона и ННП или вовсе поддерживать в ННП только
 * счётчики
 *
 * @author ma.kolpakov
 */
sealed class NavItemWidget {
    /**
     * Идентификатор layout ресурса виджета, который будет встраиваться в элемент меню
     */
    @get:LayoutRes
    internal abstract val widgetLayout: Int

    /**
     * Реализация вьюмодели виджета. Тип определяет реализация виджета.
     * Не используется параметризация для скрытия реализации в модуле
     *
     * @param disposable контейнер, куда вьюмодель может складывать свои подписки
     */
    internal abstract fun createViewModel(disposable: DisposableContainer): WidgetViewModel

    /**
     * При развитии направления метод нужно удалить в пользу непосредственной установки WidgetViewModel в layout через
     * биндинг (см. п.3 описания NavItemWidget).
     */
    @Deprecated(NAVIGATION_WIDGET_EXPERIMENTAL_SUPPORT, ReplaceWith(""))
    internal open fun createWidgetViewModelDelegate(disposable: DisposableContainer): WidgetViewModelDelegate =
        EmptyWidgetViewModelDelegate
}

/**
 * Реализация виджета, который ничего не добавляет в элементы меню
 */
internal object EmptyWidget : NavItemWidget() {
    override val widgetLayout: Int = 0
    override fun createViewModel(disposable: DisposableContainer): WidgetViewModel = EmptyWidgetViewModel
}

/**
 * Виджет счётчика
 */
data class CounterWidget(
    val counter: NavigationCounter
) : NavItemWidget() {

    override val widgetLayout: Int
        get() = TODO("See description of NavItemWidget")

    override fun createViewModel(disposable: DisposableContainer): CounterWidgetViewModel {
        TODO("See description of NavItemWidget")
    }
}

/**
 * Виджет для отметки времени прихода/ухода
 */
data class CalendarWidget(
    override val title: Observable<String>,
    override val icon: Observable<Int>,
    val clickListener: CalendarWidgetClickListener
) : NavItemWidget(), IconWidget, TitleWidget {

    override val widgetLayout: Int
        get() = TODO("See description of NavItemWidget")

    // для календаря сейчас настройка цвета неактуальна
    override val iconColor: Observable<Int> = Observable.just(RDesign.color.text_color_counter)

    override fun createViewModel(disposable: DisposableContainer): CalendarWidgetViewModel =
        CalendarWidgetViewModelImpl(title, icon, iconColor, clickListener)

    override fun createWidgetViewModelDelegate(disposable: DisposableContainer): WidgetViewModelDelegate =
        CalendarWidgetViewModelDelegateImpl(createViewModel(disposable))
}

/**
 * Виджет для открытия сканера документов
 */
data class ScannerWidget(
    override val icon: Observable<Int>,
    override val iconColor: Observable<Int>,
    val clickListener: ScannerWidgetClickListener
) : NavItemWidget(), IconWidget {

    override val widgetLayout: Int
        get() = TODO("See description of NavItemWidget")

    @Suppress("unused")
    @JvmOverloads
    constructor(
        clickListener: ScannerWidgetClickListener,
        @StringRes icon: Int = RDesign.string.option_scan_icon,
        @ColorRes iconColor: Int = RDesign.color.text_color_counter
    ) : this(Observable.just(icon), Observable.just(iconColor), clickListener)

    override fun createViewModel(disposable: DisposableContainer): IconWidgetViewModel {
        disposable.add(clickListener)
        return IconWidgetViewModelDelegate(icon, iconColor, clickListener)
    }

    override fun createWidgetViewModelDelegate(disposable: DisposableContainer): WidgetViewModelDelegate =
        ScannerWidgetViewModelDelegateImpl(createViewModel(disposable))

    /**
     * Слушатель нажатий на иконку виджета сканера
     */
    interface ScannerWidgetClickListener : IconWidgetClickListener, Disposable
}