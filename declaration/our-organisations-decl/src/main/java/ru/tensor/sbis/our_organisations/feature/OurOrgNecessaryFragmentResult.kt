package ru.tensor.sbis.our_organisations.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.our_organisations.feature.data.Organisation

/**
 * Контракт результата работы окна с обязательным выбором нашей организации.
 *
 * @author mv.ilin
 */
sealed interface OurOrgNecessaryFragmentResult : Parcelable {

    /**
     * Была выбрана организация.
     *
     * @param organisation организация.
     */
    @Parcelize
    class OrganizationChanged(val organisation: Organisation) : OurOrgNecessaryFragmentResult

    /**
     * Был загружен весь контент и его можно показать.
     */
    @Parcelize
    object OnShowContent : OurOrgNecessaryFragmentResult
}