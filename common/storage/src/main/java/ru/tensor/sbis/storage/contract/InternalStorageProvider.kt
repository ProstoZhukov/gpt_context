package ru.tensor.sbis.storage.contract

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.storage.internal.SbisInternalStorage

/**
 * Интерфейс поставщика внутреннего хранилища [SbisInternalStorage]
 *
 * @author kv.martyshenko
 */
interface InternalStorageProvider : Feature {

    /**
     * внутреннее хранилище [SbisInternalStorage]
     */
    val internalStorage: SbisInternalStorage

}