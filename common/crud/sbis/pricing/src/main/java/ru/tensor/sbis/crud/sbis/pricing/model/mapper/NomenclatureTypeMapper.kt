package ru.tensor.sbis.crud.sbis.pricing.model.mapper

import ru.tensor.sbis.catalog_decl.catalog.MarkedProductionGroup
import ru.tensor.sbis.catalog_decl.catalog.NomenclatureType
import ru.tensor.sbis.catalog_decl.catalog.SubAccounting
import ru.tensor.sbis.catalog.generated.MarkedProductionGroup as ControllerMarkedProductionGroup
import ru.tensor.sbis.catalog.generated.SubAccountingType as ControllerSubAccountingType

/**
 * Определение маркированных товаров по подвиду учёта
 */
fun ControllerSubAccountingType?.isMarked() =
    this == ControllerSubAccountingType.MARKED
            || this == ControllerSubAccountingType.DRUG
            || this == ControllerSubAccountingType.MARKED_VETERINARY
            || this == ControllerSubAccountingType.ALCO

/**
 * Получение типа номенклатуры на основе [ControllerSubAccountingType] и [ControllerMarkedProductionGroup]
 * @param type см. [ControllerSubAccountingType]
 * @param mpGroup см. [ControllerMarkedProductionGroup]
 * @param withBlockPackage если true - то блок табака, если false - то пачка
 * @return см. [NomenclatureType]
 */
fun toNomenclatureType(
    type: ControllerSubAccountingType?,
    mpGroup: ControllerMarkedProductionGroup?,
    withBlockPackage: Boolean = false
): NomenclatureType {
    return toNomenclatureType(type?.map(), mpGroup?.map(), withBlockPackage)
}

/**
 * Получение типа номенклатуры на основе [SubAccounting] и [MarkedProductionGroup]
 * @param type см. [SubAccounting]
 * @param mpGroup см. [MarkedProductionGroup]
 * @param withBlockPackage если true - то блок табака, если false - то пачка
 * @return см. [NomenclatureType]
 */
fun toNomenclatureType(
    type: SubAccounting?,
    mpGroup: MarkedProductionGroup?,
    withBlockPackage: Boolean = false
): NomenclatureType {
    return when (type) {
        SubAccounting.ALCOHOLIC -> if (mpGroup == MarkedProductionGroup.BEER) NomenclatureType.BEER else NomenclatureType.ALCOHOL
        SubAccounting.DRUG -> NomenclatureType.MEDICATIONS
        SubAccounting.JEWELLERY -> NomenclatureType.JEWELLERY
        SubAccounting.TOBACCO -> if (withBlockPackage) NomenclatureType.TOBACCO_BLOCK else NomenclatureType.TOBACCO_CONSUMER
        SubAccounting.MARKED_VETERINARY -> NomenclatureType.MILK
        SubAccounting.MARKED -> mpGroup.toNomenclatureType(withBlockPackage)
        SubAccounting.VETERINARY,
        SubAccounting.EXCISE,
        SubAccounting.TRACEABLE_PRODUCTS,
        SubAccounting.GRAIN,
        null -> NomenclatureType.MERCHANDISE
        SubAccounting.INVALID -> NomenclatureType.UNDEFINED
    }
}

