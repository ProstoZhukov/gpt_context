package ru.tensor.sbis.edo.additional_fields.decl

import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика компонента доп полей
 * Реализация поставляется через EdoAdditionalFieldsPlugin
 * Для её получения в прикладном модуле добавьте этот интерфейс в зависимости плагина
 *
 * @author sa.nikitin
 */
interface AdditionalFieldsComponentFactory : Feature {

    /**
     * Создать компонент
     * Необходимо вызвать до super.onStart прикладной активности или фрагмента
     *
     * Повторные вызовы этого метода в рамках жизни одного экрана не приведут к созданию нового компонента
     *
     * @param storeOwner    Владелец хранилища, в котором компонент будет "жить"
     */
    fun getOrCreateComponent(
        storeOwner: ViewModelStoreOwner,
        savedStateOwner: SavedStateRegistryOwner,
        uniqueInstanceKey: String? = null,
    ): AdditionalFieldsComponent
}