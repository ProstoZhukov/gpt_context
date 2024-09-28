package ru.tensor.sbis.hallscheme.v2.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.hallscheme.v2.business.model.tableinfo.TableInfo
import ru.tensor.sbis.hallscheme.v2.business.model.textconfig.HallSchemeTextConfig
import java.util.*

/**
 * Интерфейс для элемента схемы.
 * @author aa.gulevskiy
 */
interface HallSchemeItemDto : Parcelable {
    val id: UUID
    val cloudId: Int
    val name: String
    val coordinateX: Int
    val coordinateY: Int
    val type: Int
    val disposition: Int
    val kind: String
    val category: String?
    val sofaStyle: Int
    val z: Int
    val size: Int?
    val color: String?
    val fillColor: String?
    val width: Int?
    val height: Int?
    val placeFrom: Int?
    val placeTo: Int?
    val rowFrom: Int?
    val rowTo: Int?
    val showLeftLabel: Boolean?
    val showRightLabel: Boolean?
    val opacity: Float?
    val url: String?
    val textConfig: HallSchemeTextConfig?
    var tableInfo: TableInfo?
}

/**
 * Простая модель для элемента схемы.
 */
@Parcelize
@Suppress("unused")
class HallSchemeItemDtoImpl(
    override val id: UUID,
    override val cloudId: Int,
    override val name: String,
    override val coordinateX: Int,
    override val coordinateY: Int,
    override val type: Int,
    override val disposition: Int,
    override val kind: String,
    override val category: String? = null,
    override val sofaStyle: Int = 1,
    override val z: Int = 0,
    override val size: Int? = null,
    override val color: String? = null,
    override val fillColor: String? = null,
    override val width: Int? = null,
    override val height: Int? = null,
    override val placeFrom: Int? = null,
    override val placeTo: Int? = null,
    override val rowFrom: Int? = null,
    override val rowTo: Int? = null,
    override val showLeftLabel: Boolean? = false,
    override val showRightLabel: Boolean? = false,
    override val opacity: Float? = null,
    override val url: String? = null,
    override val textConfig: HallSchemeTextConfig? = null,
    override var tableInfo: TableInfo? = null
) : HallSchemeItemDto {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HallSchemeItemDtoImpl

        if (id != other.id || cloudId != other.cloudId) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
