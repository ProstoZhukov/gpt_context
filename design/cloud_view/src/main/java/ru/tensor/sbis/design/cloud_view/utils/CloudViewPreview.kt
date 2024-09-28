/**
 * @author vv.chekurda
 */
package ru.tensor.sbis.design.cloud_view.utils

import ru.tensor.sbis.design.cloud_view.CloudView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView.CloudTitleData
import ru.tensor.sbis.design.cloud_view.model.DefaultCloudViewData
import ru.tensor.sbis.design.cloud_view.model.DefaultPersonModel
import ru.tensor.sbis.design.cloud_view.model.ReceiverInfo
import ru.tensor.sbis.design.profile_decl.person.PersonData

/**
 * Показать preview для [CloudView].
 */
internal fun CloudView.showPreview() {
    isPersonal = true
    data = DefaultCloudViewData("Привет, прошу принять пункт плана")
}

/**
 * Показать preview для [CloudTitleView].
 */
internal fun CloudTitleView.showPreview() {
    data = CloudTitleData(
        author = DefaultPersonModel(PersonData(), "Соловьева К."),
        receiverInfo = ReceiverInfo(
            DefaultPersonModel(PersonData(), "Кукушкин В."),
            15
        )
    )
}