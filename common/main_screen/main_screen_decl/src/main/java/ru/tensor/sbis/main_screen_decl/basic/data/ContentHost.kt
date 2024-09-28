package ru.tensor.sbis.main_screen_decl.basic.data

import android.content.Context
import androidx.annotation.IdRes
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder

/**
 * Хост прикладного контента.
 *
 * @param fragmentContainer контейнер для экранов, открываемых с разводящей поверх неё.
 * @param mainContainerId контейнер разводящей.
 * @param overlayFragmentHolder опциональный инструмент для размещения содержимого поверх всего контента.
 *
 * @author us.bessonov
 */
class ContentHost(
    val context: Context,
    val fragmentContainer: FragmentContainer,
    @IdRes val mainContainerId: Int,
    val overlayFragmentHolder: OverlayFragmentHolder?
)