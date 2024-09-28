package ru.tensor.sbis.catalog_decl.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Модель данных тип номенклатуры
 *
 * @author sp.lomakin
 */
@Parcelize
data class NomenclatureTypeData(
    val id: UUID?,
    val isAlcohol: Boolean?,
    val snControl: Boolean?,
    val subAccounting: SubAccounting?,
    val flags: Long?,
    val categoryType: CategoryType?
) : Parcelable {

    /**
     *  Полный учет серийных номеров
     */
    val fullSnControl: Boolean
        get() = Signs.FULL_SN_CONTROL.contains(flags)
}

/**
 *
 *  https://wi.sbis.ru/docs/db/%D0%9D%D0%BE%D0%BC%D0%B5%D0%BD%D0%BA%D0%BB%D0%B0%D1%82%D1%83%D1%80%D0%B0/%D0%A2%D0%B8%D0%BF%D0%9D%D0%BE%D0%BC%D0%B5%D0%BD%D0%BA%D0%BB%D0%B0%D1%82%D1%83%D1%80%D1%8B/options/%D0%9F%D1%80%D0%B8%D0%B7%D0%BD%D0%B0%D0%BA%D0%B8/?v=20.5000
 *
 *  Признаки
 *  ТИП: Flags
 *
 *  10: Разделение наименований при полном учете серийных номеров
 *  8: Проверять количество серийных номеров
 *  7: Полный учет серийных номеров
 *  6: Цена устанавливается на партию
 *  5: ЕНВД не применим
 *  4: Учет срока годности
 *  3: Учет серийных номеров
 *  11: Ввод сумм акциза в реализации
 *  9: Подбор партии по серийным номерам
 *  12: Автоматический подбор партии по коду
 */
enum class Signs(private val value: Long) {
    FULL_SN_CONTROL(1 shl 7);

    fun contains(mask: Long?): Boolean {
        return mask?.let { (it and value) != 0L } ?: false
    }
}

/**
 * https://wi.sbis.ru/docs/db/%D0%9D%D0%BE%D0%BC%D0%B5%D0%BD%D0%BA%D0%BB%D0%B0%D1%82%D1%83%D1%80%D0%B0/%D0%A2%D0%B8%D0%BF%D0%9D%D0%BE%D0%BC%D0%B5%D0%BD%D0%BA%D0%BB%D0%B0%D1%82%D1%83%D1%80%D1%8B/options/%D0%9F%D0%BE%D0%B4%D0%B2%D0%B8%D0%B4%D0%A3%D1%87%D0%B5%D1%82%D0%B0/?v=20.5000
 *
 *  ПодвидУчета
 */
enum class SubAccounting {
    /* 1 - алкогольный */
    ALCOHOLIC,
    /* 2 - ветеринарный */
    VETERINARY,
    /* 3 - лекарственный */
    DRUG,
    /* 4 - подакцизный */
    EXCISE,
    /* 5 - табачный (устарело) */
    /* для создания табака необходимо использовать [SubAccounting.MARKED] и [MarkedProductionGroup.TABACCO] */
    TOBACCO,
    /* 6 - маркированный */
    MARKED,
    /* 7 - маркированная ветеринария */
    MARKED_VETERINARY,
    /* 8 - прослеживаемая продукция */
    TRACEABLE_PRODUCTS,
    /* 9 - ювелирные изделия */
    JEWELLERY,
    /* 10 - зерновая продукция */
    GRAIN,
    /*  невалидное значение */
    INVALID;
}