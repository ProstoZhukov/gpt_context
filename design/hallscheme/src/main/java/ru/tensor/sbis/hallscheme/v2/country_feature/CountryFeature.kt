package ru.tensor.sbis.hallscheme.v2.country_feature

import ru.tensor.sbis.design.SbisMobileIcon

/** Описание доступности фич, которые имеют региональную специфику. */
internal interface CountryFeature {

    /** Доступ к провайдеру регион-специфичных ресурсов. */
    val resProvider: ResProvider

    /** Провайдер регион-специфичных ресурсов. */
    interface ResProvider {
        /** Иконка валюты. */
        val iconMoneyRes: SbisMobileIcon.Icon
    }
}