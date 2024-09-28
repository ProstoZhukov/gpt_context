package ru.tensor.sbis.storage.contract

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.storage.external.SbisExternalStorage

/**
 * Интерфейс поставщика внешнего хранилища [SbisExternalStorage]
 *
 * @author kv.martyshenko
 */
interface ExternalStorageProvider : Feature {

    /**
     * внешнее хранилище [SbisExternalStorage]
     */
    val externalStorage: SbisExternalStorage

}