package ru.tensor.sbis.widget_player.widget.paragraph

/**
 * @author am.boldinov
 */
enum class ParagraphLevel(val value: Int) {
    P1(1),
    P2(2),
    P3(3),
    P4(4),
    P5(5),
    P6(6),
    P7(7),
    P8(8),
    P9(9),
    P10(10);

    companion object {

        fun fromValue(value: Int): ParagraphLevel? {
            return ParagraphLevel.values().find { it.value == value }
        }
    }
}