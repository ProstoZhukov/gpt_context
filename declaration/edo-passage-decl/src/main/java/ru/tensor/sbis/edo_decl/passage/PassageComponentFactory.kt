package ru.tensor.sbis.edo_decl.passage

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.edo_decl.passage.mass_passage.MassPassagesComponent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика компонента переходов
 * Реализация поставляется через EdoPassagePlugin, для её получения в прикладном модуле добавьте этот интерфейс в
 * зависимости плагина
 *
 * @author sa.nikitin
 */
interface PassageComponentFactory : Feature {

    /**
     * Создать компонент одиночного перехода
     *
     * Для старта процесса перехода необходимо вызвать [PassageComponent.start]
     *
     * Повторные вызовы этого метода в рамках жизни одного экрана не приведут к созданию нового компонента
     * Для старта нового процесса перехода используйте [PassageComponent.start]
     *
     * @param storeOwner Владелец хранилища, в котором компонент будет "жить"
     */
    fun getOrCreatePassageComponent(storeOwner: ViewModelStoreOwner): PassageComponent

    /**
     * Создать компонент массовых переходов
     *
     * Для старта процесса перехода необходимо вызвать [MassPassagesComponent.start]
     *
     * Повторные вызовы этого метода в рамках жизни одного экрана не приведут к созданию нового компонента
     * Для старта нового процесса перехода используйте [MassPassagesComponent.start]
     *
     * @param storeOwner Владелец хранилища, в котором компонент будет "жить"
     */
    fun createMassPassageComponent(storeOwner: ViewModelStoreOwner): MassPassagesComponent
}