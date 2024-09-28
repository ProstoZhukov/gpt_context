package ru.tensor.sbis.common.rx.livedata

import androidx.lifecycle.LiveData
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.toLiveData
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.rx.consumer.subscribeWithFallback
import java.util.concurrent.TimeUnit

/**
 * Небольшой набор шорткатов для обеспечения вью биндинга на основе rx компонентов.
 * Направлен на обеспечение MVVM паттернов без liveData на основе RX цепочек.
 * @author Subbotenko Dmitry
 */

/**
 * Текущее значение из Flowable если он установлен из BehaviorProcessor. В противном случае возвращает null всегда.
 */
val <T> Flowable<RxContainer<T>>.value: T? get() = (this as? BehaviorProcessor<RxContainer<T>>)?.value?.value

/**
 * Текущее значение из Observable если он установлен из BehaviorSubject. В противном случае возвращает null всегда.
 */
val <T> Observable<RxContainer<T>>.value: T? get() = (this as? BehaviorSubject<RxContainer<T>>)?.value?.value

/**
 * Получение androidLiveData из Flowable. Пригодно для биндинга компонентов.
 */
val <T> Flowable<RxContainer<T>>.androidLiveData: LiveData<T?>
    get() = this.toLiveData().map { it.value }.distinctUntilChanged()

/**
 * Шорткат для взятия и установки значений BehaviorProcessor
 */
var <T> BehaviorProcessor<RxContainer<T>>.dataValue: T?
    get() = this.value?.value
    set(value) = this.onNext(RxContainer(value))

/**
 * Шорткат для взятия и установки значений BehaviorSubject
 */
var <T> BehaviorSubject<RxContainer<T>>.dataValue: T?
    get() = this.value?.value
    set(value) = this.onNext(RxContainer(value))

/**
 *  Простой биндинг вью компонента к RX цепочке
 *  Посути это обычная подписка, со сменой потока на mainThread, + throttleLatest для того чтобы опускать слишком частые события.
 *
 */
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T> Flowable<out RxContainer<T>>.bind(onNext: (T?) -> Unit) = this
        .filter { !it.fromView }
        .throttleLatest(100, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWithFallback { onNext.invoke(it.value) }

/**
 *  Простой биндинг вью компонента к RX цепочке
 *  Посути это обычная подписка, со сменой потока на mainThread, + throttleLatest для того чтобы опускать слишком частые события.
 *
 */
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T> Observable<out RxContainer<T>>.bind(onNext: (T?) -> Unit) = this
        .filter { !it.fromView }
        .throttleLatest(100, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWithFallback { onNext.invoke(it.value) }

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T> Observable<out RxContainer<T>>.bindWithDistinct(onNext: (T?) -> Unit) = this
    .filter { !it.fromView }
    .distinctUntilChanged()
    .observeOn(AndroidSchedulers.mainThread())
    .subscribeWithFallback { onNext.invoke(it.value) }

//TODO: Добавлять сюда методы twoWayObserver для работы с другими типами view
/**
 * Двусторонний датабиндинг для EditText.
 *
 * @param editText
 */
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun BehaviorSubject<RxContainer<String>>.twoWayObserver(editText: EditText): Disposable {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (dataValue != s.toString())
                onNext(RxContainer(s.toString(), true))
        }
    })

    return bind {
        if (editText.text.toString() != it) {
            editText.setText(it, TextView.BufferType.NORMAL)
            editText.setSelection(it?.length ?: 0)
        }
    }
}

/**
 * Двусторонний датабиндинг для EditText.
 */
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun Observable<RxContainer<String>>.twoWayObserver(editText: EditText, consumer: (RxContainer<String>) -> Unit): Disposable {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (value != s.toString())
                consumer(RxContainer(s.toString(), true))
        }
    })

    return bind {
        if (editText.text.toString() != it) {
            editText.setText(it, TextView.BufferType.NORMAL)
            editText.setSelection(it?.length ?: 0)
        }
    }
}

/**
 * Двусторонний датабиндинг для EditText.
 *
 * @param editText
 */
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun BehaviorProcessor<RxContainer<String>>.twoWayObserver(editText: EditText): Disposable {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (dataValue != s.toString())
                onNext(RxContainer(s.toString(), true))
        }
    })

    return bind {
        if (editText.text.toString() != it) {
            editText.setText(it, TextView.BufferType.NORMAL)
            editText.setSelection(it?.length ?: 0)
        }
    }
}