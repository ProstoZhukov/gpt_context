package ru.tensor.sbis.crud.sale.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonModel as ControllerRefusalReasonModel

/**
 * Модель причины возврата/удаления
 *
 * @param id Int - идентификатор причины
 * @param name String - имя причины
 * @param type RefusalReasonType - тип причины
 * @param isVisible Boolean - параметр, определяющий видимость
 * @param isDefault Boolean - явлется ли дефолтной
 * @param isWriteOff Boolean - признак "со списанием"
 * @param isDeleted Boolean - явлется ли удаленной
 * @param syncStatus SaleSyncStatus - статус синхронизации
 */
@Parcelize
data class RefusalReason(
    val id: Long,
    val name: String,
    val type: RefusalReasonType,
    val isVisible: Boolean,
    val isDefault: Boolean,
    val isWriteOff: Boolean,
    val isDeleted: Boolean,
    val syncStatus: SaleSyncStatus,
    val isEditing: Boolean = false
) : Parcelable {

    companion object {

        /**@SelfDocumented */
        fun stub(): RefusalReason = RefusalReason(
            0,
            "",
            RefusalReasonType.CANCEL,
            isVisible = false,
            isDefault = false,
            isWriteOff = false,
            isDeleted = false,
            syncStatus = SaleSyncStatus.NOT_REQUIRED
        )

        /**@SelfDocumented */
        fun create(refusalReason: RefusalReason, init: Builder.() -> Unit) = Builder(refusalReason, init).build()

        /**@SelfDocumented */
        class Builder private constructor(val refusalReason: RefusalReason) {

            constructor(refusalReason: RefusalReason, init: Builder.() -> Unit) : this(refusalReason) {
                init()
            }
            /**@SelfDocumented */
            var name: String? = null

            /**@SelfDocumented */
            var type: RefusalReasonType? = null

            /**@SelfDocumented */
            var isWriteOff: Boolean? = null

            /**@SelfDocumented */
            var isVisible: Boolean? = null

            /**@SelfDocumented */
            fun name(init: Builder.() -> String?) = apply { name = init() }

            /**@SelfDocumented */
            fun type(init: Builder.() -> RefusalReasonType?) = apply { type = init() }

            /**@SelfDocumented */
            @SuppressWarnings("unused")
            fun isWriteOff(init: Builder.() -> Boolean?) = apply { isWriteOff = init() }

            /**@SelfDocumented */
            fun isVisible(init: Builder.() -> Boolean?) = apply { isVisible = init() }

            /**@SelfDocumented */
            fun build() = RefusalReason(this)
        }
    }

    private constructor(builder: Builder) : this(
        builder.refusalReason.id,
        builder.name ?: builder.refusalReason.name,
        builder.type ?: builder.refusalReason.type,
        builder.isVisible ?: builder.refusalReason.isVisible,
        builder.refusalReason.isDefault,
        builder.isWriteOff ?: builder.refusalReason.isWriteOff,
        builder.refusalReason.isDeleted,
        builder.refusalReason.syncStatus
    )
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerRefusalReasonModel.map(): RefusalReason = RefusalReason(
    id, name, type.map(), isVisible, isDefault, isWriteOff, isDeleted, syncStatus.map()
)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun RefusalReason.map(): ControllerRefusalReasonModel = ControllerRefusalReasonModel(
    id, type.map(), 0, name, isVisible, isDefault, isWriteOff, isDeleted, syncStatus.map()
)
