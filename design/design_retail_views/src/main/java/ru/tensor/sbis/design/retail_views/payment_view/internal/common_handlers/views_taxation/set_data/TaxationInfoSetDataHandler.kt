package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.set_data

import android.text.SpannableString
import android.text.style.UnderlineSpan
import ru.tensor.sbis.design.retail_models.SaleTaxationSystemData
import ru.tensor.sbis.design.retail_models.TaxationSystemBlockViewModel
import ru.tensor.sbis.design.retail_views.money_view.MoneyView
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_dangerous.TaxationInfoAccessDangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_safety.TaxationInfoAccessSafetyApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.action_listeners.TaxationInfoActionListenerApi
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Реализация объекта для установки данных в "блок СНО". */
internal class TaxationInfoSetDataHandler(
    private val safetyApi: TaxationInfoAccessSafetyApi,
    private val viewAccessApi: TaxationInfoAccessDangerousApi,
    private val actionListenerApi: TaxationInfoActionListenerApi
) : TaxationInfoSetDataApi {

    override fun setTaxSystemInfo(taxationSystemInfo: TaxationSystemBlockViewModel?) {
        /* Меняем видимость блока СНО. */
        safetyApi.setTaxationSystemInfoVisibility(
            isVisible = taxationSystemInfo != null
        )

        if (taxationSystemInfo == null) {
            viewAccessApi.patentTaxSystemName.text = null
            viewAccessApi.primaryTaxSystemName.text = null
        } else {
            taxationSystemInfo
                .taxationSystemData
                .take(TaxationSystemBlockViewModel.SUPPORTED_TAX_DATA_COUNT)
                .forEach { taxInfo ->
                    if (taxInfo.isPatentCode) {
                        /* Выполняем настройку СНО Views. */
                        setupTaxationInfoViews(
                            taxInfo = taxInfo,
                            taxTotalSumView = viewAccessApi.patentTotalSum,
                            taxSystemNameView = viewAccessApi.patentTaxSystemName
                        )

                        /* Устанавливаем слушатель на нажатие "ИП". */
                        actionListenerApi.setPatentTaxationSystemClickListener {
                            actionListenerApi.onTaxationSystemClickAction?.invoke(taxInfo.taxSystemCode)
                        }
                    } else {
                        /* Выполняем настройку СНО Views. */
                        setupTaxationInfoViews(
                            taxInfo = taxInfo,
                            taxTotalSumView = viewAccessApi.primaryTotalSum,
                            taxSystemNameView = viewAccessApi.primaryTaxSystemName
                        )

                        /* Устанавливаем слушатель на нажатие "ООО". */
                        actionListenerApi.setPrimaryTaxationSystemClickListener {
                            actionListenerApi.onTaxationSystemClickAction?.invoke(taxInfo.taxSystemCode)
                        }
                    }
                }
        }
    }

    private fun setupTaxationInfoViews(
        taxInfo: SaleTaxationSystemData,
        taxTotalSumView: MoneyView,
        taxSystemNameView: SbisTextView
    ) {
        /* Кол-во средств. */
        taxTotalSumView.showMoney(taxInfo.sum)

        /* Блок с названиями СНО. */
        taxSystemNameView.text = createUnderlinedText(taxInfo.taxSystemName)
    }

    private fun createUnderlinedText(text: String) =
        SpannableString(text).apply { setSpan(UnderlineSpan(), 0, length, 0) }
}