private fun MarkedProductionGroup?.toNomenclatureType(withBlockPackage: Boolean): NomenclatureType {
    return when (this) {
        MarkedProductionGroup.TABACCO -> if (withBlockPackage) NomenclatureType.TOBACCO_BLOCK else NomenclatureType.TOBACCO_CONSUMER
        MarkedProductionGroup.NICOTINE -> if (withBlockPackage) NomenclatureType.NICOTINE_CONTAINING_GROUP else NomenclatureType.NICOTINE_CONTAINING_CONSUMER
        MarkedProductionGroup.SHOES -> NomenclatureType.SHOES
        MarkedProductionGroup.TEXTILE -> NomenclatureType.TEXTILE
        MarkedProductionGroup.TIRES -> NomenclatureType.TIRES
        MarkedProductionGroup.DRUG -> NomenclatureType.MEDICATIONS
        MarkedProductionGroup.PERFUME -> NomenclatureType.PERFUME
        MarkedProductionGroup.CAMERA -> NomenclatureType.CAMERA
        MarkedProductionGroup.BICYCLE -> NomenclatureType.BICYCLE
        MarkedProductionGroup.MEDICAL_DEVICES -> NomenclatureType.MEDICAL_DEVICES
        MarkedProductionGroup.MILK -> NomenclatureType.MILK
        MarkedProductionGroup.WATER -> NomenclatureType.WATER
        MarkedProductionGroup.TOBACCO_ALT -> NomenclatureType.TOBACCO_ALTERNATE
        MarkedProductionGroup.BEER -> NomenclatureType.BEER
        MarkedProductionGroup.BAA -> NomenclatureType.BAA_CONSUMER
        MarkedProductionGroup.ANTISEPTIC -> NomenclatureType.ANTISEPTIC
        null -> NomenclatureType.MARKED
        MarkedProductionGroup.MAX_ONLINE_VALUE,
        MarkedProductionGroup.INVALID -> NomenclatureType.UNDEFINED
        MarkedProductionGroup.NON_ALCO_BEER -> NomenclatureType.NON_ALCO_BEER
        MarkedProductionGroup.FURS -> NomenclatureType.FURS
        MarkedProductionGroup.SOFT_DRINKS -> NomenclatureType.SOFT_DRINKS
        MarkedProductionGroup.CAVIAR -> NomenclatureType.CAVIAR
        MarkedProductionGroup.TOBACCO_NICOTINE_RAW -> NomenclatureType.TOBACCO_NICOTINE_RAW
        MarkedProductionGroup.CONSERVE -> NomenclatureType.CONSERVE
        MarkedProductionGroup.PETFOOD -> NomenclatureType.PETFOOD
        MarkedProductionGroup.VEGETABLEOIL -> NomenclatureType.VEGETABLEOIL
        MarkedProductionGroup.VETPHARMA -> NomenclatureType.VETPHARMA
        MarkedProductionGroup.OPTICAL_FIBER -> NomenclatureType.OPTICAL_FIBER
        MarkedProductionGroup.CABLE_PRODUCTION -> NomenclatureType.CABLE_PRODUCTION
        MarkedProductionGroup.HEATING_DEVICES -> NomenclatureType.HEATING_DEVICES
        MarkedProductionGroup.RADIO_ELECTRONICS -> NomenclatureType.RADIO_ELECTRONICS
    }
}

/**
 * Получение подвида учета и группы маркированной продукции на основе [NomenclatureType]
 * @param nomenclatureType см. [NomenclatureType]
 * @return см. [SubAccounting] и [MarkedProductionGroup]
 */
