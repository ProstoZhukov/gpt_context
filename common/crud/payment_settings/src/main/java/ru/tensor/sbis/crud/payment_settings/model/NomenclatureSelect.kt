package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.retail_settings.generated.NomenclatureSelect as ControllerNomenclatureSelect

/**
 * Перечисление откуда разрешено выбирать номенклатуры.
 */
enum class NomenclatureSelect {
    /** Прайслисты и каталог. */
    PRICE_AND_CATALOG,

    /** Только прайслисты. */
    PRICE,

    /** @SelfDocumented */
    WRITE_OFF
}

/**
 * Маппер для преобразования модели контроллера во вью модель.
 */
fun ControllerNomenclatureSelect.map(): NomenclatureSelect =
    when (this) {
        ControllerNomenclatureSelect.CATALOG -> NomenclatureSelect.PRICE_AND_CATALOG
        ControllerNomenclatureSelect.PRICE -> NomenclatureSelect.PRICE
        ControllerNomenclatureSelect.WRITEOFF -> NomenclatureSelect.WRITE_OFF
    }

/**
 * Маппер для преобразования вью модели в модель контроллера.
 */
fun NomenclatureSelect.map(): ControllerNomenclatureSelect =
    when (this) {
        NomenclatureSelect.PRICE_AND_CATALOG -> ControllerNomenclatureSelect.CATALOG
        NomenclatureSelect.PRICE -> ControllerNomenclatureSelect.PRICE
        NomenclatureSelect.WRITE_OFF -> ControllerNomenclatureSelect.WRITEOFF
    }