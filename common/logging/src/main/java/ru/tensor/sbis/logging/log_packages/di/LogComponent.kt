package ru.tensor.sbis.logging.log_packages.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import io.reactivex.Observable
import ru.tensor.sbis.logging.log_packages.presentation.ClipboardCopier
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageViewHolderCallback
import ru.tensor.sbis.logging.log_packages.presentation.LogPackageViewModel

/**
 * Dagger компонент для поставки зависимостей модуля logging
 *
 * @author av.krymov
 */
@Component(modules = [LogModule::class])
internal interface LogComponent {

    /**@SelfDocumented*/
    fun provideLogPackageViewModel(): LogPackageViewModel

    /**@SelfDocumented*/
    fun clipboardCopier(): ClipboardCopier

    /**@SelfDocumented*/
    fun logPackageViewHolderCallback(): LogPackageViewHolderCallback

    /**@SelfDocumented*/
    val shakeDetectorObservable: Observable<Unit>

    /**@SelfDocumented*/
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): LogComponent
    }
}