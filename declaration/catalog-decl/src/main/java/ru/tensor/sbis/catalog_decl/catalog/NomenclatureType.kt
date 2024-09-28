package ru.tensor.sbis.catalog_decl.catalog

import androidx.annotation.StringRes
import ru.tensor.sbis.catalog_decl.R

/**
 * Тип номенклатуры. Строится на основе подвида учета [SubAccounting] и группы маркированной продукции [MarkedProductionGroup].
 * @property titleRes строковый ресурс названия типа
 */
enum class NomenclatureType(@StringRes val titleRes: Int) {

    /** Простой, не маркированный товар */
    MERCHANDISE(R.string.catalog_decl_nomenclature_type_merchandise),

    /** Маркированный товар без определённого типа */
    MARKED(R.string.catalog_decl_nomenclature_type_labeled),

    TOBACCO_CONSUMER(R.string.catalog_decl_nomenclature_type_tobacco),
    TOBACCO_BLOCK(R.string.catalog_decl_nomenclature_type_tobacco),
    TOBACCO_OLD(R.string.catalog_decl_nomenclature_type_tobacco),
    TOBACCO_EXPERIMENTAL(R.string.catalog_decl_nomenclature_type_tobacco),
    TOBACCO_TRANSPORT(R.string.catalog_decl_nomenclature_type_tobacco),

    NICOTINE_CONTAINING_CONSUMER(R.string.catalog_decl_nomenclature_type_nicotine),
    NICOTINE_CONTAINING_GROUP(R.string.catalog_decl_nomenclature_type_nicotine),
    NICOTINE_CONTAINING_GROUP_TRANSPORT(R.string.catalog_decl_nomenclature_type_nicotine),

    ALCOHOL(R.string.catalog_decl_nomenclature_type_alcohol),
    MEDICATIONS(R.string.catalog_decl_nomenclature_type_medications),
    SHOES(R.string.catalog_decl_nomenclature_type_shoes),
    TEXTILE(R.string.catalog_decl_nomenclature_type_textile),
    TIRES(R.string.catalog_decl_nomenclature_type_tires),
    PERFUME(R.string.catalog_decl_nomenclature_type_perfume),
    CAMERA(R.string.catalog_decl_nomenclature_type_camera),
    JEWELLERY(R.string.catalog_decl_nomenclature_type_jewellery),

    BICYCLE(R.string.catalog_decl_nomenclature_type_bicycle),
    BICYCLE_TRANSPORT(R.string.catalog_decl_nomenclature_type_bicycle),

    MEDICAL_DEVICES(R.string.catalog_decl_nomenclature_type_medical_devices),
    MEDICAL_DEVICES_TRANSPORT(R.string.catalog_decl_nomenclature_type_medical_devices),

    TOBACCO_ALTERNATE(R.string.catalog_decl_nomenclature_type_tobacco),

    MILK(R.string.catalog_decl_nomenclature_type_milk),
    MILK_BLOCK(R.string.catalog_decl_nomenclature_type_milk),
    MILK_TRANSPORT(R.string.catalog_decl_nomenclature_type_milk),

    WATER(R.string.catalog_decl_nomenclature_type_water),
    WATER_GROUP(R.string.catalog_decl_nomenclature_type_water),
    WATER_TRANSPORT(R.string.catalog_decl_nomenclature_type_water),

    NON_ALCO_BEER(R.string.catalog_decl_nomenclature_type_beer_non_alco),
    BEER(R.string.catalog_decl_nomenclature_type_beer),

    BAA_CONSUMER(R.string.catalog_decl_nomenclature_type_baa),
    BAA_GROUP(R.string.catalog_decl_nomenclature_type_baa),
    BAA_TRANSPORT(R.string.catalog_decl_nomenclature_type_baa),


    ANTISEPTIC(R.string.catalog_decl_nomenclature_type_antiseptic),
    ANTISEPTIC_GROUP(R.string.catalog_decl_nomenclature_type_antiseptic),
    ANTISEPTIC_TRANSPORT(R.string.catalog_decl_nomenclature_type_antiseptic),

