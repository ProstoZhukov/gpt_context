package ru.tensor.sbis.edo_decl.doc_opener.doc_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Модель документа для открытия
 *
 * @author sa.nikitin
 */
abstract class DocModel : Parcelable {
    abstract val docUuid: UUID
    abstract val docType: String
    abstract val docUrl: String?
    abstract val docTitle: String
    @Deprecated("Не следует передавать, синхронизация УПФ будет автоматически по docUuid и docType")
    abstract val printFormId: DocPrintFormId?
    open val customArgs: Parcelable?
        get() = null
}

@Parcelize
class DefaultDocModel(
    override val docUuid: UUID,
    override val docType: String,
    override val docUrl: String? = null,
    override val docTitle: String,
    override val printFormId: DocPrintFormId? = null,
    override val customArgs: Parcelable? = null,
) : DocModel()
