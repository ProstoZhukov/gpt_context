package ru.tensor.sbis.barcode_decl.barcodereader

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**@SelfDocumented*/
enum class BarcodeSymbology {
    CODE_128,
    CODE_39,
    CODE_93,
    CODABAR,
    DATA_MATRIX,
    EAN_13,
    EAN_8,
    ITF,
    QR_CODE,
    UPC_A,
    UPC_E,
    PDF417,
    AZTEC,
    UNKNOWN,
    MANUAL_RECEIPT
}

/**
 * Данные штрих или qr кода
 *
 * @param displayValue String - отсканированные или введенные вручную данные
 * @param barcodeSymbology BarcodeSymbology - тип
 * @param file путь к изображению
 */
@Parcelize
data class Barcode(
    val displayValue: String,
    val barcodeSymbology: BarcodeSymbology,
    var file: String? = null
) : Parcelable {

    companion object {

        /**@SelfDocumented*/
        fun NIL_BARCODE() = Barcode("", BarcodeSymbology.UNKNOWN)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Barcode

        if (displayValue != other.displayValue) return false
        if (barcodeSymbology != other.barcodeSymbology) return false

        return true
    }

    override fun hashCode(): Int {
        var result = displayValue.hashCode()
        result = 31 * result + barcodeSymbology.hashCode()
        return result
    }
}

/**@SelfDocumented*/
fun Barcode.isNil() = this.displayValue.isEmpty() && barcodeSymbology == BarcodeSymbology.UNKNOWN

/**@SelfDocumented*/
fun Barcode.isNotNil() = isNil().not()