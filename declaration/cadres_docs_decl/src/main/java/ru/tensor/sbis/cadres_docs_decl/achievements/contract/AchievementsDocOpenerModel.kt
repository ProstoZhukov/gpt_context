package ru.tensor.sbis.cadres_docs_decl.achievements.contract

import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsActionSettings
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsOpenFrom
import ru.tensor.sbis.cadres_docs_decl.achievements.AchievementsScreenState

/** Интерфейс для моделей открытия ПиВ через docOpener. */
interface AchievementsDocOpenerModel {
    /** Тип, указывающий, откуда происходит открытие ПиВ. */
    val openFromType: AchievementsOpenFrom

    /** Тип, указывающий, в каком режиме необходимо открыть ПиВ. */
    val openScreenState: AchievementsScreenState

    /** Настройки допустимых действий над документом, если есть на них разрешения с онлайна. */
    val actionSettings: AchievementsActionSettings
}