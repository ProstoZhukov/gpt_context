package ru.tensor.sbis.order_message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.common_catalog_items_decl.models.cart.CartItem
import ru.tensor.sbis.queue_list_decl.models.Queue
import java.math.BigDecimal
import java.util.UUID

/**
 * Номенклатура по позициям
 *
 * @param nomenclatures список номенклатуры
 */
sealed class OrderMessageNomenclatures(
    open val nomenclatures: List<Nomenclature> = arrayListOf(),
) : Parcelable {

    /**
     * Номенклатура по организации
     *
     * @property companyName список номенклатуры
     */
    @Parcelize
    data class CompanyNomenclatures(
        override val nomenclatures: List<Nomenclature>,
        val companyName: String,
    ) : OrderMessageNomenclatures(nomenclatures)

    /**
     * Номенклатура по очереди
     *
     * @param queue список номенклатуры
     */
    @Parcelize
    data class QueueNomenclatures(
        override val nomenclatures: List<Nomenclature>,
        val queue: Queue
    ) : OrderMessageNomenclatures(nomenclatures)

    /**
     * Номенклатуры больше недоступные
     *
     * @property isSalon является ли салоном
     */
    @Parcelize
    data class NomenclaturesNotPublished(
        override val nomenclatures: List<Nomenclature>,
        val isSalon: Boolean = false
    ) : OrderMessageNomenclatures(nomenclatures)

    /**
     * Номенклатуры с измененнными ценами
     *
     * @property isSalon является ли салоном
     */
    @Parcelize
    data class NomenclaturesChangedPrice(
        override val nomenclatures: List<Nomenclature>,
        val isSalon: Boolean = false
    ) : OrderMessageNomenclatures(nomenclatures)

    /**
     * Номенклатура в стоп листах
     */
    @Parcelize
    data class StopListNomenclature(
        override val nomenclatures: List<Nomenclature>
    ) : OrderMessageNomenclatures(nomenclatures)

    /**
     * Номенклатура товары с разным налогообложением
     */
    @Parcelize
    data class DifferentNomTaxes(
        override val nomenclatures: List<Nomenclature>
    ) : OrderMessageNomenclatures(nomenclatures)
}

/**@SelfDocumented*/
@Parcelize
data class Nomenclature(
    val uuid: UUID,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val packName: String,
    val duration: Int,
    val availableQuantity: Int = 0
) : Parcelable

/**@SelfDocumented*/
fun CartItem.mapToNomenclature(availableQuantity: Int = 0, newPrice: BigDecimal? = null): Nomenclature = Nomenclature(
    uuid = uuid,
    name = name,
    price = (newPrice ?: price).toDouble(),
    imageUrl = imageUrl,
    packName = pack?.packUnit?.name ?: "",
    duration = duration,
    availableQuantity = availableQuantity
)
