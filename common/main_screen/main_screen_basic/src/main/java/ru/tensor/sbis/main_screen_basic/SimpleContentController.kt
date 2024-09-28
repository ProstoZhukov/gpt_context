package ru.tensor.sbis.main_screen_basic

import androidx.fragment.app.Fragment
import ru.tensor.sbis.main_screen_decl.basic.BasicContentController
import ru.tensor.sbis.main_screen_decl.basic.BasicMainScreenViewApi
import ru.tensor.sbis.main_screen_decl.basic.data.ContentHost
import ru.tensor.sbis.main_screen_decl.content.ContentController

/**
 * Минимальная реализация [BasicContentController].
 *
 * @author us.bessonov
 */
open class SimpleContentController(
    private val createScreenAction: (
        entryPoint: ContentController.EntryPoint,
        mainScreen: BasicMainScreenViewApi,
        contentHost: ContentHost
    ) -> Fragment
) : BasicContentController {

    override fun createScreen(
        entryPoint: ContentController.EntryPoint,
        mainScreen: BasicMainScreenViewApi,
        contentHost: ContentHost
    ) = createScreenAction(entryPoint, mainScreen, contentHost)
}