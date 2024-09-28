package ru.tensor.sbis.business.common.domain.filter.navigation

/**
 * Тип навигации
 */
enum class NavigationType {
    /**по узлам иерархии*/
    MULTI_ROOT,

    /**постраничная*/
    PAGE,

    /**смещением*/
    OFFSET,

    /**по курсору*/
    POSITION,

    /**все записи без навигации*/
    NULL
}

/**
 * Направление возвращаемых записей (для навигации с типом ntPOSITION)
 */
enum class Direction {
    /**after*/
    FORWARD,

    /**both*/
    BOTHWAYS,

    /**before*/
    BACKWARD
}
