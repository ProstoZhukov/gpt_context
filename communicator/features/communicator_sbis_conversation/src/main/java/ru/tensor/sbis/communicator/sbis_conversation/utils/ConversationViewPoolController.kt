package ru.tensor.sbis.communicator.sbis_conversation.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ru.tensor.sbis.communicator.common.conversation.utils.pool.ConversationViewPoolInitializer
import ru.tensor.sbis.communicator.sbis_conversation.DialogCreationActivity

/**
 * Класс для хранения и управления пулом экрана реестра сообщений.
 *
 * @author vv.chekurda
 */
internal class ConversationViewPoolController : ConversationViewPoolInitializer, LifecycleObserver {

    private var conversationViewPoolsHolder: ConversationViewPool? = null
    private var newDialogViewPoolsHolder: ConversationViewPool? = null

    override fun initViewPool(fragment: Fragment) {
        val pool = conversationViewPoolsHolder
            ?: createViewPoolsHolder(fragment.requireContext())
                .also { conversationViewPoolsHolder = it }
        pool.prepareViewPools()
        fragment.lifecycle.run {
            addObserver(this@ConversationViewPoolController)
            addObserver(pool)
        }
    }

    /**
     * Проинициализировать пулы view реестра сообщений для нового диалога.
     *
     * @param activity активити, которая запрашивает инициализацию.
     */
    fun initNewDialogViewPool(activity: AppCompatActivity) {
        createViewPoolsHolder(activity).let { pool ->
            newDialogViewPoolsHolder = pool
            activity.lifecycle.addObserver(pool)
        }
    }

    /**
     * Получить холдер пулов view реестра сообщений.
     */
    fun getViewPoolsHolder(context: Context): ConversationViewPool =
        conversationViewPoolsHolder?.tryToGet(context)
            ?: newDialogViewPoolsHolder.takeIf { context is DialogCreationActivity }
            ?: createViewPoolsHolder(context)

    /**
     * Очистить холдеров пулов view реестра сообщений для нового диалога.
     */
    fun clearNewDialogViewPoolsHolder() {
        newDialogViewPoolsHolder = null
    }

    private fun createViewPoolsHolder(context: Context): ConversationViewPool =
        ConversationViewPool(context)

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        conversationViewPoolsHolder = null
    }
}