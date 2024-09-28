package ru.tensor.sbis.cadres_docs_decl.achievements.achievements_section

import androidx.annotation.StringRes
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent

/** @SelfDocumented */
typealias AchievementsStubProvider = (Map<Int, () -> Unit>) -> StubViewContent

/**
 * Модель содержащая данные для показа заглушки при возникновении ошибки внутри
 * секции ПиВ, которая считается приоритетной.
 *
 * @param case - идентификатор ошибки в виде текстового ресурса с основной информацией.
 * В качестве идентификатора для заглушек производных от [StubViewCase]
 * используется [StubViewCase.messageRes].
 *
 * @param stubProvider - провайдер заглушки. На вход принимает внешние действия
 * выполняемые при нажатии на заглушку (cм. [StubViewContent.actions]).
 */
data class AchievementsStubContent(
    @StringRes val case: Int,
    val stubProvider: AchievementsStubProvider
)