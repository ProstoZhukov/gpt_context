package ru.tensor.sbis.main_screen_decl.basic

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.main_screen_decl.basic.data.ContentHost
import ru.tensor.sbis.main_screen_decl.content.ContentController

/**
 * Контроллер прикладного экрана.
 *
 * @author us.bessonov
 */
interface BasicContentController {

    /**
     * Создать [Fragment] экрана.
     */
    fun createScreen(
        entryPoint: ContentController.EntryPoint,
        mainScreen: BasicMainScreenViewApi,
        contentHost: ContentHost
    ): Fragment

    /**
     * Метод для восстановления экрана после смены конфигурации.
     */
    fun restore(
        mainScreen: BasicMainScreenViewApi,
        contentHost: ContentHost,
        fragment: Fragment
    ) = Unit

    /**
     * Метод для выполнения обновления контента (после пуш-уведомления/диплинка).
     */
    fun update(
        entryPoint: ContentController.EntryPoint,
        contentHost: ContentHost
    ) = Unit

    /**
     * Метод для обработки перехода в режим [Lifecycle.State.STARTED].
     */
    fun start(contentHost: ContentHost) = Unit

    /**
     * Метод для обработки перехода в режим [Lifecycle.State.RESUMED].
     */
    fun resume(contentHost: ContentHost) = Unit

    /**
     * Метод для обработки перехода в режим `Lifecycle.PAUSED`.
     */
    fun pause(contentHost: ContentHost) = Unit

    /**
     * Метод для обработки перехода в режим `Lifecycle.STOPPED`.
     */
    fun stop(contentHost: ContentHost) = Unit

    /**
     * Метод для обработки действий назад.
     *
     * @return true, если действие обработано.
     */
    fun backPressed(
        mainScreen: BasicMainScreenViewApi,
        contentHost: ContentHost
    ): Boolean = false

}