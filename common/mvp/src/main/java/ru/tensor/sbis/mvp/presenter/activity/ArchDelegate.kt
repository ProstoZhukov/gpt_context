package ru.tensor.sbis.mvp.presenter.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.mvp.presenter.BaseArchDelegate

/**
 * Реализация делегата для активити
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class ArchDelegate<VIEW_MODEL>(
    private val activity: AppCompatActivity,
    creatingMethod: () -> VIEW_MODEL,
    key: String? = null,
    doOnCleared: (VIEW_MODEL) -> Unit = {}
) : BaseArchDelegate<VIEW_MODEL>(creatingMethod, key, doOnCleared) {

    override fun getViewModelProvider(factory: ViewModelProvider.Factory): ViewModelProvider =
        ViewModelProvider(activity, factory)
}
