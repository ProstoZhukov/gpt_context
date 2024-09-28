package ru.tensor.sbis.design.topNavigation.internal_view

/**
 * Состояние достпуности элементов шапки.
 *
 * @author da.zolotarev
 */
internal data class ChildsConfigurationState(
    var counter: Boolean = false,
    var backBtn: Boolean = false,
    var leftCustomContent: Boolean = false
) {
    companion object {
        /**
         * Создать модель со всеми доступными элементами.
         */
        fun createAllAvailableContent() = ChildsConfigurationState(
            counter = true,
            backBtn = true,
            leftCustomContent = true
        )
    }
}