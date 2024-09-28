package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import ru.tensor.devices.settings.generated.AlcoholSaleSettings as ControllerAlcoholSaleSettings

/**
 * Модель настроек продажи алкоголя.
 *
 * @property isAllowed разрешена ли продажа алкоголя
 * @property startTime время начала продажи алкоголя
 * @property endTime время конца продажи алкоголя
 * @property alcoholWithoutEgais флаг алкоголь без ЕГАИС
 * @property enableEgaisEmulation включена ли эмуляция ЕГАИС
 * @property markingScanMode настройка "Маркировка в режиме"
 */
@Parcelize
data class AlcoholSaleSettings(
    val isAllowed: Boolean,
    val isJewelryAllowed: Boolean,
    val alcoholSaleAllowedTime: AlcoholSaleAllowedTime?,
    val alcoholWithoutEgais: Boolean,
    val enableEgaisEmulation: Boolean,
    val temporaryRestrictions: List<AlcoholSaleTemporaryRestriction>,
    val markingScanMode: MarkingScanMode?,
    val alcoInportionsWithdraw: Boolean,
    val alcoTradeOffstore: Boolean
) : Parcelable {

    companion object {

        /** @SelfDocumented */
        fun stub() = AlcoholSaleSettings(
            isAllowed = false,
            isJewelryAllowed = false,
            alcoholSaleAllowedTime = null,
            alcoholWithoutEgais = false,
            enableEgaisEmulation = false,
            temporaryRestrictions = emptyList(),
            markingScanMode = null,
            alcoInportionsWithdraw = false,
            alcoTradeOffstore = false
        )
    }
}

/** @SelfDocumented */
fun ControllerAlcoholSaleSettings.map(): AlcoholSaleSettings = AlcoholSaleSettings(
    isAllowed = isAllowed,
    isJewelryAllowed = isJewelryAllowed,
    alcoholSaleAllowedTime = allowedTime?.toAndroid(),
    alcoholWithoutEgais = alcoholWithoutEgais,
    enableEgaisEmulation = enableEgaisEmulation,
    temporaryRestrictions = temporaryRestrictions.map { it.toAndroid() },
    markingScanMode = markingScanMode?.toAndroid(),
    alcoInportionsWithdraw = alcoInportionsWithdraw,
    alcoTradeOffstore = alcoTradeOffstore
)

/** @SelfDocumented */
fun AlcoholSaleSettings.map(): ControllerAlcoholSaleSettings = ControllerAlcoholSaleSettings(
    isAllowed,
    isJewelryAllowed,
    alcoholSaleAllowedTime?.toController(),
    ArrayList(temporaryRestrictions.map { it.toController() }),
    alcoholWithoutEgais,
    enableEgaisEmulation,
    markingScanMode?.toController(),
    alcoInportionsWithdraw,
    alcoTradeOffstore
)