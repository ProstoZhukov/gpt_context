package ru.tensor.sbis.wrhdoc_decl.wizard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.wizard.decl.result.FinalStepResultData
import java.util.UUID

/**
 * Результат мастера создания складских документов
 * @property extId расширенный идентификатор
 * @property docType тип документа
 * @property uuid идентификатор документа
 *
 * @author as.mozgolin
 */
@Parcelize
data class WrhDocWizardResult(
    val extId: String,
    val docType: String,
    val uuid: UUID,
) : Parcelable, FinalStepResultData
