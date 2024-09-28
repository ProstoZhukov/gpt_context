package ru.tensor.sbis.cashboxes_lite_decl.feature.checker

import ru.tensor.sbis.plugin_struct.feature.Feature

/** Объект для проверки фичей, связанных с маркированной продукцией. */
interface MarkingFeatureChecker : Feature {

    /** Проверить включённость фичи "Функционал проверки проблемных кодов". */
    suspend fun isMarkingCodeCheckFeatureEnabled(): Boolean

    /** Проверить включен ли запретительный режим на кассе. */
    suspend fun isMarkingCodeCheckBlockFeatureEnabled(): Boolean
}