package ru.tensor.sbis.edo_decl.doc_opener

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.edo_decl.doc_opener.doc_model.DocModel
import ru.tensor.sbis.edo_decl.doc_opener.card.factory.AppliedDocCardFactory

/**
 * Запрос на открытие документа
 *
 * @property moduleKey Ключ модуля, из которого происходит запрос, см. [AppliedDocCardFactory.moduleKey]
 *
 * @author sa.nikitin
 */
sealed class OpenDocRequest : Parcelable {
    abstract val moduleKey: String?
}

/**
 * Запрос на открытие документа по его модели [docModel]
 *
 *
 * @author sa.nikitin
 */
@Parcelize
class OpenDocByModelRequest(
    val docModel: DocModel,
    override val moduleKey: String? = null
) : OpenDocRequest()

/**
 * Запрос на открытие документа по ссылке на него [docUrl]
 * Вид ссылки - https://[stand-]online.sbis.ru/opendoc.html?guid="uuid"
 *
 * @author sa.nikitin
 */
@Parcelize
class OpenDocByUrlRequest(
    val docUrl: String,
    val docTitle: String = "",
    override val moduleKey: String? = null
) : OpenDocRequest()