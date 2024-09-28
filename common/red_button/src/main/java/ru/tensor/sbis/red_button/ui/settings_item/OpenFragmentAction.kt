package ru.tensor.sbis.red_button.ui.settings_item

import android.content.Intent
import android.content.res.Resources
import ru.tensor.sbis.red_button.utils.RedButtonOpenHelper
import ru.tensor.sbis.settings_screen_decl.Delegate

/**
 * Действие результатом которого будет показ фрагмента "красной кнопки" через делегат.
 *
 * @author du.bykov
 */
internal class OpenFragmentAction(private val openHelper: RedButtonOpenHelper) :
        (Resources, Delegate, Intent?) -> Unit {

    override fun invoke(p1: Resources, delegate: Delegate, intent: Intent?) {
        delegate.showFragment(getFragment = { openHelper.getRedButtonHost() })
    }
}