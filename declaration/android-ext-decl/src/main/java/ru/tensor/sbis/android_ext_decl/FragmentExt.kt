package ru.tensor.sbis.android_ext_decl

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

/**
 * Позволяет работать с аргументами фрагмента, не заботясь о том,
 * были ли они созданы ранее или не были.
 *
 * Вызывать этот метод необходимо только тогда, когда
 * состояние фрагмента ещё не сохранено, иначе
 * изменения аргументов не будут иметь эффекта.
 * Т.е. либо до добавления во FragmentManager,
 * либо когда ЖЦ фрагмента в состоянии STARTED.
 *
 * @see doPreventingStateLoss
 */
fun Fragment.updateArgs(block: Bundle.() -> Unit) {
    if (isStateSaved) {
        IllegalStateException(
            "Состояние фрагмента ${this::class.java.name} уже сохранено, " +
                "изменения аргументов не будут иметь эффекта."
        ).throwIfDebug()
        return
    }
    (arguments ?: Bundle().also { arguments = it })
        .apply(block::invoke)
}

/**
 * Выполняет переданное действие [action], гарантируя отсутствие ошибок,
 * связанных с изменением уже сохранённого состояния фрагмента
 * ("Cannot perform this action after onSaveInstanceState" и подобных).
 *
 * Достигается это за счёт того, что дейсвтие будет выполнено гарантированно
 * в состоянии ЖЦ STARTED. Если в момент вызова ЖЦ фрагмента в более раннем
 * состоянии, например, CREATED, то действие будет отложено до перехода в STARTED.
 *
 * Обратите внимание: если ЖЦ фрагмента уже в состоянии DESTROYED, переданный action
 * никогда не будет выполнен.
 *
 * Т.к. в первую очередь защита от потерь состояния нужна при действиях с
 * дочерними фрагментами, для удобства в [action] передаётся childFragmentManager.
 */
fun Fragment.doPreventingStateLoss(action: (FragmentManager) -> Unit) {
    if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
        Timber.w("Фрагмент в состоянии DESTROYED, action никогда не будет выполнен")
        return
    }
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            action.invoke(childFragmentManager)
        }
    })
}