fun fromNomenclatureType(nomenclatureType: NomenclatureType): Pair<ControllerSubAccountingType?, ControllerMarkedProductionGroup?> {
    val subAccountingType = when (nomenclatureType) {
        NomenclatureType.ALCOHOL,
        NomenclatureType.BEER -> ControllerSubAccountingType.ALCO
        NomenclatureType.MEDICATIONS -> ControllerSubAccountingType.DRUG
        NomenclatureType.JEWELLERY -> ControllerSubAccountingType.JEWELLERY
        NomenclatureType.MARKED,
        NomenclatureType.TOBACCO_CONSUMER,
        NomenclatureType.TOBACCO_BLOCK,
        NomenclatureType.TOBACCO_OLD,
        NomenclatureType.TOBACCO_EXPERIMENTAL,
        NomenclatureType.TOBACCO_TRANSPORT,
        NomenclatureType.TOBACCO_NICOTINE_RAW,
        NomenclatureType.NICOTINE_CONTAINING_CONSUMER,
        NomenclatureType.NICOTINE_CONTAINING_GROUP,
        NomenclatureType.NICOTINE_CONTAINING_GROUP_TRANSPORT,
        NomenclatureType.SHOES,
        NomenclatureType.TEXTILE,
        NomenclatureType.TIRES,
        NomenclatureType.PERFUME,
        NomenclatureType.CAMERA,
        NomenclatureType.BICYCLE,
        NomenclatureType.BICYCLE_TRANSPORT,
        NomenclatureType.MEDICAL_DEVICES,
        NomenclatureType.MEDICAL_DEVICES_TRANSPORT,
        NomenclatureType.MILK,
        NomenclatureType.MILK_BLOCK,
        NomenclatureType.MILK_TRANSPORT,
        NomenclatureType.TOBACCO_ALTERNATE,
        NomenclatureType.WATER,
        NomenclatureType.WATER_TRANSPORT,
        NomenclatureType.WATER_GROUP,
        NomenclatureType.BAA_CONSUMER,
        NomenclatureType.BAA_GROUP,
        NomenclatureType.BAA_TRANSPORT,
        NomenclatureType.ANTISEPTIC,
        NomenclatureType.ANTISEPTIC_GROUP,
        NomenclatureType.NON_ALCO_BEER,
        NomenclatureType.FURS,
        NomenclatureType.CAVIAR,
        NomenclatureType.CONSERVE,
        NomenclatureType.PETFOOD,
        NomenclatureType.VEGETABLEOIL,
        NomenclatureType.VETPHARMA,
        NomenclatureType.OPTICAL_FIBER,
        NomenclatureType.SOFT_DRINKS,
        NomenclatureType.SOFT_DRINKS_GROUP,
        NomenclatureType.SOFT_DRINKS_KIT,
        NomenclatureType.SOFT_DRINKS_TRANSPORT,
        NomenclatureType.CABLE_PRODUCTION,
        NomenclatureType.HEATING_DEVICES,
        NomenclatureType.RADIO_ELECTRONICS,
        NomenclatureType.ANTISEPTIC_TRANSPORT -> ControllerSubAccountingType.MARKED
        NomenclatureType.MERCHANDISE,
        NomenclatureType.UNDEFINED -> null
    }
    val markedProductionGroup = when (nomenclatureType) {
        NomenclatureType.TOBACCO_ALTERNATE -> ControllerMarkedProductionGroup.TOBACCO_ALT
        NomenclatureType.TOBACCO_CONSUMER,
        NomenclatureType.TOBACCO_BLOCK,
        NomenclatureType.TOBACCO_OLD,
        NomenclatureType.TOBACCO_EXPERIMENTAL,
        NomenclatureType.TOBACCO_TRANSPORT -> ControllerMarkedProductionGroup.TABACCO
        NomenclatureType.TOBACCO_NICOTINE_RAW -> ControllerMarkedProductionGroup.TOBACCO_NICOTINE_RAW
        NomenclatureType.NICOTINE_CONTAINING_CONSUMER,
        NomenclatureType.NICOTINE_CONTAINING_GROUP,
        NomenclatureType.NICOTINE_CONTAINING_GROUP_TRANSPORT -> ControllerMarkedProductionGroup.NICOTINE
        NomenclatureType.SHOES -> ControllerMarkedProductionGroup.SHOES
        NomenclatureType.TEXTILE -> ControllerMarkedProductionGroup.TEXTILE
        NomenclatureType.TIRES -> ControllerMarkedProductionGroup.TIRES
        NomenclatureType.PERFUME -> ControllerMarkedProductionGroup.PERFUME
        NomenclatureType.CAMERA -> ControllerMarkedProductionGroup.CAMERA
        NomenclatureType.BICYCLE,
        NomenclatureType.BICYCLE_TRANSPORT -> ControllerMarkedProductionGroup.BICYCLE
        NomenclatureType.MEDICAL_DEVICES,
        NomenclatureType.MEDICAL_DEVICES_TRANSPORT -> ControllerMarkedProductionGroup.MEDICAL_DEVICES
        NomenclatureType.MILK,
        NomenclatureType.MILK_BLOCK,
        NomenclatureType.MILK_TRANSPORT -> ControllerMarkedProductionGroup.MILK
        NomenclatureType.WATER,
        NomenclatureType.WATER_TRANSPORT,
        NomenclatureType.WATER_GROUP -> ControllerMarkedProductionGroup.WATER
        NomenclatureType.BEER,
        NomenclatureType.NON_ALCO_BEER -> ControllerMarkedProductionGroup.BEER
        NomenclatureType.BAA_GROUP,
        NomenclatureType.BAA_TRANSPORT,
        NomenclatureType.BAA_CONSUMER -> ControllerMarkedProductionGroup.BAA
        NomenclatureType.ANTISEPTIC,
        NomenclatureType.ANTISEPTIC_GROUP,
        NomenclatureType.ANTISEPTIC_TRANSPORT -> ControllerMarkedProductionGroup.ANTISEPTIC
        NomenclatureType.FURS -> ControllerMarkedProductionGroup.FURS
        NomenclatureType.CAVIAR -> ControllerMarkedProductionGroup.CAVIAR
        NomenclatureType.CONSERVE -> ControllerMarkedProductionGroup.CONSERVE
        NomenclatureType.PETFOOD -> ControllerMarkedProductionGroup.PETFOOD
        NomenclatureType.VEGETABLEOIL -> ControllerMarkedProductionGroup.VEGETABLEOIL
        NomenclatureType.VETPHARMA -> ControllerMarkedProductionGroup.VETPHARMA
        NomenclatureType.OPTICAL_FIBER -> ControllerMarkedProductionGroup.OPTICAL_FIBER
        NomenclatureType.SOFT_DRINKS,
        NomenclatureType.SOFT_DRINKS_GROUP,
        NomenclatureType.SOFT_DRINKS_KIT,
        NomenclatureType.SOFT_DRINKS_TRANSPORT -> ControllerMarkedProductionGroup.SOFT_DRINKS
        NomenclatureType.MERCHANDISE,
        NomenclatureType.MARKED,
        NomenclatureType.ALCOHOL,
        NomenclatureType.MEDICATIONS,
        NomenclatureType.JEWELLERY,
        NomenclatureType.CABLE_PRODUCTION,
        NomenclatureType.HEATING_DEVICES,
        NomenclatureType.RADIO_ELECTRONICS,
        NomenclatureType.UNDEFINED -> null
    }
    return subAccountingType to markedProductionGroup
}

