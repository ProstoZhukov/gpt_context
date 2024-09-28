package ru.tensor.sbis.wrhdoc_decl.nomenclature.model

import java.util.UUID

/**
 * Информация о документе
 *
 * @property docUUID идентификатор документа
 * @property docTypeName строковое представление типа документа
 *
 * @author as.mozgolin
 */
data class DocInfo(
    val docUUID: UUID?,
    val docTypeName: String,
)
