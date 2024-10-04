package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_dangerous

import android.view.View
import android.view.ViewStub
import ru.tensor.sbis.design.retail_views.databinding.RetailViewsTaxationContentBinding
import ru.tensor.sbis.design.retail_views.money_view.MoneyView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Реализация объекта для прямого доступа к View элементам "комментарий к оплате". */
@DangerousApi
internal class TaxationInfoAccessDangerousHandler(
    private val taxationViewStub: ViewStub
) : TaxationInfoAccessDangerousApi {

    override val taxationContentRootContainer: View
        get() = taxationSystemViewBinding.root

    override val primaryTaxSystemName: SbisTextView
        get() = taxationSystemViewBinding.retailViewsTaxSystemNamePrimary

    override val patentTaxSystemName: SbisTextView
        get() = taxationSystemViewBinding.retailViewsTaxSystemNamePatent

    override val primaryTotalSum: MoneyView
        get() = taxationSystemViewBinding.retailViewsTotalSumPrimary

    override val patentTotalSum: MoneyView
        get() = taxationSystemViewBinding.retailViewsTotalSumPatent

    /* Логика с 'ViewStub' - сохранена. */
    private var taxationSystemLazyInflateViewBinding: RetailViewsTaxationContentBinding? = null

    private val taxationSystemViewBinding: RetailViewsTaxationContentBinding
        get() = getOrInflateTaxationSystemBinding()

    private fun getOrInflateTaxationSystemBinding(): RetailViewsTaxationContentBinding {
        taxationSystemLazyInflateViewBinding?.let { return it }

        return RetailViewsTaxationContentBinding.bind(taxationViewStub.inflate())
            .also { inflatedView -> taxationSystemLazyInflateViewBinding = inflatedView }
    }
}