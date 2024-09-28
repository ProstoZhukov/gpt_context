/**
 * Общие утилитные расширения модулей communicator
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.communicator.common.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.subjects.Subject


/** @SelfDocumented */
inline fun <reified T> Any.castTo(): T? = this as? T

/** @SelfDocumented */
inline fun <reified T> Any.requireCastTo(): T = castTo()!!

/** @SelfDocumented */
inline fun <T> T.doIf(condition: Boolean, predicate: T.() -> Unit): T =
    apply { if (condition) predicate() }

/** @SelfDocumented */
inline fun <T> T.doIfNotNull(any: Any?, predicate: T.() -> Unit): T =
    apply { if (any != null) predicate() }

/** @SelfDocumented */
inline val <T> Observable<T>.asSubject: Subject<T>
    get() = requireCastTo()

/** @SelfDocumented */
inline val <T> LiveData<T>.asMutable: MutableLiveData<T>
    get() = requireCastTo()





