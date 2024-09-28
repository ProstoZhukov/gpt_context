package ru.tensor.sbis.design.retail_models

import android.os.Parcelable
import java.math.BigDecimal
import kotlinx.parcelize.Parcelize

/**
 * Класс с информацией о бонусах операции - продажи, возврата, e.t.c;
 *
 * @property availableBonusesForDecrement сумма бонусов, доступных к списанию по операции.
 * @property totalAvailableBonuses доступный бонусный баланс операции: выражается либо бонусным балансом клиента,
 *                                 либо бонусным балансом дисконтной карты, либо суммой этих двух балансов.
 * @property bonusInc сумма бонусов, которая будет начислена когда операция будет проведена.
 * @property alreadyDecrementedBonuses сумма бонусов, списанных на данный момент.
 */
@Parcelize
data class BonusValues(
    val availableBonusesForDecrement: BigDecimal,
    val totalAvailableBonuses: BigDecimal,
    val bonusInc: BigDecimal,
    val alreadyDecrementedBonuses: BigDecimal
) : Parcelable