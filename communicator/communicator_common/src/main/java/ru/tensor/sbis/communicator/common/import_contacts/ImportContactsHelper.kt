package ru.tensor.sbis.communicator.common.import_contacts

import android.content.Context
import androidx.fragment.app.Fragment
import io.reactivex.Maybe
import io.reactivex.Single
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Делегат для импортирования контактов с устройства
 *
 * @author da.zhukov
 */
interface ImportContactsHelper {
    /**
     * Метод для импортирования контактов без предварительной проверки разрешений.
     * Используется для ситуаций, когда нет доступа к контексту приложения,
     * например, при синхронизации модуля коммуникатор через
     * [ru.tensor.sbis.communicator.sync.CommunicatorSyncAdapter]
     */
    @Suppress("KDocUnresolvedReference", "SpellCheckingInspection", "unused")
    fun importContactsUnsafe()

    /**
     * Метод для импортирования контактов с предварительной проверкой разрешения на чтение контактов устройства.
     * В случае отсутствия разрешений будет вызван [fallback]
     */
    @Suppress("unused")
    fun importContactsSafe(fallback: () -> Unit): Maybe<CommandStatus>

    /**
     * Запрос доступа к контактам
     */
    fun requestPermissions(fragment: Fragment)

    /**
     * Запрет запрашивать импорт контактов впредь
     */
    fun disableRequestContactPermissions(context: Context)

    /**
     * Метод для обработки результатов запроса разрешений.
     * @return CommandStatus с текстом ошибки, если требуется
     */
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray): Single<CommandStatus>

    /**
     * Метод для очистки подписок.
     */
    fun onDestroy()

    /**
     * Поставщик хелпера для импорта контактов
     */
    interface Provider : Feature {

        /**
         * @return поставщик зависимости UI обертки контроллера
         */
        val importContactsHelper: ImportContactsHelper
    }
}