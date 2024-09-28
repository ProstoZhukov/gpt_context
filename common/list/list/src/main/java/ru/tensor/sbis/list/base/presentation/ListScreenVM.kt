package ru.tensor.sbis.list.base.presentation

import androidx.lifecycle.LiveData
import ru.tensor.sbis.list.view.SbisListVM
import ru.tensor.sbis.list.view.container.ListContainerViewModel

/**
 * Реализация интерфейса позволит использовать класс для data binding.
 *
 * @property swipeRefreshIsEnabled LiveData<Boolean> доступность использования свайпа для запроса обновления данных.
 * @property swipeRefreshIsVisible LiveData<Boolean> видимость индикатора обновления по свайпу. Если
 * [swipeRefreshIsEnabled] содержит положительное значение, индикатор появляется автоматически после жеста, установкой
 * же значение false можно скрыть индикатор.
 */
interface ListScreenVM : SbisListVM,
    ListContainerViewModel {

    val swipeRefreshIsEnabled: LiveData<Boolean>
    val swipeRefreshIsVisible: LiveData<Boolean>

    /**
     * Сбросить состояние пагинации и загрузить данные с первой страницы.
     */
    fun showRefresh()
}