package ru.tensor.sbis.common.util

import android.app.Dialog
import android.view.View
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetBehavior
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetDialog

/**
 * Created by aa.mironychev on 06.04.2018.
 */

/**
 * Конфигурируем [CustomBottomSheetBehavior] так, чтобы фрагмент открывался сразу в полный размер.
 */
fun Dialog.skipCollapseState(): Dialog {
    // Modifying behavior of the bottomSheet - set it to expanded right away as well as forcing to skip collapsed state
    setOnShowListener {
        val view = (it as CustomBottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        view?.let {
            val behavior = CustomBottomSheetBehavior.from(view)
            behavior.state = CustomBottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }
    return this
}