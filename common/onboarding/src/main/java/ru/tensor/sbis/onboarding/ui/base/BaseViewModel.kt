package ru.tensor.sbis.onboarding.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author as.chadov
 */
internal open class BaseViewModel : ViewModel() {

    protected fun addDisposable(factory: () -> Disposable) {
        disposables.add(factory())
    }

    @CallSuper
    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    private val disposables = CompositeDisposable()
}