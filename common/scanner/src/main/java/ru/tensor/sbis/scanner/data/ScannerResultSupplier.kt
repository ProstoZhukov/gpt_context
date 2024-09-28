package ru.tensor.sbis.scanner.data

import ru.tensor.sbis.common.util.uri.UriWrapper
import ru.tensor.sbis.edo_decl.scanner.ScannerEventDispatcher
import ru.tensor.sbis.edo_decl.scanner.ScannerResult
import ru.tensor.sbis.scanner.generated.ScannerPageInfo

/**
 * Поставщик результатов сканирования подписчикам [ru.tensor.sbis.edo_decl.scanner.ScannerEventProvider]
 *
 * @author sa.nikitin
 */
internal class ScannerResultSupplier(
        private val requestCode: String,
        private val uriWrapper: UriWrapper,
        private val scannerEventDispatcher: ScannerEventDispatcher
) {

    /**
     * Формирует результат скана из локальной директории
     */
    fun getUrisFromPath(path: String): List<String> {
        val uri = uriWrapper.getStringUriForFilePath(path)
        return listOf(uri)
    }

    /**
     * Формирует результат скана из списка, который содержит модели страниц
     */
    inline fun getUrisFromPageInfoIndexList(indexSize: Int, provider: (Int) -> ScannerPageInfo): List<String> {
        return ArrayList<String>(indexSize).apply {
            for (i in 0 until indexSize) {
                val uri = uriWrapper.getStringUriForFilePath(provider(i).imageCroppedPath)
                add(uri)
            }
        }
    }

    /**
     * Формирует результат скана из модели страницы
     */
    fun getUrisFromPageInfo(pageInfo: ScannerPageInfo): List<String> {
        return getUrisFromPath(pageInfo.imageCroppedPath)
    }

    /**
     * Доставляет результат скана
     */
    fun dispatchResult(uriList: List<String>) {
        scannerEventDispatcher.dispatchScannerResult(ScannerResult(requestCode, uriList))
    }

}