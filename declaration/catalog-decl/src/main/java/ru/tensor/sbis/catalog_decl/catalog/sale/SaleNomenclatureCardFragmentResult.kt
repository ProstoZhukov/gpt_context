package ru.tensor.sbis.catalog_decl.catalog.sale

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Контракт результата работы окна номенклатуры в продаже.
 *
 * @author mv.ilin
 */
sealed interface SaleNomenclatureCardFragmentResult : Parcelable {

    /**
     *  Номенклатура в продаже нажата кнопка закрыть
     */
    @Parcelize
    object ClickToForceCloseSellingNom : SaleNomenclatureCardFragmentResult

    /**
     *  Номенклатура в продаже нажата кнопка OK
     */
    @Parcelize
    class ClickToCloseSellingNom(val saleNomenclatureUUID: UUID) : SaleNomenclatureCardFragmentResult

    /**
     *  Номенклатура в продаже Нажата кнопка удалить
     */
    @Parcelize
    class ClickToRemoveSellingNom(val saleNomenclatureUUID: UUID) : SaleNomenclatureCardFragmentResult
}