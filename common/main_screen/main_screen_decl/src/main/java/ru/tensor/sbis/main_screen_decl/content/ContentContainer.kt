package ru.tensor.sbis.main_screen_decl.content

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.main_screen_decl.env.BottomBarProviderExt

/**
 * Контейнер, содержащий всю необходимую информацию для работы с контентом.
 *
 * @property containerId идентификатор контейнера для встраивания контента
 * @property fragmentManager
 * @property scrollHelper
 * @property bottomBarProvider поставщик нижней панели действий
 *
 * @author kv.martyshenko
 */
class ContentContainer(
    @IdRes val containerId: Int,
    val fragmentManager: FragmentManager,
    val scrollHelper: ScrollHelper,
    val bottomBarProvider: BottomBarProviderExt
)