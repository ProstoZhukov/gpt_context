package ru.tensor.sbis.link_share.ui

import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams
import ru.tensor.sbis.plugin_struct.feature.Feature

/**@SelfDocumented*/
interface LinkShareFragmentProvider : Feature {

    /**
     * Функция получения ContentCreator
     *
     * @return новый ContentCreator, содержащий в себе Fragment
     *
     * @see ContentCreatorParcelable
     */
    fun getLinkShareContentCreator(params: SbisLinkShareParams): ContentCreatorParcelable
}