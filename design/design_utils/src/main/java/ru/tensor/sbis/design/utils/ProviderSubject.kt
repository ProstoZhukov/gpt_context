@file:Suppress("CanBeParameter")

package ru.tensor.sbis.design.utils

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.BehaviorSubject
import kotlin.properties.ReadWriteProperty

/**@SelfDocumented**/
class ProviderSubject<T>(
    val observable: Observable<T>,
    @Suppress("MemberVisibilityCanBePrivate") val setter: ReadWriteProperty<Any?, T?>
) {

    var value by setter

    /**@SelfDocumented**/
    fun onNext(value: T) {
        this.value = value
    }

    /**@SelfDocumented**/
    fun subscribe(subscribe: Consumer<in T>): Disposable = observable.subscribe(subscribe)
    /**@SelfDocumented**/
    fun subscribe(subscribe: (T) -> Unit): Disposable = observable.subscribe(subscribe)
}

/**@SelfDocumented**/
fun <T> Observable<T>.subjectProvider(setter: ReadWriteProperty<Any?, T?>): ProviderSubject<T> = ProviderSubject(this, setter)
/**@SelfDocumented**/
@Suppress("MemberVisibilityCanBePrivate")
fun <T> BehaviorSubject<T>.subjectProvider(): ProviderSubject<T> = ProviderSubject(this, delegateProperty({ value }, { it?.let { onNext(it) } }))
