package ru.tensor.sbis.widget_player.converter.internal

/**
 * @author am.boldinov
 */
internal object ConverterParams {

    val EMPTY_ATTRIBUTES = HashMap<String, String>(0)

    object ReservedTag {
        const val FRAME_ROOT = "frame"
        const val FRAME_ERROR = "FrameError"
        const val TEXT_WIDGET = "FormattedText"
    }

    object Type {
        const val TAG = "tag"
        const val TEXT = "text"
    }

    object HeaderMetaType {
        const val RESOURCE = "resource"
    }

}