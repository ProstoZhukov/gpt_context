package ru.tensor.sbis.communication_decl.communicator.share

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Провайдер фичи шаринга сообщений.
 *
 * @author da.zhukov
 */
interface ShareMessagesProvider : Feature {

    /**
     * Получить фрагмент шаринга сообщений.
     */
    fun getShareMessagesFragment(sharingArgs: ShareMessagesArgs): Fragment

    /**
     * Получить интент шаринга сообщений.
     */
    fun getShareMessagesActivityIntent(context: Context, sharingArgs: ShareMessagesArgs): Intent
}