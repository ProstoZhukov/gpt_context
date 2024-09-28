package ru.tensor.sbis.master_detail

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ru.tensor.sbis.mvvm.fragment.CommandRunner

/**
 * Класс вьюмодели компонента master-detail.
 *
 * @author ps.smirnyh
 */
class MasterDetailViewModel(
    private val stateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {

    private val _detailContainerStubViewVisibility = stateHandle.getLiveData(STUB_VISIBILITY, VISIBLE)

    /**
     * Исполнитель команд над фрагментом.
     */
    val commandRunner = CommandRunner()

    /**
     * Видимость логотипа в правой части экрана.
     */
    val detailContainerStubViewVisibility: LiveData<Int>
        get() = _detailContainerStubViewVisibility

    /**
     * Метод указываеющий на удаление детального фрагмента.
     */
    fun deletedDetailFragment() {
        stateHandle[STUB_VISIBILITY] = VISIBLE
    }

    /**
     * Метод указывающий на добавление детального фрагмента.
     */
    fun addedDetailFragment() {
        stateHandle[STUB_VISIBILITY] = GONE
    }

    private companion object {
        const val STUB_VISIBILITY = "STUB_VISIBILITY"
    }
}