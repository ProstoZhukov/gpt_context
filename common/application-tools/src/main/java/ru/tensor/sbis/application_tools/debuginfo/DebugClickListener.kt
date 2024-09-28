package ru.tensor.sbis.application_tools.debuginfo

import ru.tensor.sbis.application_tools.debuginfo.model.BaseDebugInfo

/**
 * @author du.bykov
 *
 * Слушатель нажатия на элемент с дебажной информацией.
 */
interface DebugClickListener {

    /** @SelfDocumented */
    fun onDebugInfoClick(
        type: BaseDebugInfo.Type,
        crashPosition: Int
    )
}