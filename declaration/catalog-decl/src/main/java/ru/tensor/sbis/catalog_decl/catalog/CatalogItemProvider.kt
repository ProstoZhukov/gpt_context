package ru.tensor.sbis.catalog_decl.catalog

import androidx.annotation.WorkerThread
import io.reactivex.Completable
import io.reactivex.Maybe
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 *  Поставщик элементов каталога
 *
 *  @author sp.lomakin
 */
interface CatalogItemProvider : Feature {

    /**@SelfDocumented*/
    @WorkerThread
    fun getItem(uuid: UUID): CatalogItemData?

    /**@see getItem*/
    fun getItemRx(uuid: UUID): Maybe<CatalogItemData>

    /**@SelfDocumented*/
    fun getConditionalNomenclatureUuid(): UUID?

    /**
     *  Разобрать марку и сохранить код в номенклатуру.
     *
     *  [uuid] номенклатура.
     *  [mark] марка будет разобрана, штрихкод добавлен.
     */
    fun writeMark(uuid: UUID, mark: String): Completable

    /**
     * Запустить синхронизацию
     *
     * @return false - если недостаточно прав, иначе true
     */
    fun synchronize(): Boolean
}