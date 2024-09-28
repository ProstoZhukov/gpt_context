package ru.tensor.sbis.hallscheme.v2.country_feature

import ru.tensor.sbis.design.SbisMobileIcon

/** Описание доступности фич, которые имеют региональную специфику. */
internal object CountryFeatureManager : CountryFeature {

    /* Доступ к регион-специфичным ресурсам. */
    override val resProvider: CountryFeature.ResProvider =
        object : CountryFeature.ResProvider {
            override val iconMoneyRes: SbisMobileIcon.Icon
                get() = SbisMobileIcon.Icon.smi_Ruble
        }
}