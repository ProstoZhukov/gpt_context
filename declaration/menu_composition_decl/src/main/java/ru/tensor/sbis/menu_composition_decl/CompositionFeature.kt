package ru.tensor.sbis.menu_composition_decl

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize
import ru.tensor.retail.compositions_decl.CompositionsFeature
import ru.tensor.retail.compositions_decl.DialogFragmentWithDismissListener
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фичи модуля "Модификаторы".
 **/
// TODO: Вынести по задаче https://dev.sbis.ru/opendoc.html?guid=35b6085a-2949-4d50-a9e1-8b9d4dc85d3b&client=3
interface CompositionFeature : Feature {

    companion object {
        const val TAG = "compositionTag"
    }

    /** Создать фрагмент со списком модификаторов. */
    fun createCompositionsDialogFragment(
        compositionMode: CompositionMode,
        compositionExternalData: CompositionExternalData = CompositionExternalData()
    ): DialogFragmentWithDismissListener

    /** Создать фрагмент со списком модификаторов. */
    @Deprecated("Перейти на CompositionMode")
    fun createCompositionsDialogFragment(
        moduleMode: CompositionsFeature.Presentation.ModuleMode,
        compositionExternalData: CompositionExternalData = CompositionExternalData()
    ): DialogFragmentWithDismissListener

    /** Вызов сохранения модификаторов. */
    fun compositionSave(moduleMode: CompositionsFeature.Presentation.ModuleMode)

    /** Сохранить комплект с данными по весу. */
    fun compositionSave(unit: String, quantity: Double)

    /**
     *  Подписаться на события поставляемые модулем.
     *  !!! События будут приходить ТОЛЬКО первому подписчику. Все последующие подписчики будут игнорироваться.
     **/
    fun compositionEvents(): Flow<CompositionEvent>

    /** Закрыть экран. */
    fun closeCompositionScreen()

    /** Показать диалог подтверждения применения изменений без учета ошибки. */
    fun showRequiredDialogLabel(error: String)

    /** Скрыть крутилку. */
    fun hideLoading()

    /** События поставляемые модулем. */
    sealed class CompositionEvent : Parcelable {

        /** Нажата кнопка сохранения модификаторов. */
        @Parcelize
        data class Save(
            /** Список выбранных модификаторов. */
            val ids: String,
            val compositionMode: CompositionMode,
            /** Внешние данные для экрана выбора комплектов и модификаторов. */
            val compositionExternalData: CompositionExternalData
        ) : CompositionEvent()

        /** Нажата кнопка закрытия экрана. */
        @Parcelize
        object CloseScreen : CompositionEvent()
    }
}