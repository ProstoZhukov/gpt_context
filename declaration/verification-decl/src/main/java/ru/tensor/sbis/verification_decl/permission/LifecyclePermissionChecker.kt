package ru.tensor.sbis.verification_decl.permission

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

/**
 * Расширение [PermissionChecker] с поддержкой реакций на иземнения жизненного цикла [LifecycleOwner]
 *
 * @author ma.kolpakov
 * Создан 12/11/2018
 */
interface LifecyclePermissionChecker : PermissionChecker, LifecycleObserver, Disposable {

    /**
     * Активация подписки на обновление полномочий
     */
    fun enable()

    /**
     * Приостановка подписки на обновление полномочий
     */
    fun disable()
}