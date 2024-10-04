package ru.tensor.sbis.design.navigation.view.model

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

/**
 * Вьюмодель шапки аккордеона с логотипом приложения.
 *
 * @author us.bessonov
 */
internal interface NavigationHeaderViewModel {

    /** @SelfDocumented */
    val data: NavigationHeaderData

    /** @SelfDocumented */
    val isSelected: LiveData<Boolean>

    @Deprecated("https://online.sbis.ru/opendoc.html?guid=ec38d634-c12d-496b-8367-27214f232ac1&client=3")
    val selectionFlow: Flow<Boolean>

    /**
     * Отформатированный счётчик числа новых объектов
     */
    val newCounter: LiveData<String?>

    /**
     * Отформатированный счётчик общего числа объектов
     */
    val totalCounter: LiveData<String?>

    /**
     * Переключатель отображения счётчика в header аккордеона
     */
    val countersVisibility: LiveData<Boolean>

    /**
     * Состояние видимости разделителя между счётчиками
     */
    val countersDividerVisible: LiveData<Boolean>

    /** @SelfDocumented */
    fun setSelected(selected: Boolean)

    /** @SelfDocumented */
    fun onClicked()

    /** @SelfDocumented */
    fun updateCounters(counters: NavigationCounters)

    /** @SelfDocumented */
    fun updateCountersVisibility(enabled: Boolean)
}
