package ru.tensor.sbis.catalog_decl.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

/**@SelfDocumented */
private const val QUANTITY_SCALE = 3

/**
 * С данной точностью корректно конвертируется упаковка с максимальным кол-вом по одной позиции (99 999)
 */
private const val PACK_CONVERSION_SCALE = 10

/**
 *  Модель упаковка номенклатуры
 *
 *  @author sp.lomakin
 */
@Parcelize
data class Packs(
    val base: Pack?,
    val pack: Pack?,
    val qty: Double? = null,
    val id: String? = null,
    val isBase: Boolean = false
) : Parcelable, Serializable {

    companion object {

        /**
         * Метод для сравнения [Packs] с игнорированием [Pack.localizedName], [Pack.localizedAbbr]
         * для [Packs.base] и [Packs.pack].
         *
         * @see [Pack.contentEquals]
         */
        fun Packs.contentEquals(other: Packs): Boolean {
            if (this === other) return true

            if (!base.contentEquals(other.base)) return false
            if (!pack.contentEquals(other.pack)) return false
            if (id != other.id) return false
            if (qty != other.qty) return false

            return true
        }

        /**
         * Метод игнорирующий поля [Pack.localizedName], [Pack.localizedAbbr] при сравнении упаковок.
         *
         * @param other [Pack]
         * @param skipQty Пропустить проверку кол-ва при сравнении
         */
        fun Pack?.contentEquals(other: Pack?, skipQty: Boolean = false): Boolean {
            if (this == null && other == null) return true
            if (this == null || other == null) return false
            if (this === other) return true

            if (originalName != other.originalName) return false
            if (qty != other.qty && !skipQty) return false
            if (originalAbbr != other.originalAbbr) return false
            if (code != other.code) return false
            if (id != other.id) return false
            if (denominator != other.denominator) return false

            return true
        }

        /**
         * Конвертирует количество, соответствующее ед. измерения переданной упаковки
         */
        fun Packs?.convertQuantity(quantity: BigDecimal, toPack: Packs?): BigDecimal =
            quantity.divide(this?.base?.qty?.toBigDecimal() ?: BigDecimal.ONE, RoundingMode.HALF_UP)
                .multiply(
                    (toPack?.base?.qty?.toBigDecimal() ?: BigDecimal.ONE)
                        .divide(
                            toPack?.pack?.qty?.toBigDecimal() ?: BigDecimal.ONE,
                            PACK_CONVERSION_SCALE,
                            RoundingMode.HALF_UP
                        )
                )
                .setScale(QUANTITY_SCALE, RoundingMode.HALF_UP)
    }

    @Parcelize
    data class Pack(
            val originalName: String?,
            val localizedName: String?,
            val qty: Double?,
            val originalAbbr: String?,
            val localizedAbbr: String?,
            val code: String?,
            val id: Long? = null,
            val denominator: Double? = null
    ) : Parcelable, Serializable {

        val name: String?
            get() = localizedName ?: originalName

        val abbr: String?
            get() = localizedAbbr ?: originalAbbr
    }
}