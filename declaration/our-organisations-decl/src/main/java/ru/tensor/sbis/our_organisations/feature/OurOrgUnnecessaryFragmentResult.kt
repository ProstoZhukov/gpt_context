package ru.tensor.sbis.our_organisations.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.our_organisations.feature.data.Organisation

/**
 * Контракт результата работы окна с не обязательным выбором нашей организации.
 *
 * @author mv.ilin
 */
sealed interface OurOrgUnnecessaryFragmentResult : Parcelable {

    /**
     * Была выбрана организация.
     *
     * @param organisations список организаций, пусто - если в множественном выборе пользователь не выбрал никакую организацию.
     */
    @Parcelize
    class OrganizationChanged(val organisations: List<Organisation>) : OurOrgUnnecessaryFragmentResult

    /**
     * Была нажата кнопка сохранить.
     *
     * @param organisations список организаций, пусто - если пользователь не выбрал никакую организацию.
     */
    @Parcelize
    class OnReturnOrganisation(val organisations: List<Organisation>) : OurOrgUnnecessaryFragmentResult

    /**
     * Был загружен весь контент и его можно показать.
     */
    @Parcelize
    object OnShowContent : OurOrgUnnecessaryFragmentResult
}