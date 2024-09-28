package ru.tensor.sbis.main_screen_decl.basic

import android.app.Activity
import android.content.Intent
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import ru.tensor.sbis.main_screen_decl.basic.data.OverlayStatusBarBackgroundPanelBehavior
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenEntryPoint
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenId
import ru.tensor.sbis.main_screen_decl.env.MainScreenHost
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.verification_decl.permission.PermissionScope

/**
 * API компонента Раскладка (главный экран без ННП и аккордеона).
 *
 * @author us.bessonov
 */
interface BasicMainScreenViewApi {

    /**
     * Хост компонента главного экрана.
     */
    val host: MainScreenHost

    /**
     * Инициализировать компонент.
     *
     * @param host хост главного экрана.
     * @param fragmentManager [FragmentManager] для размещения прикладного контента.
     * @param contentController реализация контроллера контента под шапкой (разводящей). Если требуется только
     * создание экрана, то достаточно использовать `SimpleContentController`.
     * @param contentScreenId идентификатор экрана разводящей.
     * @param intentHandleExtensions расширения для обработки поступающих [Intent].
     * @param customTopContainerId кастомный идентификатор контейнера для размещения контента поверх разводящей и
     * шапки. По умолчанию используется идентификатор [View] самого компонента.
     * @param monitorPermissionsOnLifecycle требуется ли проверка разрешений на экраны.
     */
    fun setup(
        host: MainScreenHost,
        fragmentManager: FragmentManager,
        contentController: BasicContentController,
        contentScreenId: ScreenId = DEFAULT_SCREEN_ID,
        intentHandleExtensions: List<IntentHandleExtension<out IntentHandleExtension.ExtensionKey>> = emptyList(),
        @IdRes
        customTopContainerId: Int? = null,
        monitorPermissionsOnLifecycle: Boolean = false
    )

    /**
     * Настроить шапку.
     */
    fun configureTopNavigation(configure: TopNavigationConfigurator.() -> Unit)

    /**
     * Установить прозрачный цвет статусбара.
     * Предусматривает отображение искусственного фона под статусбара при открытии карточек.
     *
     * @param overlayStatusBarBackgroundPanelBehavior определяет поведение показа/отображения фона под статусбаром
     * (при значении `null`, используется поведение по умолчанию, отображая панель при наличии хотя бы одного экрана с
     * содержимым помимо основного контента).
     */
    fun enableTransparentStatusBar(
        overlayStatusBarBackgroundPanelBehavior: OverlayStatusBarBackgroundPanelBehavior? = null
    )

    /**
     * Активировать компонент.
     * Метод должен быть вызван после полного завершения конфигурации компонента.
     */
    fun activate()

    /**
     * Передать компоненту новый [Intent], поступивший в [Activity]
     */
    fun onNewIntent(intent: Intent)

    /**
     * Получить [Fragment], отображённый средствами компонента.
     * По умолчанию, использует для поиска дефолтный идентификатор контента под шапкой, если он не переопределялся в
     * [setup].
     *
     * @param id Идентификатор [ScreenEntryPoint], которой соответствует искомый [Fragment].
     */
    fun findDisplayedScreen(id: ScreenId = DEFAULT_SCREEN_ID): Fragment?

    /**
     * Метод для мониторинга прав по определенной зоне.
     *
     * @param permissionScope зона доступа.
     */
    fun monitorPermissionScope(permissionScope: PermissionScope): LiveData<PermissionLevel?>

    /**
     * Метод для получения доступных расширений по обработке [Intent].
     *
     * @param key ключ расширения.
     */
    fun <K : IntentHandleExtension.ExtensionKey, E : IntentHandleExtension<K>> getIntentHandleExtension(key: K): E?
}

private val DEFAULT_SCREEN_ID = ScreenId.Tag("Default screen")