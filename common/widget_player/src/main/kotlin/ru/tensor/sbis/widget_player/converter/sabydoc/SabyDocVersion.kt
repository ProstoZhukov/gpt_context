package ru.tensor.sbis.widget_player.converter.sabydoc

/**
 * @author am.boldinov
 */
enum class SabyDocVersion {
    JSONML,
    FRAME;

    internal companion object {

        /**
         * В случае если не удалось распознать версию - возвращает [JSONML] по умолчанию.
         */
        @JvmStatic
        fun fromString(version: String?): SabyDocVersion {
            return when (version?.lowercase()) {
                "frame" -> FRAME
                else    -> JSONML
            }
        }

    }
}