package ru.tensor.sbis.date_picker.adapter.viewholder

import androidx.databinding.BaseObservable
import androidx.databinding.Observable

/**
 * Вспомогательный класс для возможности отписки от обновлений [BaseObservable]
 *
 * @author us.bessonov
 */
internal class ObservableFieldDisposable(
    private val callback: Observable.OnPropertyChangedCallback,
    private val observable: BaseObservable
) {
    /**
     * Удаляет [callback] у [observable]
     */
    fun dispose() = observable.removeOnPropertyChangedCallback(callback)
}

/**
 * Выполняет [action] при подписке и при обновлении свойства
 */
internal fun BaseObservable.subscribe(action: () -> Unit): ObservableFieldDisposable {
    val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            action()
        }
    }
    addOnPropertyChangedCallback(callback)
    action()
    return ObservableFieldDisposable(callback, this)
}