package ru.tensor.retail.compositions_decl

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentOnAttachListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.parcelize.Parcelize
import ru.tensor.retail.compositions_decl.actionlistener.BaseActionListenerTemp
import ru.tensor.retail.compositions_decl.actionlistener.extensions.setActionListenerIfHisProvidedTemp
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Контракт фичи "Модификаторы", "Комплекты".
 *
 * @author da.pavlov1
 */
interface CompositionsFeature {

    companion object {
        /**
         * Утилита для упрощенного доступа к внешнему слушателю.
         *
         * @param listener слушатель событий от фрагмента.
         * @param deepSearchNeeded необходимость подписки на события child фрагментов.
         * p.s. Если слушатель не устанавливается, то зачастую достаточно просто взвести этот флажок.
         *
         * @return [FragmentOnAttachListener]
         */
        fun createCompositionActionListenerAttachHelper(
            listener: BaseActionListenerTemp,
            deepSearchNeeded: Boolean = false
        ): FragmentOnAttachListener = object : FragmentOnAttachListener {
            override fun onAttachFragment(fragmentManager: FragmentManager, childFragment: Fragment) {
                /* Хак, для использования внутри LifecycleEventObserver.onStateChanged(...) */
                val innerAttachListener = this

                object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            childFragment.childFragmentManager.removeFragmentOnAttachListener(innerAttachListener)

                            childFragment.lifecycle.removeObserver(this)
                        }
                    }
                }.let { lifecycleObserver ->
                    childFragment.lifecycle.addObserver(lifecycleObserver)
                }

                if (!deepSearchNeeded) {
                    childFragment.setActionListenerIfHisProvidedTemp(listener)
                } else {
                    childFragment.childFragmentManager.addFragmentOnAttachListener(innerAttachListener)
                }
            }
        }
    }

    /** Описание объекта отвечающего за предоставление Presentation компонентов. */
    interface Presentation {

        /** Интерфейс для предоставления фичи [CompositionsFeature.Presentation]. */
        interface Provider : Feature {

            /** Метод для получения фичи [CompositionsFeature.Presentation]. */
            fun getRetailCompositionsPresentationFeature(): Presentation?
        }

        /** Интерфейс для предоставления фичи с новой версией модификаторов  [CompositionsFeature.Presentation]. */
        interface ProviderComposition : Feature {

            /** Метод для получения фичи [CompositionsFeature.Presentation]. */
            fun getRetailCompositionsPresentationFeature(): Presentation?
        }

        /** Интерфейс для оповещения о событиях модуля 'Compositions'. */
        interface ActionListener : BaseActionListenerTemp {

            /** Событие "составляющие были изменены". */
            fun onCompositionsWasChanged()
        }

        /** Режимы работы модуля. */
        sealed class ModuleMode : Parcelable {

            /** Карточка номенклатуры. */
            @Parcelize
            data class CardMode(val saleNomenclatureId: UUID) : ModuleMode()

            /** Список меню. */
            @Parcelize
            data class MenuMode(
                val saleId: UUID,
                val compositionId: String,
                val nomenclatureTitle: String? = null,
                val guestIconNumber: Short? = null,
                val guestPosition: Short? = null,
                val unit: String? = null,
                val appliedQuantity: Double? = null
            ) : ModuleMode()
        }

        /**
         * Метод отвечает за получение [DialogFragmentWithDismissListener] для начала работы
         * с модулем 'compositions' (Модификаторы/Комплекты).
         *
         * @param moduleMode режим работы модуля.
         *
         * @return [Fragment] отвечающий за инициализацию модуля 'compositions'.
         */
        fun createCompositionsDialogFragment(
            moduleMode: ModuleMode,
            withOldVersion: Boolean = true
        ): DialogFragmentWithDismissListener
    }

    /** Описание объекта - "источник данных для модуля 'Compositions'". (Для особенностей МП Розница) */
    interface DataSource {

        /** Интерфейс для предоставления фичи [CompositionsFeature.DataSource]. */
        interface Provider : Feature {

            /** Метод для получения фичи [CompositionsFeature.DataSource]. */
            fun getRetailCompositionsDataSourceFeature(): DataSource?
        }

        /** Определение видимости кнопки "Модификаторы +" для номенклатуры [saleNomenclatureId]. */
        suspend fun getCompositionsBtnVisibility(
            saleNomenclatureId: UUID,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): Boolean

        /** Получить список примененных составляющих для номенклатуры [saleNomenclatureId]. */
        suspend fun getAppliedCompositions(
            saleNomenclatureId: UUID,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): List<CompositionElement>

        /** Источник данных для определения видимости кнопки "Модификаторы +". */
        @Deprecated("Используйте API на корутинах.", ReplaceWith("getCompositionsBtnVisibility"))
        fun getCompositionsBtnVisibilitySource(saleNomenclatureId: UUID): Single<Boolean>

        /** Источник данных для получения списка примененных составляющих. */
        @Deprecated("Используйте API на корутинах.", ReplaceWith("getAppliedCompositions"))
        fun getAppliedCompositionsDataSource(saleNomenclatureId: UUID): Single<List<CompositionElement>>

        /**
         * Модель составляющей для отображения в карточке.
         *
         * @param compositionTitle название составляющей.
         * @param appliedCount примененное кол-во.
         * @param unitName упаковка.
         * @param cost стоимость.
         */
        @Parcelize
        data class CompositionElement(
            val compositionTitle: String,
            val appliedCount: Double,
            val unitName: String?,
            val cost: Double?
        ) : Parcelable
    }
}