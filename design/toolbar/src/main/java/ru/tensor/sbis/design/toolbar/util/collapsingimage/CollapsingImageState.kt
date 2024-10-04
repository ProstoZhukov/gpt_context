package ru.tensor.sbis.design.toolbar.util.collapsingimage

/**
 * Состояние анимации изображения.
 *
 * @author us.bessonov
 */
internal sealed interface CollapsingImageState

/**
 * В процессе анимации сворачивания
 */
internal data class Collapsing(
    val fraction: Float,
    val targetImageEnd: Float,
    val targetImageTop: Float,
    val targetImageBottom: Float
) : CollapsingImageState

/**
 * В процессе анимации разворачивания
 */
internal data class Expanding(val fraction: Float) : CollapsingImageState

/**
 * Свёрнуто, в промежуточном состоянии
 */
internal data class Collapsed(
    val imageEnd: Float,
    val imageTop: Float,
    val imageBottom: Float
) : CollapsingImageState

/**
 * В конечном состоянии - свёрнуто или развёрнуто
 */
internal object Settled : CollapsingImageState