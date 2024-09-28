package ru.tensor.sbis.business_tools_decl.contractors

import androidx.annotation.WorkerThread
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фича предоставляет API для (добавления в избранное/удаления из избранного) контрагентов.
 *
 * @author av.efimov1
 */
interface ContractorFavoriteFeature : Feature {

    /**
     * Добавляет/удаляет контрагента в разделе "Избранное".
     *
     * @param sppId идентификатор СПП контрагента.
     * @param isFavorite если true - контрагент будет добавлен в избранное, иначе - удален из избранного.
     *
     * @return вернет true если метод отработал успешно.
     */
    @WorkerThread
    fun setFavorite(sppId: Int, isFavorite: Boolean): Boolean

    /**
     * Првоерить, находится ли контрагент в избранном, или нет.
     *
     * @param sppId идентификатор СПП контрагента.
     *
     * @return вернет true - если контргент находится в "Избранном", иначе false.
     */
    @WorkerThread
    fun isFavorite(sppId: Int): Boolean
}