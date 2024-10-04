package ru.tensor.sbis.hallscheme.v2.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.hallscheme.v2.business.model.Background
import ru.tensor.sbis.hallscheme.v2.business.model.tableconfig.TableConfig

/**
 * Модель схемы зала.
 * @author aa.gulevskiy
 */
@Parcelize
data class HallSchemeModelDto(
    val planTheme: String,
    val background: Background,
    val backgroundRoom: String? = null,
    val top: Int? = null,
    val left: Int? = null,
    val bottom: Int? = null,
    val right: Int? = null,
    val pinTables: Boolean = true,
    val items: List<HallSchemeItemDto>,
    val opacity: Float? = null,
    val textureType: Int, // тип текстуры в диапазоне [0..8]
    val tableConfig: TableConfig, // Настройки отображения информации на столе
    val zoom: Int = 100, // дефолтное значение в процентах
    val zoomBackground: Int = 100  // дефолтное значение в процентах
) : Parcelable