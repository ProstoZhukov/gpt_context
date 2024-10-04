package ru.tensor.sbis.hallscheme.v2.business

/**
 * Отступы от границ вью до столешницы стола.
 */
internal class TablePadding(
    /**@SelfDocumented*/
    val left: Int,

    /**@SelfDocumented*/
    val top: Int,

    /**@SelfDocumented*/
    val right: Int,

    /**@SelfDocumented*/
    val bottom: Int
) {
    /**@SelfDocumented*/
    val horizontal: Int = left + right

    /**@SelfDocumented*/
    val vertical: Int = top + bottom
}