package ru.tensor.sbis.communicator.contacts_declaration.registry

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Роутер для навигации реестра контактов
 *
 * @author da.zhukov
 */
interface ContactsRouter {

    /**
     * Инициализация роутера и его зависимостей.
     * @param fragment фрагмент, для которого будет осуществляться навигация.
     */
    fun initRouter(fragment: Fragment)

    /**
     * Отсоединение роутера, в этом месте произойдет очистка зависимостей и ссылок.
     */
    fun detachRouter()

    /**
     * Очистить details контейнер планшета.
     */
    fun removeSubContent()

    /**
     * Показать экран переписки.
     * @param personUuid идентификатор персоны для открытия переписки (диалога/чата).
     */
    fun startNewConversation(personUuid: UUID)

    /**
     * Показать профиль сотрудника
     *
     * @param uuid идентификатор сотрудника
     */
    fun showProfile(uuid: UUID)

    /**
     * Показать экран подтверждения номера телефона
     *
     * @param registryContainerId контейнер для открытия фрагмента
     */
    fun showVerificationFragment(@IdRes registryContainerId: Int)


    /**
     * Поставщик роутера [ContactsRouter]
     */
    interface Provider : Feature {

        /** @SelfDocumented */
        fun getContactsRouter(): ContactsRouter
    }
}