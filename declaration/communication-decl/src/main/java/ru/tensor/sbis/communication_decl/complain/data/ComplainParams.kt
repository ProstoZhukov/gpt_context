package ru.tensor.sbis.communication_decl.complain.data

import java.io.Serializable
import java.util.UUID

/**
 * Дата класс параметров для жалобы.
 * @param entityType тип сущности, на которую заводят жалобу.
 * @param entityUUID uuid сущности, на которую заводят жалобу.
 * @param entityParentType тип родительской сущности. Указание обязательно для:
 * - обсуждение группы социальной сети
 * - комментарий/сообщение
 * - отзыв на заведение sabyget
 * @param entityParentUuid uuid родительской сущности.Указание обязательно для:
 * - обсуждение группы социальной сети
 * - комментарий/сообщение
 * - отзыв на заведение sabyget
 * @param comment комментарий к жалобе.
 * @param reason причина жалобы (спам, агрессия и пр.).
 * @param additionalData дополнительный набор данных в виде JSON.
 *
 * @author da.zhukov
 */
data class ComplainParams(
    val entityType: ComplainEntityType,
    val entityUUID: UUID,
    val entityParentType: ComplainEntityType? = null,
    val entityParentUuid: UUID? = null,
    val comment: String? = null,
    val reason: ComplainReasonType = ComplainReasonType.SPAM,
    val additionalData: String? = null
) : Serializable