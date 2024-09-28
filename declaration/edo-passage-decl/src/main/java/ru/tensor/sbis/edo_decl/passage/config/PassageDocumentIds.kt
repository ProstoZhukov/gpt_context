package ru.tensor.sbis.edo_decl.passage.config

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.edo_decl.passage.PassageEvent
import java.util.UUID

/**
 * Идентификаторы документа для перехода
 *
 * @property id                     Уникальный идентификатор документа
 * @property type                   Тип документа, например, "Наряд", "СлужЗап", "FileSD" и т.д
 * @property activeEventId          Идентификатор события
 * @property activeEventCloudId     Облачный идентификатор события
 * @property meta                   Прикладная мета-информация о документе, будет доступна в [PassageEvent]
 *
 * @author sa.nikitin
 */
sealed interface PassageDocIds<T> : Parcelable {
    val id: T
    val activeEventId: T?
    val activeEventCloudId: String?
    val type: String
    val meta: Parcelable?
}

/**
 * Реализация [PassageDocIds] с типом [UUID] для свойств [id] и [activeEventId]
 *
 * @property id UUID документа
 * Следует использовать RpDocument.getDoc().getUuid()
 *
 * @property activeEventId  UUID события
 * Следует использовать ru.tensor.sbis.docflow.generated.EventModel.uuid
 *
 * @property activeEventCloudId  Облачный идентификатор события
 * Следует использовать ru.tensor.sbis.docflow.generated.EventModel.cloudId
 *
 * @author sa.nikitin
 */
@Parcelize
data class PassageDocumentIds(
    override val id: UUID,
    override val type: String,
    override val activeEventId: UUID?,
    override val activeEventCloudId: String? = null,
    override val meta: Parcelable? = null
) : PassageDocIds<UUID> {

    companion object {

        val STUB = PassageDocumentIds(id = UUID(0, 0), type = "", activeEventId = null)
    }
}

/**
 * Реализация [PassageDocIds] с типом [String] для свойств [id] и [activeEventId]
 * Работа с вложениями не будет доступна.
 * Переходы будут осуществляться через статические методы IPassage со строковыми параметрами.
 *
 * @property id             Строковой идентификатор документа
 * @property activeEventId  Строковой идентификатор события
 *
 * @author sa.nikitin
 */
@Parcelize
data class PassageDocumentStrIds(
    override val id: String,
    override val type: String,
    override val activeEventId: String?,
    override val meta: Parcelable? = null
) : PassageDocIds<String> {
    @IgnoredOnParcel
    override val activeEventCloudId: String? = null
}