    FURS(R.string.catalog_decl_nomenclature_type_furs),

    CAVIAR(R.string.catalog_decl_nomenclature_type_seafood),

    TOBACCO_NICOTINE_RAW(R.string.catalog_decl_nomenclature_type_tobacco_nicotine_raw),

    CONSERVE(R.string.catalog_decl_nomenclature_type_conserve),

    PETFOOD(R.string.catalog_decl_nomenclature_type_petfood),

    VEGETABLEOIL(R.string.catalog_decl_nomenclature_type_vegetable_oil),

    VETPHARMA(R.string.catalog_decl_nomenclature_type_vet_pharme),

    SOFT_DRINKS(R.string.catalog_decl_nomenclature_type_soft_drinks),
    SOFT_DRINKS_GROUP(R.string.catalog_decl_nomenclature_type_soft_drinks),
    SOFT_DRINKS_KIT(R.string.catalog_decl_nomenclature_type_soft_drinks),
    SOFT_DRINKS_TRANSPORT(R.string.catalog_decl_nomenclature_type_soft_drinks),

    OPTICAL_FIBER(R.string.catalog_decl_nomenclature_type_optical_fiber),

    CABLE_PRODUCTION(R.string.catalog_decl_nomenclature_type_cable_production),
    HEATING_DEVICES(R.string.catalog_decl_nomenclature_type_heating_devices),
    RADIO_ELECTRONICS(R.string.catalog_decl_nomenclature_type_radio_electronics),

    UNDEFINED(R.string.catalog_decl_nomenclature_type_undefined);
}

/**
 * Метод проверяет, является ли товар табаком.
 * Альтернативный табак табаком не является!
 */
fun NomenclatureType?.isTobacco(): Boolean {
    return this in setOf(
        NomenclatureType.TOBACCO_CONSUMER,
        NomenclatureType.TOBACCO_BLOCK,
        NomenclatureType.TOBACCO_OLD,
        NomenclatureType.TOBACCO_EXPERIMENTAL,
        NomenclatureType.TOBACCO_TRANSPORT
    )
}

/** Является ли товар никотиносодержащей продукцией? */
fun NomenclatureType?.isNicotine(): Boolean {
    return this in setOf(
        NomenclatureType.NICOTINE_CONTAINING_CONSUMER,
        NomenclatureType.NICOTINE_CONTAINING_GROUP,
        NomenclatureType.NICOTINE_CONTAINING_GROUP_TRANSPORT
    )
}

/** Является ли товар лекарством? */
fun NomenclatureType?.isMedications(): Boolean {
    return this == NomenclatureType.MEDICATIONS
}

/** Является ли товар духами? */
fun NomenclatureType?.isPerfume(): Boolean {
    return this == NomenclatureType.PERFUME
}

/** Является ли товар водой? */
fun NomenclatureType?.isWater(): Boolean {
    return this in setOf(
        NomenclatureType.WATER,
        NomenclatureType.WATER_GROUP,
        NomenclatureType.WATER_TRANSPORT
    )
}

/** Является ли товар медицинскими изделяими? */
fun NomenclatureType?.isMedicalDevices(): Boolean {
    return this == NomenclatureType.MEDICAL_DEVICES
}

/** Является ли товар алкоголем? */
fun NomenclatureType?.isAlcohol(): Boolean {
    return this == NomenclatureType.ALCOHOL
}

/** Является ли товар молочной продукцией? */
fun NomenclatureType?.isMilk(): Boolean {
    return this in setOf(
        NomenclatureType.MILK,
        NomenclatureType.MILK_BLOCK,
        NomenclatureType.MILK_TRANSPORT
    )
}

