package ru.tensor.sbis.wizard.impl.di

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.wizard.decl.WizardSteps
import ru.tensor.sbis.wizard.impl.WizardFragment
import ru.tensor.sbis.wizard.impl.state.StateSaver

/**
 * Сущность, обеспечивающая внедрение зависимостей мастера
 *
 * @param appContext        Контекст приложения
 * @param steps             Прикладной экземпляр [WizardSteps]
 * @param savedState        Сохранённое состояние мастера, см. [StateSaver]
 *
 * @author sa.nikitin
 */
internal class WizardDI(
    appContext: Context,
    steps: WizardSteps,
    savedState: Bundle?,
    wizardBackStackName: String?
) : ViewModel() {

    private val diComponent: WizardDIComponent =
        DaggerWizardDIComponent.factory().create(appContext, steps, savedState, wizardBackStackName)

    /** @SelfDocumented */
    fun inject(wizardFragment: WizardFragment) {
        diComponent.inject(wizardFragment)
    }

    override fun onCleared() {
        super.onCleared()
        diComponent.presenter.release()
    }
}

/**
 * Фабрика [WizardDI] как [ViewModel]-и
 *
 * @author sa.nikitin
 */
internal class WizardDIFactory(
    private val appContext: Context,
    private val steps: WizardSteps,
    private val savedState: Bundle?,
    private val wizardBackStackName: String?
) : ViewModelProvider.Factory {

    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        require(modelClass == WizardDI::class.java) { "Unsupported ViewModel type $modelClass" }
        @Suppress("UNCHECKED_CAST")
        return WizardDI(appContext, steps, savedState, wizardBackStackName) as VM
    }
}