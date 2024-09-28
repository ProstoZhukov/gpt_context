package ru.tensor.sbis.edo_decl.passage.mass_passage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Результат работы компонента массовых переходов
 *
 * @param totalDocsCount            Количество документов для выполнения переходов по ним
 * @param successProcessedDocsCount Количество документов с успешно выполненными переходами
 * @param failedProcessedDocsCount  Количество документов с переходами, которые выполнить не удалось
 *
 * @author sa.nikitin
 */
@Parcelize
data class MassPassagesResult(
    val totalDocsCount: Int,
    val successProcessedDocsCount: Int,
    val failedProcessedDocsCount: Int
) : Parcelable