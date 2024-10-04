package ru.tensor.sbis.design.view_ext.barcode

import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.updateLayoutParams
import com.google.zxing.BarcodeFormat
import com.google.zxing.BarcodeFormat.QR_CODE
import ru.tensor.sbis.design.view_ext.R
import ru.tensor.sbis.design.view_ext.databinding.DesignViewExtBarcodeViewBinding

/**
 * Установка данных в отображение баркода и его номера
 */
fun DesignViewExtBarcodeViewBinding.setBarcodeInfo(
    barcodeFormat: BarcodeFormat,
    barcodeNumber: String,
    formattedNumber: String
) {
    val resources = root.resources
    val isQrCode = barcodeFormat == QR_CODE

    val barcodeNumberWidth =
        if (isQrCode) WRAP_CONTENT else resources.getDimensionPixelSize(R.dimen.design_view_ext_barcode_width)

    designViewExtBarcodeNumber.apply {
        text = formattedNumber
        updateLayoutParams { width = barcodeNumberWidth }
    }

    val barcodeImageHeight = resources.getDimensionPixelSize(
        if (isQrCode) R.dimen.design_view_ext_barcode_qr_code_size else R.dimen.design_view_ext_barcode_height
    )

    designViewExtBarcodeImage.apply {
        updateLayoutParams { height = barcodeImageHeight }
        setBarcodeImage(barcodeNumber, barcodeFormat, backColor = android.graphics.Color.WHITE)
    }
}