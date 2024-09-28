package ru.tensor.sbis.edo_decl.doc_opener.doc_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Идентификатор УПФ документа
 *
 * @property attachId       Идентификатор УПФ как вложения
 * @property redactionId    Идентификатор редакции УПФ как вложения
 *
 * @author sa.nikitin
 */
@Parcelize
class DocPrintFormId(val attachId: UUID, val redactionId: UUID?) : Parcelable