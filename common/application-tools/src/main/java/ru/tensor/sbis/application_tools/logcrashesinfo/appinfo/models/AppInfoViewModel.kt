package ru.tensor.sbis.application_tools.logcrashesinfo.appinfo.models

/**
 * @author du.bykov
 *
 * Модель представления крана информации о краше.
 */
class AppInfoViewModel(appDetails: Map<String, String>) {

    val appInfoRowViewModels: List<AppInfoRowViewModel> = toAppInfoRowViewModels(appDetails)

    private fun toAppInfoRowViewModels(appDetails: Map<String, String>): ArrayList<AppInfoRowViewModel> {
        val viewModels = ArrayList<AppInfoRowViewModel>()
        for (key in appDetails.keys) {
            viewModels.add(
                AppInfoRowViewModel(
                    key,
                    appDetails[key]!!
                )
            )
        }
        return viewModels
    }
}