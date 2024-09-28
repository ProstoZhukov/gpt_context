package ru.tensor.sbis.cadres_docs_decl.achievements

/**
 * Поддерживаемые типы документов для открытия на карточке "ПиВ"
 *
 * @param docType - строковое значение типов ПИВ соответствующих значениям с контроллера
 *  */
enum class AchievementsType(val docType: String) {
    INCENTIVE("Поощрение"),
    PENALTY("Взыскание");

    companion object {
        /** @SelfDocumented */
        fun toAchievementsType(docType: String): AchievementsType? =
            values().firstOrNull { it.docType == docType }
    }
}