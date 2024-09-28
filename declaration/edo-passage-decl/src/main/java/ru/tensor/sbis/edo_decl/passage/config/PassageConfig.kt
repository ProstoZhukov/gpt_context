package ru.tensor.sbis.edo_decl.passage.config

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.attachments.models.action.AttachmentActionType
import ru.tensor.sbis.attachments.models.action.defaultViewerAllowedActions
import ru.tensor.sbis.edo_decl.passage.data.PassageDataProvider
import ru.tensor.sbis.mobile.docflow.generated.IPassage
import java.util.EnumSet
import java.util.UUID

/**
 * Конфигурация компонента переходов
 *
 * @property iPassageFactory            Фабрика [IPassage]
 *                                      ВНИМАНИЕ! Если передан null, то оффлайн переходы не будут работать,
 *                                      т.к. статическое выполнение доступно только без БД
 *
 * @property ids                        Идентификаторы документа для перехода по нему
 *
 * @property dataProvider               Экземпляр [PassageDataProvider]
 *
 * @property additionalFieldsAreaId     Идентификатор области доп полей,
 *                                      которая отображается на карточке и должна проверяться во время переходов в ДЗЗ
 *
 * @property attachmentAllowedActions   Перечисление доступных действий над вложениями
 *
 * @author sa.nikitin
 */
@Parcelize
class PassageConfig private constructor(
    val iPassageFactory: ControllerFactory<IPassage>?,
    val ids: PassageDocIds<*>,
    val dataProvider: PassageDataProvider,
    val additionalFieldsAreaId: UUID?,
    val attachmentAllowedActions: EnumSet<AttachmentActionType>
) : Parcelable {

    constructor(
        ids: PassageDocumentStrIds,
        dataProvider: PassageDataProvider,
        attachmentAllowedActions: EnumSet<AttachmentActionType> =
            defaultPassageAttachmentAllowedActions
    ) : this(
        iPassageFactory = null,
        ids = ids,
        dataProvider = dataProvider,
        additionalFieldsAreaId = null,
        attachmentAllowedActions = attachmentAllowedActions
    )

    constructor(
        iPassageFactory: ControllerFactory<IPassage>?,
        ids: PassageDocumentIds,
        dataProvider: PassageDataProvider,
        additionalFieldsAreaId: UUID? = null,
        attachmentAllowedActions: EnumSet<AttachmentActionType> =
            defaultPassageAttachmentAllowedActions
    ) : this(
        iPassageFactory = iPassageFactory,
        ids = ids as PassageDocIds<*>,
        dataProvider = dataProvider,
        additionalFieldsAreaId = additionalFieldsAreaId,
        attachmentAllowedActions = attachmentAllowedActions
    )

    @Deprecated("Используйте первичный конструктор")
    constructor(
        di: PassageDI?,
        ids: PassageDocumentIds,
        dataProvider: PassageDataProvider,
        uiConfig: PassageUiConfig = PassageUiConfig(),
        additionalFieldsAreaId: UUID? = null,
        attachmentAllowedActions: EnumSet<AttachmentActionType> =
            defaultPassageAttachmentAllowedActions
    ) : this(
        iPassageFactory = di?.let(PassageDI::IPassageFactory),
        ids = ids,
        dataProvider = dataProvider,
        additionalFieldsAreaId = additionalFieldsAreaId,
        attachmentAllowedActions = attachmentAllowedActions
    )

    @Deprecated("Используйте первичный конструктор")
    constructor(
        ids: PassageDocumentIds,
        dataProvider: PassageDataProvider,
        uiConfig: PassageUiConfig = PassageUiConfig(),
        additionalFieldsAreaId: UUID? = null,
        attachmentAllowedActions: EnumSet<AttachmentActionType> =
            defaultPassageAttachmentAllowedActions
    ) : this(
        di = null,
        ids = ids,
        dataProvider = dataProvider,
        uiConfig = uiConfig,
        additionalFieldsAreaId = additionalFieldsAreaId,
        attachmentAllowedActions = attachmentAllowedActions
    )
}

/** @SelfDocumented */
val defaultPassageAttachmentAllowedActions: EnumSet<AttachmentActionType> = EnumSet.copyOf(
    defaultViewerAllowedActions + AttachmentActionType.DELETE - AttachmentActionType.SIGN
)