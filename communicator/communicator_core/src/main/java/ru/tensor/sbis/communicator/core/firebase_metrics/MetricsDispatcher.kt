package ru.tensor.sbis.communicator.core.firebase_metrics

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace

/**
 * Диспетчер метрик Firebase Performance модуля коммуникатор
 * для регистрации трейсов [MetricsType]
 *
 * @author vv.chekurda
 */
object MetricsDispatcher {

    private val activeTraceSpace = HashMap<String, Trace>()

    /**
     * Начать метрику
     *
     * @param type тип метрики
     */
    fun startTrace(type: MetricsType) {
        activeTraceSpace[type.tag] = FirebasePerformance.startTrace(type.tag)
    }

    /**
     * Закончить метрику
     *
     * @param type тип метрики
     */
    fun stopTrace(type: MetricsType) {
        activeTraceSpace.run {
            if (containsKey(type.tag)){
                get(type.tag)?.stop()
                remove(type.tag)
            }
        }
    }
}

/**
 * Типы метрики для различных сценариев
 * @property tag тег трейса
 */
enum class MetricsType(val tag: String) {
    /**
     * Удаление диалога
     */
    FIREBASE_DELETE_DIALOG("communicator_delete_dialog"),

    /**
     * Открытие экрана папок
     */
    @Suppress("unused")
    FIREBASE_OPEN_FOLDERS("communicator_open_folders"),

    /**
     * Прочтение непрочитанных сообщений через свайп-меню
     */
    FIREBASE_READ_DIALOG_BY_SWIPE_MENU("communicator_read_dialog"),

    /**
     * Открытие диалога
     */
    FIREBASE_OPEN_DIALOG("communicator_open_dialog"),

    /**
     * Открытие папки в реестре диалогов
     */
    FIREBASE_SHOW_FOLDER("communicator_show_folder"),

    /**
     * Загрузка первой страницы реестра контактов
     */
    FIREBASE_CONTACT_LIST_FIRST_PAGE_LOADING("contacts_first_page"),

    /**
     * Отправка с последующей отрисовкой нового сообщения.
     */
    FIREBASE_SEND_AND_GET_NEW_MESSAGE("communicator_send_and_get_new_message")
}