/** Является ли товар антисептиком? */
fun NomenclatureType?.isAntiseptic(): Boolean {
    return this in setOf(
        NomenclatureType.ANTISEPTIC,
        NomenclatureType.ANTISEPTIC_GROUP,
        NomenclatureType.ANTISEPTIC_TRANSPORT
    )
}

/** Является ли товар сокосодержащей продукцией? */
fun NomenclatureType?.isSoftDrink(): Boolean {
    return this in setOf(
        NomenclatureType.SOFT_DRINKS,
        NomenclatureType.SOFT_DRINKS_GROUP,
        NomenclatureType.SOFT_DRINKS_KIT,
        NomenclatureType.SOFT_DRINKS_TRANSPORT,
    )
}

/** Является ли товар БАДом? */
fun NomenclatureType?.isBAA(): Boolean {
    return this in setOf(
        NomenclatureType.BAA_CONSUMER,
        NomenclatureType.BAA_TRANSPORT,
        NomenclatureType.BAA_GROUP
    )
}

/** Является ли товар пивом? */
fun NomenclatureType?.isBeer(): Boolean {
    return this in setOf(
        NomenclatureType.BEER,
        NomenclatureType.NON_ALCO_BEER,
    )
}

/**
 * Маркированные товары не выделенные в отдельную категорию
 */
fun isUncategorizedMarkedType(nomenclatureType: NomenclatureType) =
    nomenclatureType in setOf(
        NomenclatureType.SHOES,
        NomenclatureType.TEXTILE,
        NomenclatureType.TIRES,
        NomenclatureType.PERFUME,
        NomenclatureType.CAMERA,
        NomenclatureType.BICYCLE,
        NomenclatureType.MEDICAL_DEVICES,
        NomenclatureType.WATER,
        NomenclatureType.FURS,
        NomenclatureType.CAVIAR
    )

/** Проверяет, является ли тип номенклатуры некорректным (т.е. распознать тип номенклатуры не удалось) */
fun NomenclatureType?.isIncorrect(): Boolean {
    return this == null || this == NomenclatureType.UNDEFINED
}

/** Соответствует ли тип номенклатуры маркированному товару? */
fun NomenclatureType?.isMarked() = !isIncorrect() && this != NomenclatureType.MERCHANDISE
        && !isSubAccountingWithSn(mapToSubAccounting())


/** Соответствует ли тип номенклатуры маркированному товару без конкретной товарной группы? */
fun NomenclatureType?.isMarkedWithoutMpGroup() = this == NomenclatureType.MARKED

/** Узнать подвид учета через тип номенклатуры */
fun NomenclatureType?.mapToSubAccounting() = when(this) {
    NomenclatureType.ALCOHOL -> SubAccounting.ALCOHOLIC
    NomenclatureType.MEDICATIONS -> SubAccounting.DRUG
    NomenclatureType.JEWELLERY -> SubAccounting.JEWELLERY
    NomenclatureType.TOBACCO_BLOCK -> SubAccounting.TOBACCO
    NomenclatureType.TOBACCO_CONSUMER -> SubAccounting.TOBACCO
    NomenclatureType.MILK -> SubAccounting.MARKED_VETERINARY
    NomenclatureType.NICOTINE_CONTAINING_CONSUMER,
    NomenclatureType.SHOES,
    NomenclatureType.TEXTILE,
    NomenclatureType.TIRES,
    NomenclatureType.PERFUME,
    NomenclatureType.CAMERA,
    NomenclatureType.BICYCLE,
    NomenclatureType.MEDICAL_DEVICES,
    NomenclatureType.WATER,
    NomenclatureType.TOBACCO_ALTERNATE,
    NomenclatureType.BEER,
    NomenclatureType.BAA_CONSUMER,
    NomenclatureType.ANTISEPTIC,
    NomenclatureType.FURS,
    NomenclatureType.CAVIAR,
    NomenclatureType.SOFT_DRINKS,
    NomenclatureType.MARKED -> SubAccounting.MARKED
    NomenclatureType.UNDEFINED -> SubAccounting.INVALID
    else -> null
}
