package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_taxation.access_dangerous

import android.view.View
import ru.tensor.sbis.design.retail_views.money_view.MoneyView
import ru.tensor.sbis.design.retail_views.payment_view.internal.DangerousApi
import ru.tensor.sbis.design.retail_views.payment_view.internal.ViewNotExistInActivePaymentDelegate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/** Обобщение API для доступа к View элементам "блок СНО". */
interface TaxationInfoAccessDangerousApi {

    /** Получение прямого доступа к рутовому элементу "блока СНО". */
    val taxationContentRootContainer: View
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к полю "название ООО". */
    val primaryTaxSystemName: SbisTextView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к полю "название ИП". */
    val patentTaxSystemName: SbisTextView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к View "ООО: итоговая сумма". */
    val primaryTotalSum: MoneyView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get

    /** Получение прямого доступа к View "ИП: итоговая сумма". */
    val patentTotalSum: MoneyView
        @DangerousApi @Throws(ViewNotExistInActivePaymentDelegate::class) get
}