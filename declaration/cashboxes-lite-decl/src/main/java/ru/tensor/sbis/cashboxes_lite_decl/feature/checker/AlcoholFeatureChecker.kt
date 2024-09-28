package ru.tensor.sbis.cashboxes_lite_decl.feature.checker

import ru.tensor.sbis.plugin_struct.feature.Feature

/** Объект для проверки фичей, связанных с алкогольной продукцией. */
interface AlcoholFeatureChecker : Feature {

    /** Проверить включённость фичи "Разливной крепкий алкоголь". */
    suspend fun isStrongAlcoholFeatureEnabled(): Boolean
}