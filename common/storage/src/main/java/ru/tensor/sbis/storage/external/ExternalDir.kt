package ru.tensor.sbis.storage.external

import java.io.File

/**
 * Класс, представляющий внешнюю директорию
 *
 * @property dir
 * @property status состояние директории [ExternalDirStatus]
 *
 * @author sa.nikitin
 */
class ExternalDir(val dir: File, val status: ExternalDirStatus)