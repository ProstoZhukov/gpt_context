package ru.tensor.sbis.toolbox_decl

import android.content.Intent

/**
 * Получатель события поступления нового [Intent].
 *
 * @author us.bessonov
 */
interface NewIntentReceiver {
    /** @SelfDocumented */
    fun onNewIntent(intent: Intent)
}