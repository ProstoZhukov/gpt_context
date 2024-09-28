package ru.tensor.sbis.base_app_components.settings

import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.master_detail.MasterDetailFragment

/**
 * Хост фрагмент, отображающий фрагмент списка настроек.
 *
 * @author ma.kolpakov
 */
abstract class BaseSettingsHostFragment : MasterDetailFragment(), AdjustResizeHelper.KeyboardEventListener {

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        childFragmentManager.fragments.forEach {
            (it as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardOpenMeasure(keyboardHeight)
        }
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        childFragmentManager.fragments.forEach {
            (it as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardCloseMeasure(keyboardHeight)
        }
        return true
    }
}