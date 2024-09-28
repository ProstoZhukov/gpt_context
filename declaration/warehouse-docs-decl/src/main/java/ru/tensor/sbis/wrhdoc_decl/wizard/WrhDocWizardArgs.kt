package ru.tensor.sbis.wrhdoc_decl.wizard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Аргументы для прикладных шагов мастера создания складских документов
 * @property clientUUID идентификатор клиента, для пропуска шага выбора клиента
 * @property scannedUri вложения, при создании документа через распознавание
 * @property closeOnSave закрыть карточку при нажатии на зеленую галку
 *
 * @author as.mozgolin
 */
@Parcelize
data class WrhDocWizardArgs(
    val clientUUID: UUID? = null,
    val scannedUri: List<String>? = null,
    val closeOnSave: Boolean = false
) : Parcelable
