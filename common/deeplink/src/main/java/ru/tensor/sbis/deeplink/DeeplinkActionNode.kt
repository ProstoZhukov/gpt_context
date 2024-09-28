package ru.tensor.sbis.deeplink

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * Выполняет действия внешних ссылок.
 */
interface DeeplinkActionNode {

    /**
     * Обрабатывает действие внешней ссылки
     * @param args армументы действия
     */
    fun onNewDeeplinkAction(args: DeeplinkAction)

    companion object {
        // Ключ аргумента действия [Intent]
        const val EXTRA_DEEPLINK_ACTION = "EXTRA_DEEPLINK_ACTION"

        // Ключ аргумента действия для [Bundle]
        internal const val ARG_DEEPLINK_ACTION = "ARG_DEEPLINK_ACTION"

        /**
         * Выполняет действие внешней ссылки с использованием аргмументов
         * @param fragment фрагмент, на котором должно выполниться действие
         */
        @JvmStatic
        fun performNewDeeplinkAction(fragment: Fragment) {
            if (fragment !is DeeplinkActionNode) throw IllegalArgumentException("$this isn't implement ${DeeplinkActionNode::javaClass}")
            fragment.arguments?.run {
                val args = getDeeplinkAction<DeeplinkAction>(this)
                remove(ARG_DEEPLINK_ACTION)
                fragment.onNewDeeplinkAction(args ?: return@run)
            }
        }

        /**
         * Вносит действие внешней ссылки в [Bundle]
         * @param bundle целевой [Bundle]
         * @param action объект [SerializableDeeplinkAction]
         */
        @JvmStatic
        fun putNewDeeplinkActionToArgsIfNotNull(bundle: Bundle, action: SerializableDeeplinkAction?) {
            bundle.putSerializable(ARG_DEEPLINK_ACTION, action ?: return)
        }

        /**
         * Вносит действие внешней ссылки в [Bundle]
         * @param bundle целевой [Bundle]
         * @param action объект [SerializableDeeplinkAction]
         */
        @JvmStatic
        fun putNewDeeplinkActionToArgsIfNotNull(bundle: Bundle, action: ParcelableDeeplinkAction?) {
            bundle.putParcelable(ARG_DEEPLINK_ACTION, action ?: return)
        }

        /**
         * Возвращает действие внешней ссылки
         * @param from источник [Bundle]
         * @return action объект [DeeplinkAction] нужного типа, null если привести не удалось или объект не найден
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T> getDeeplinkAction(from: Bundle?): T? where T: DeeplinkAction {
            var result: T? = getSerializableDeeplinkAction<SerializableDeeplinkAction>(from) as? T
            if(result == null) {
                result = getParcelableDeeplinkAction<ParcelableDeeplinkAction>(from) as? T
            }
            return result
        }

        /**
         * Возвращает действие внешней ссылки
         * @param from источник [Bundle]
         * @return action объект [ParcelableDeeplinkAction] нужного типа, null если привести не удалось или объект не найден
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T> getParcelableDeeplinkAction(from: Bundle?): T? where T: ParcelableDeeplinkAction =
            from?.getParcelable(ARG_DEEPLINK_ACTION) as? T

        /**
         * Возвращает действие внешней ссылки
         * @param from источник [Bundle]
         * @return action объект [ParcelableDeeplinkAction] нужного типа, null если привести не удалось или объект не найден
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T> getSerializableDeeplinkAction(from: Bundle?): T? where T: SerializableDeeplinkAction =
            from?.getSerializable(ARG_DEEPLINK_ACTION) as? T

        /**
         * Возвращает действие внешней ссылки
         * @param intent источник [Intent]
         * @return action объект [DeeplinkAction] нужного типа, null если привести не удалось или объект не найден
         */
        inline fun <reified T> getDeeplinkAction(intent: Intent): T? where T: DeeplinkAction? {
            return intent.extras
                ?.takeIf { it.containsKey(EXTRA_DEEPLINK_ACTION) }
                ?.let {
                    it.getSerializable(EXTRA_DEEPLINK_ACTION) as? T
                        ?: it.getParcelable<ParcelableDeeplinkAction>(EXTRA_DEEPLINK_ACTION) as? T
                }
        }
    }
}