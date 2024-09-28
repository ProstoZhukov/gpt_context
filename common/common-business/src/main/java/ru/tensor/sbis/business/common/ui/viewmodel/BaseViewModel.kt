package ru.tensor.sbis.business.common.ui.viewmodel

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Базовый класс вьюмодели
 *
 * @author as.chadov
 *
 * @property initialized true если инициализация была совершена
 * @property deferredInit true если необходима инициализация отложенная до первоначального создания вью,
 * иначе инициализация осуществляется сразу при первом обращении к вью-модели
 * @property hasRestoredState true если состояние было восстановлено
 */
open class BaseViewModel() :
    ViewModel(),
    Disposable {

    var initialized = false
        private set
    open val deferredInit = false
    var hasRestoredState = false
        private set
    private val disposables = CompositeDisposable()

    /**
     * Создание вьюмодели родителя по отношению к другим вложенным вью-моделям [BaseViewModel]
     * @param nestedVm список вложенных вью-моделей
     */
    constructor(vararg nestedVm: BaseViewModel) : this() {
        for (viewModel in nestedVm) addDisposable { viewModel }
    }

    // region initialize
    /** инициировать инициализацию при первом обращении к вью-модели */
    fun initialize() {
        if (initialized.not() && !deferredInit) {
            onInitialization()
            initialized = true
        }
    }

    /** инициировать инициализацию после получения предыдущего состояния вью [savedState] */
    fun initialize(savedState: Bundle?) {
        setState(savedState)
        if (initialized.not() && deferredInit) {
            onInitialization()
            initialized = true
        }
    }

    /** инициировать инициализацию и вернуть контейнер с подпиской */
    fun initializeAsDisposable(): Disposable =
        also { initialize() }
    // endregion initialize

    /** @SelfDocumented */
    fun deactivate() {
        initialized = false
    }

    // region savedInstanceState
    /** Восстановление состояния [savedState]. */
    @CallSuper
    open fun setState(savedState: Bundle?) {
        hasRestoredState = savedState != null
    }

    /** Сохранение состояния [outState]. */
    open fun saveState(outState: Bundle) = Unit
    // endregion savedInstanceState

    /**
     * Вызывается сразу после конструктора, весь код начала работы с репозиториями,
     * интеракторам нужно размещать здесь
     */
    protected open fun onInitialization() = Unit

    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    protected fun addDisposable(factory: () -> Disposable) {
        disposables.add(factory())
    }

    /** Выполняет освобождение ресурсов вьюмодели */
    @CallSuper
    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    override fun isDisposed() = disposables.isDisposed

    override fun dispose() = onCleared()
}