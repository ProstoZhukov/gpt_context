package ru.tensor.sbis.consignment_decl.registry.model

import java.util.Date
import java.util.UUID

/**
 * ЭТРН-элемент для реестра.
 *
 * @property uuid идентификатор.
 * @property date дата.
 * @property faceName имя исполнителя.
 * @property список элементов груза.
 * @property есть ли еще элементы груза.
 * @property docInfo информация по документу.
 * @property isRead прочитан или нет.
 *
 * @author kv.martyshenko
 */
data class ConsignmentRegistryElement(
    val uuid: UUID,
    var date: Date,
    var faceName: String,
    var cargoList: List<String>,
    var cargoListHasMore: Boolean,
    var docInfo: DocInfo,
    var isRead: Boolean
)