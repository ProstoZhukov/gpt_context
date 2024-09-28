package ru.tensor.sbis.person_decl.motivation

/**
 * Конфигурация для отображения View с актуальными бейджами пользователя
 *
 * @property badgeSize - размер бейджа
 * @property lineCount - количество отображаемых строк с бейджами
 * @property insideBorders - true если бейджи необходимо вписать в границы View, false если они должны заезжать за границы
 * @property rowDividerSize - размер отступа между бейджами по горизонтали
 * @property columnDividerSize - размер отступа между бейджами по вертикали
 *
 * @author am.boldinov
 */
data class ActualBadgesViewConfiguration(
    val badgeSize: Int,
    val lineCount: Int,
    val insideBorders: Boolean,
    val rowDividerSize: Int,
    val columnDividerSize: Int? = null
)