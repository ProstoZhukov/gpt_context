package ru.tensor.sbis.red_button.ui.stub.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.red_button.interactor.RedButtonPreferencesInteractor
import ru.tensor.sbis.red_button.ui.stub.RedButtonStubViewModel
import javax.inject.Inject

/**
 * Фабрика для создания вью модели [RedButtonStubViewModel]
 *
 * @author ra.stepanov
 */
class RedButtonStubViewModelFactory @Inject constructor(
    private val preferencesInteractor: RedButtonPreferencesInteractor
) : ViewModelProvider.Factory {

    /**@SelfDocumented */
    @Suppress("UNCHECKED_CAST")
    override fun <VIEW_MODEL : ViewModel> create(modelClass: Class<VIEW_MODEL>): VIEW_MODEL {
        return RedButtonStubViewModel(preferencesInteractor) as VIEW_MODEL
    }
}