package ru.tensor.sbis.design.retail_views.payment_view.internal.delegates.api

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.annotation.EmptySuper
import androidx.databinding.ViewDataBinding
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.IncludeViewsInitializeApi

/** Интерфейс для описания Api первоначальной настройки работы делегата.  */
interface BaseRenderApi<BINDING : ViewDataBinding, INITIALIZE_API : BaseInitializeApi> {

    /** Объект предоставляющий доступ к API [Handler]. */
    val renderApiHandler: Handler<BINDING, INITIALIZE_API>

    /** Интерфейс объекта предоставляющего доступ к API [BaseRenderApi]. */
    interface Handler<BINDING : ViewDataBinding, INITIALIZE_API : BaseInitializeApi> {

        /** Поле для доступа к Binding объекту делегата. */
        val viewDelegateBinding: BINDING

        /** Создание и инфлейт View делегата. */
        fun inflateViewDelegateBinding(layoutInflater: LayoutInflater, rootView: ViewGroup): BINDING

        /** Действие выполняемое сразу после инфлейта делегата. */
        @CallSuper
        fun doAfterInflate(initializeApi: INITIALIZE_API) {
            /* Выполняем блокировку клавиатуры устройства, т.к. у нас своя. */
            getFieldsForLockSoftKeyboard(initializeApi).forEach { editText ->
                editText.showSoftInputOnFocus = false
            }

            /* Выполняем первоначальную настройку для include views. */
            getIncludeViewsInitializeApi(initializeApi).forEach { initializeApi ->
                initializeApi.initialize()
            }
        }

        /** Список полей ввода для отключения экранной клавиатуры для них. */
        @EmptySuper
        fun getFieldsForLockSoftKeyboard(initializeApi: INITIALIZE_API): List<EditText> = listOf()

        /** Список полей ввода для отключения экранной клавиатуры для них. */
        @EmptySuper
        fun getIncludeViewsInitializeApi(initializeApi: INITIALIZE_API): List<IncludeViewsInitializeApi> = listOf()
    }
}