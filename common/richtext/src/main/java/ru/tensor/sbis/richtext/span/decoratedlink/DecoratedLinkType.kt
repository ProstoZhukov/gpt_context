package ru.tensor.sbis.richtext.span.decoratedlink

/**
 * Типы отображения декорированных ссылок
 *
 * @author am.boldinov
 */
enum class DecoratedLinkType(val raw: String) {
    SMALL("s"),
    MEDIUM("m"),
    LARGE("l"),
    EXTRA_LARGE("xl");

    companion object {

        @JvmStatic
        fun fromValue(value: String?): DecoratedLinkType? {
            return value?.let { raw ->
                values().find { it.raw == raw }
            }
        }
    }
}