private fun ControllerMarkedProductionGroup.map(): MarkedProductionGroup {
    return when (this) {
        ControllerMarkedProductionGroup.TABACCO -> MarkedProductionGroup.TABACCO
        ControllerMarkedProductionGroup.TOBACCO_ALT -> MarkedProductionGroup.TOBACCO_ALT
        ControllerMarkedProductionGroup.SHOES -> MarkedProductionGroup.SHOES
        ControllerMarkedProductionGroup.TEXTILE -> MarkedProductionGroup.TEXTILE
        ControllerMarkedProductionGroup.TIRES -> MarkedProductionGroup.TIRES
        ControllerMarkedProductionGroup.DRUG -> MarkedProductionGroup.DRUG
        ControllerMarkedProductionGroup.PERFUME -> MarkedProductionGroup.PERFUME
        ControllerMarkedProductionGroup.CAMERA -> MarkedProductionGroup.CAMERA
        ControllerMarkedProductionGroup.BICYCLE -> MarkedProductionGroup.BICYCLE
        ControllerMarkedProductionGroup.MEDICAL_DEVICES -> MarkedProductionGroup.MEDICAL_DEVICES
        ControllerMarkedProductionGroup.MILK -> MarkedProductionGroup.MILK
        ControllerMarkedProductionGroup.WATER -> MarkedProductionGroup.WATER
        ControllerMarkedProductionGroup.INVALID -> MarkedProductionGroup.INVALID
        ControllerMarkedProductionGroup.MAX_ONLINE_VALUE -> MarkedProductionGroup.MAX_ONLINE_VALUE
        ControllerMarkedProductionGroup.NICOTINE -> MarkedProductionGroup.NICOTINE
        ControllerMarkedProductionGroup.BEER -> MarkedProductionGroup.BEER
        ControllerMarkedProductionGroup.BAA -> MarkedProductionGroup.BAA
        ControllerMarkedProductionGroup.ANTISEPTIC -> MarkedProductionGroup.ANTISEPTIC
        ControllerMarkedProductionGroup.NON_ALCO_BEER -> MarkedProductionGroup.NON_ALCO_BEER
        ControllerMarkedProductionGroup.FURS -> MarkedProductionGroup.FURS
        ControllerMarkedProductionGroup.SOFT_DRINKS -> MarkedProductionGroup.SOFT_DRINKS
        ControllerMarkedProductionGroup.CAVIAR -> MarkedProductionGroup.CAVIAR
        ControllerMarkedProductionGroup.TOBACCO_NICOTINE_RAW -> MarkedProductionGroup.TOBACCO_NICOTINE_RAW
        ControllerMarkedProductionGroup.CONSERVE -> MarkedProductionGroup.CONSERVE
        ControllerMarkedProductionGroup.PETFOOD -> MarkedProductionGroup.PETFOOD
        ControllerMarkedProductionGroup.VEGETABLEOIL -> MarkedProductionGroup.VEGETABLEOIL
        ControllerMarkedProductionGroup.VETPHARMA -> MarkedProductionGroup.VETPHARMA
        ControllerMarkedProductionGroup.OPTICAL_FIBER -> MarkedProductionGroup.OPTICAL_FIBER
        ru.tensor.sbis.catalog.generated.MarkedProductionGroup.CABLE_PRODUCTION -> MarkedProductionGroup.CABLE_PRODUCTION
        ru.tensor.sbis.catalog.generated.MarkedProductionGroup.HEATING_DEVICES -> MarkedProductionGroup.HEATING_DEVICES
        ru.tensor.sbis.catalog.generated.MarkedProductionGroup.RADIO_ELECTRONICS -> MarkedProductionGroup.RADIO_ELECTRONICS
    }
}

