package ru.tensor.sbis.version_checker_decl

/**
 * Поставщик состояния актуальна ли версии приложения.
 *
 * @author mv.ilin
 */
interface IsActualVersionProvider {

    /**
     * Проверить приложение на актуальность версии.
     * @return true если версия приложения актуально, иначе false.
     */
    fun isActualVersion(): Boolean

}