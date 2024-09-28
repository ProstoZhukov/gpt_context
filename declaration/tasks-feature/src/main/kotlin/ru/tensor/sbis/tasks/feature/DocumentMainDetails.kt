package ru.tensor.sbis.tasks.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.Date
import java.util.UUID

/**
 * Облегчённая модель аргументов модели документа.
 * @property documentUuid идентификатор документа.
 * @property eventUuid идентификатор события.
 * @property docType тип документа.
 * @property registryDataAccentEventUuid идентификатор события реестровой записи.
 * @property regulationName название регламента.
 * @property documentState состояние документа, см. [DocumentState]. null - неизвестно.
 * @property creationDate дата создания документа.
 * @property isImportant флаг фажности документа, true - важный, false - нет.
 * @property isDeleted true если документ помечен как удалённый, false - нет.
 * @property hasBaseDocs true если есть базовые документы, false - нет.
 * @property milestoneNames названия вех документа.
 * @property term срок выполнения документа.
 * @property responsibleFace лицо ответственного за документ, см. [FaceInfo].
 * @property taskDescription описание документа.
 * @property commentOnPassage комментарий на переходе.
 * @property sum сумма.
 * @property contractor контрагент.
 *
 * @author aa.sviridov
 */
@Parcelize
data class DocumentMainDetails(
    val documentUuid: UUID,
    val eventUuid: UUID?,
    val docType: String,
    val registryDataAccentEventUuid: UUID?,
    val regulationName: String,
    val documentState: DocumentState?,
    val creationDate: Date?,
    val isImportant: Boolean,
    val isDeleted: Boolean,
    val hasBaseDocs: Boolean,
    val milestoneNames: List<String>,
    val term: Date?,
    val responsibleFace: FaceInfo?,
    val taskDescription: String,
    val commentOnPassage: String,
    val sum: BigDecimal?,
    val contractor: String,
) : Parcelable