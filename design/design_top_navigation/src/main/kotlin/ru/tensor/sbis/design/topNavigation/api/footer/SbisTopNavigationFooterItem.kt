package ru.tensor.sbis.design.topNavigation.api.footer

/**
 * Элемент подвала шапки.
 *
 * @author da.zolotarev
 */
data class SbisTopNavigationFooterItem(
    /** @SelfDocumented */
    val id: String = DEFAULT_ID,
    /** Контент элемента */
    val content: SbisTopNavigationFooterContent,
    /** Расположение элемента. */
    val placement: SbisTopNavigationFooterPlacement = SbisTopNavigationFooterPlacement.INSIDE_GRAPHIC_BACKGROUND,
    /** Поведение элемента при скролле. */
    val behaviour: SbisTopNavigationFooterBehavior = SbisTopNavigationFooterBehavior.FIXED
) {
    companion object {
        const val DEFAULT_ID = "DEFAULT_ID"
    }
}