/**
 * Маппер подвида учета контроллера к подвиду учета на UI
 */
fun ControllerSubAccountingType.map(): SubAccounting {
    return when (this) {
        ControllerSubAccountingType.ALCO -> SubAccounting.ALCOHOLIC
        ControllerSubAccountingType.DRUG -> SubAccounting.DRUG
        ControllerSubAccountingType.JEWELLERY -> SubAccounting.JEWELLERY
        ControllerSubAccountingType.EXCISABLE -> SubAccounting.EXCISE
        ControllerSubAccountingType.MARKED -> SubAccounting.MARKED
        ControllerSubAccountingType.TOBACCO -> SubAccounting.TOBACCO
        ControllerSubAccountingType.VETERINARY -> SubAccounting.VETERINARY
        ControllerSubAccountingType.TRACEABLE_PRODUCTS -> SubAccounting.TRACEABLE_PRODUCTS
        ControllerSubAccountingType.MARKED_VETERINARY -> SubAccounting.MARKED_VETERINARY
        ControllerSubAccountingType.GRAIN -> SubAccounting.GRAIN
        ControllerSubAccountingType.NOT_USED_0,
        ControllerSubAccountingType.MAX_ONLINE_VALUE,
        ControllerSubAccountingType.INVALID -> SubAccounting.INVALID
    }
}