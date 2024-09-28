package ru.tensor.sbis.list.view

import androidx.lifecycle.LiveData
import ru.tensor.sbis.list.base.presentation.ListLiveData
import ru.tensor.sbis.list.view.calback.ItemMoveCallback
import ru.tensor.sbis.list.view.calback.ListViewListener
import ru.tensor.sbis.list.view.utils.InitialDataAddListener

/**
 * Реализация интерфейса позволит использовать класс для data binding.
 *
 * @property loadNextVisibility MutableLiveData<Boolean> видимость индикатора подгрузки следующей страницы.
 * @property loadPreviousVisibility MutableLiveData<Boolean> видимость индикатора подгрузки предыдущей страницы.
 * @property listData ListLiveData данные для отображения в списке.
 * @property fabPadding LiveData<Boolean> добавить отступ снизу для FAB.
 * @property scrollToPosition подписка на события прокрутки в компоненте списка
 */
interface SbisListVM : ItemMoveCallback, ListViewListener, InitialDataAddListener {

    val loadNextVisibility: LiveData<Boolean>
    val loadPreviousVisibility: LiveData<Boolean>
    val loadNextAvailability: LiveData<Boolean>
    val loadPreviousAvailability: LiveData<Boolean>
    val listData: ListLiveData
    val fabPadding: LiveData<Boolean>
    val needInitialScroll: LiveData<Boolean>
    val scrollToPosition: LiveData<Int>
}