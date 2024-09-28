package ru.tensor.sbis.main_screen_decl.basic.data

import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder

/**
 * Способ размещения прикладного экрана.
 *
 * @author us.bessonov
 */
sealed interface ContentPlacement

/**
 * Внутри (на месте разводящей).
 */
object Inside : ContentPlacement

/**
 * Поверх разводящей и шапки.
 */
object OnTop : ContentPlacement

/**
 * В [OverlayFragmentHolder].
 */
class InOverlayContainer(val swipeable: Boolean = true) : ContentPlacement
