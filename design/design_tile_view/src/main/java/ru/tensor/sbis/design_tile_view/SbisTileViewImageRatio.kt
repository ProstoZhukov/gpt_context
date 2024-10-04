package ru.tensor.sbis.design_tile_view

/**
 * Соотношение сторон изображения
 *
 * @author us.bessonov
 */
enum class SbisTileViewImageRatio(internal val fraction: Float) {
    /** Квадрат */
    SQUARE(1f),
    /** 3:4 */
    THREE_TO_FOUR(0.75f),
    /** 4:3 */
    FOUR_TO_THREE(1.333f),
    /** 16:9 */
    WIDE(1.778f);

    companion object {
        /** @SelfDocumented */
        internal val DEFAULT_FRACTION = SQUARE.fraction
    }
}