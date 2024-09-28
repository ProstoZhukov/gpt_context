package ru.tensor.sbis.formatter.currency

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Форматтер валют с учетом региона.
 *
 * @property region регион пользователя.
 *
 * @author ps.smirnyh
 */
class CurrencyFormatter internal constructor(private val region: String) {

    /**
     * Получить отформатированное значение валюты.
     *
     * @param context Контекст для получения ресурсов.
     * @param value Числовое значение денежной суммы.
     * @param mode Режим разделения целой и дробной части суммы.
     * @param size Режим отображения длины названия валюты.
     */
    fun getMoney(
        context: Context,
        value: Number,
        mode: CurrencyTranslationMode = CurrencyTranslationMode.SEPARATED,
        size: CurrencyTranslationSize = CurrencyTranslationSize.MIN
    ): CharSequence {
        val formatter = DecimalFormat().apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
            decimalFormatSymbols = DecimalFormatSymbols().apply {
                decimalSeparator = CurrencyTranslationMode.fractionSeparator
            }
        }
        val integerAndFraction = value.toString().split(CurrencyTranslationMode.fractionSeparator)
        val integer = integerAndFraction[0].toIntOrNull() ?: 0
        val fraction = integerAndFraction.getOrNull(1)?.toIntOrNull() ?: 0
        var tail = StringUtils.EMPTY
        var replaceDecimalSeparatorOnCurrency = false

        val base = if (size == CurrencyTranslationSize.SIGN) {
            StringUtils.EMPTY
        } else {
            CurrencyTranslationMode.triadDelimiter.toString()
        }
        val currency: String =
            getStringSuffix(
                context,
                value.toInt(),
                size.getCurrencySuffix(),
                base
            ) + CurrencyTranslationMode.triadDelimiter

        when (mode) {
            CurrencyTranslationMode.SPLIT -> {
                if (fraction != 0) {
                    tail = getStringSuffix(context, value.toInt(), size.getSubunitSuffix(mode), base)
                    if (integer != 0) {
                        replaceDecimalSeparatorOnCurrency = true
                    } else if (size != CurrencyTranslationSize.SIGN) {
                        formatter.minimumIntegerDigits = 0
                    }
                } else {
                    tail = currency
                }
            }

            CurrencyTranslationMode.SEPARATED -> {
                tail = currency
            }
        }

        val str = StringBuilder(formatter.format(value))
        str.trimStart { it == CurrencyTranslationMode.fractionSeparator }
        if (replaceDecimalSeparatorOnCurrency) {
            val indexSeparator = str.indexOf(CurrencyTranslationMode.fractionSeparator)
            str.replace(indexSeparator, indexSeparator + 1, currency)
        }

        return str.append(tail).toString()
    }

    @SuppressLint("DiscouragedApi")
    private fun getStringSuffix(context: Context, value: Int, resourceType: ResourceType, base: String): String {
        return when (resourceType) {
            is ResourceType.StringRes -> {
                val idRes = context.resources.getIdentifier(
                    "${resourceType.nameResource}_$region",
                    "string",
                    context.packageName
                )
                if (idRes == Resources.ID_NULL) {
                    Timber.e("Unsupported region is $region")
                    base
                } else {
                    base + context.resources.getString(idRes)
                }
            }

            is ResourceType.QuantityRes -> {
                val idRes = context.resources.getIdentifier(
                    "${resourceType.nameResource}_$region",
                    "plurals",
                    context.packageName
                )
                if (idRes == Resources.ID_NULL) {
                    Timber.e("Unsupported region is $region")
                    base
                } else {
                    base + context.resources.getQuantityString(idRes, value)
                }
            }
        }
    }
}