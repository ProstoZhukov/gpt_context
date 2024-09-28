package ru.tensor.sbis.communicator.common.navigation.contract

import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Роутер для навигации хост фрагмента.
 *
 * @author da.zhukov
 */
interface CommunicatorHostRouter : DeeplinkActionNode {

    /**
     * Инициализация роутера и его зависимостей.
     * @param fragment фрагмент, в котором будет осуществляться навигация.
     */
    fun initCommunicatorRouter(fragment: Fragment)

    /**
     * Отсоединение роутера, в этом месте произойдет очистка зависимостей и ссылок.
     */
    fun detachCommunicatorRouter()

    /**
     * Поменять реестр.
     *
     * @param registryType тип реестра, который нужно открыть в хосте.
     */
    fun changeRegistry(registryType: CommunicatorRegistryType)

    /**
     * Открыть экран по интенту [intent].
     *
     * Если для [Intent.getAction] найдется поставщик соответствующего фрагмента,
     * то он может открыться в 2ух типах контейнеров:
     * - если [useOverlayDetailContainer] == true и тип реестра поддерживает overlayContainer для данной конфигурации,
     * то откроется в контейнере, находящемся поверх view реестра на верхнем слое activity.
     * - на планшете фрагмент откроется в details контейнере рядом с реестром.
     * Если поставщика соответствующего фрагмента не найдется - откроется на activity.
     * [onCloseCallback] - колбэк о закрытии фрагмента.
     */
    fun openScreen(
        intent: Intent,
        useOverlayDetailContainer: Boolean = false,
        onCloseCallback: (() -> Unit)? = null,
        fragmentProvider: () -> Fragment? = { null }
    )

    /**
     * Обработка внешнего [DeeplinkAction].
     *
     * @param args диплинк для обработки.
     */
    fun handleDeeplinkAction(args: DeeplinkAction)

    /**
     * Показать фрагмент в details контейнере.
     */
    fun setSubContent(fragment: Fragment)

    /**
     * Очистить details контейнер планшета.
     */
    fun removeSubContent()

    /**
     * Вернуться назад по стэку фрагментов.
     */
    fun popBackStack(): Boolean

    /**
     * Проверка возможности возвращения назад по стеку.
     */
    fun canPopBackStack(): Boolean = false

    /**
     * Получить верхний фрагмент в details контейнере.
     */
    fun getTopSubContent(): Fragment?

    /**
     * Изменить выбранный элемент в реестре.
     *
     * @param uuid идентификатор элемента.
     */
    fun changeRegistrySelectedItem(uuid: UUID)

    /**
     * Поставщик роутера [CommunicatorHostRouter].
     */
    interface Provider : Feature {

        /** @SelfDocumented */
        fun getCommunicatorHostRouter(): CommunicatorHostRouter
    }
}