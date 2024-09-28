package ru.tensor.sbis.mvvm

import androidx.annotation.CallSuper
import androidx.databinding.BaseObservable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Базовый класс вьюмодели экрана
 *
 * @param <ROUTER> тип используемого роутера
</ROUTER> */
@Deprecated("Устаревший подход, переходим на mvi_extension")
open class BaseViewModel<ROUTER : Any> : BaseObservable() {

    lateinit var router: ROUTER
    private val disposables = CompositeDisposable()

    /**
     * Вызывается сразу после конструктора, весь код начала работы с репозиториям,
     * интеракторам или роутерам нужно размещать здесь
     */
    open fun initialize() {}

    /**
     * Обрабатывает нажатие кнопки "Назад"
     *
     * @return true, если нажатие кнопки "Назад" было обработано
     */
    fun onBackPressed(): Boolean {
        return false
    }

    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    /**
     * Выполняет освобождение ресурсов вьюмодели
     */
    @CallSuper
    open fun destroy() {
        disposables.dispose()
    }
}