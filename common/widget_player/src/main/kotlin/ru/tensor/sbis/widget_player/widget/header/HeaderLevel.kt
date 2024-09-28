package ru.tensor.sbis.widget_player.widget.header

/**
 * @author am.boldinov
 */
enum class HeaderLevel(val value: Int) {
    H0(0),
    H1(1),
    H2(2),
    H3(3),
    H4(4),
    H5(5),
    H6(6);

    companion object {

        fun fromValue(value: Int): HeaderLevel? {
            return values().find { it.value == value }
        }
    }
}