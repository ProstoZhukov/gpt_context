package ru.tensor.sbis.design.navigation.view.model

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.navigation.view.model.content.ItemContentViewModel
import ru.tensor.sbis.design.navigation.view.model.content.NavigationItemContent
import ru.tensor.sbis.design.navigation.view.model.icon.NavigationIconViewModel
import ru.tensor.sbis.design.navigation.view.model.label.NavigationLabelViewModel
import ru.tensor.sbis.design.navigation.view.model.label.NavigationNavLabelViewModel
import ru.tensor.sbis.design.navigation.view.view.navmenu.icon_button.NavIconButton

/**
 * Модель представления элементов меню.
 *
 * @author ma.kolpakov
 * Создан 11/8/2018
 */
// TODO: https://online.sbis.ru/opendoc.html?guid=0127b90b-8d34-4865-a51a-18fe1550ed92
internal interface NavigationViewModel :
    NavigationIconViewModel,
    NavigationNavLabelViewModel,
    NavigationLabelViewModel,
    ItemContentViewModel {

    /**
     * Состояние элемента меню.
     */
    val state: BehaviorSubject<NavigationItemState>

    /**
     * Модель кнопки с иконкой.
     */
    val navIconButton: NavIconButton?

    /**
     * Модель дополнительного контента.
     */
    val navItemContent: NavigationItemContent?

    /**
     * Отформатированный счётчик количества непросмотренных объектов в разделе для аккордеона.
     */
    val navViewUnviewedCounter: Observable<String>

    /**
     * Отформатированный счётчик общего количества объектов в разделе для аккордеона.
     */
    val navViewTotalCounter: Observable<String>

    /**
     *  Целочисленный счётчик количества новых объектов в разделе для ННП.
     *  Для счётчиков в ННП используется форматтер по умолчанию.
     *  @see [DEFAULT_FORMAT]
     */
    val tabNavViewCounterObservable: Observable<Int>

    /**
     *  Использовать основной или второстепенный цвет фона для целочисленного счётчика количества новых объектов в
     *  разделе для ННП.
     */
    val tabNavViewCounterUseSecondaryBackgroundColorObservable: Observable<Boolean>

    /**
     * Идентификатор элемента навигации.
     */
    var ordinal: Int

    /**
     * Идентификатор родительского элемента навигации.
     */
    var parentOrdinal: Int?

    /**
     * Метод для отметки пункта меню "выбранным".
     *
     * @param sourceName название источника, который вызвал метод.
     */
    fun onSelect(sourceName: String)

    /**
     * Метод для обновления счётчиков на Аккордеоне(кроме головного) и ННП.
     */
    fun updateCounters(counters: NavigationCounters)
}