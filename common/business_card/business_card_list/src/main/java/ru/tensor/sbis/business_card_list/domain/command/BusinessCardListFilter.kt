package ru.tensor.sbis.business_card_list.domain.command

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.business.card.mobile.generated.BusinessCardFilter
import ru.tensor.sbis.common.util.UUIDUtils.NIL_UUID
import java.util.UUID

/**
 * Фильтр списка визиток
 * @param personUuid Идентификатор персоны, если null - берётся ид текущей авторизованной персоны
 */
@Parcelize
data class BusinessCardListFilter(val personUuid: UUID? = NIL_UUID) : Parcelable

/**
 * Маппер для преобразование вью модели в модель контроллера
 */
fun BusinessCardListFilter.map(): BusinessCardFilter = BusinessCardFilter(personUuid)