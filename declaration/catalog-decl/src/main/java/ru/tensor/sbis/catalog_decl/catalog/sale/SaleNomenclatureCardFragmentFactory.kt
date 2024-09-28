package ru.tensor.sbis.catalog_decl.catalog.sale

import androidx.fragment.app.Fragment

/**
 * Фабрика дял создания фрагментов с номенклатурой в продаже.
 *
 * @author mv.ilin
 */
fun interface SaleNomenclatureCardFragmentFactory {
    fun create(params: SellingNomenclatureInitParams): Fragment
}