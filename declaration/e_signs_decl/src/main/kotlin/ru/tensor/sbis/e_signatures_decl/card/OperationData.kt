package ru.tensor.sbis.e_signatures_decl.card

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Данные операции, которая доступна к выполнению на экране.
 * @property id Идентификатор операции.
 * @property isLaunchRequired True, если операцию надо начать выполнять сразу после запуска экрана.
 *
 * @author vv.malyhin
 */
sealed class OperationData : Parcelable {

    abstract val id: String?
    abstract val isLaunchRequired: Boolean

    /**
     * Данные для копирования сертификата.
     * @property type Тип операции (импорт / экспорт).
     * @property cloudPfxId Идентификатор PFX.
     * @property isCarrier True, если текущее устройство содержит сертификат.
     * @property deviceId Идентификатор текущего устройства.
     * @property remoteDeviceId Идентификатор удаленного устройства.
     *
     * @author vv.malyhin
     */
    @Parcelize
    data class Copying(
        override val id: String? = null,
        override val isLaunchRequired: Boolean = false,
        val type: Type? = null,
        val cloudPfxId: String? = null,
        val isCarrier: Boolean = false,
        val deviceId: String? = null,
        val remoteDeviceId: String? = null,
    ) : OperationData() {

        companion object {
            /** Вспомогательная функция для преобразования сырых Int-данных к типу операции. */
            fun Int?.toType() =
                when (this) {
                    1 -> Type.IMPORT
                    2 -> Type.EXPORT
                    else -> null
                }

            /** Вспомогательная функция для преобразования сырых Boolean-данных к типу операции. */
            fun Boolean?.toType() =
                when (this) {
                    true -> Type.IMPORT
                    false -> Type.EXPORT
                    else -> null
                }
        }

        /**
         * Тип операции: импорт, экспорт сертификата.
         *
         * @author vv.malyhin
         */
        enum class Type {
            IMPORT, EXPORT
        }
    }
}