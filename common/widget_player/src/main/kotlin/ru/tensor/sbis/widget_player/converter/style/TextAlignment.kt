package ru.tensor.sbis.widget_player.converter.style

/**
 * @author am.boldinov
 */
enum class TextAlignment {
    LEFT,
    CENTER,
    RIGHT;

    companion object {

        @JvmStatic
        fun fromValue(align: String?): TextAlignment? {
            return when (align) {
                "left"    -> LEFT
                "right"   -> RIGHT
                "center"  -> CENTER
                "justify" -> LEFT
                else      -> null
            }
        }
    }
}